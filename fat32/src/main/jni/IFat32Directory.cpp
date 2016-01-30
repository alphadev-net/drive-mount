#include "IFat32Directory.hpp"

#include "Fat32Common.hpp"
#include "Fat32Directory.hpp"
#include "Fat32File.hpp"
#include "Fat32Disk.hpp"

const char *FatDirectoryFreedError = "IFat32Directory instance was freed";

IFat32Directory::IFat32Directory(std::weak_ptr<Fat32Disk> fat32)
{
    m_fat32 = fat32;
    m_entry = std::shared_ptr<DirectoryEntry>();
}

IFat32Directory::IFat32Directory(IFat32Directory &&other)
{
    m_fat32 = std::move(other.m_fat32);
    m_entry = std::move(other.m_entry);

    m_entries = std::move(other.m_entries);
}

void IFat32Directory::checkInitialized()
{
    if (!m_entry)
        initialize();
}

void IFat32Directory::initialize()
{
    if (!m_entry)
        throw std::runtime_error("directory did not set entry");

    m_entries.clear();

    Fat32File file(m_fat32, m_entry);
    Fat32DirectoryEntry entry;

    while (true)
    {
        int entryPosition = file.tell();

        if (file.read((char*)&entry, sizeof(Fat32DirectoryEntry)) != sizeof(Fat32DirectoryEntry))
            break;

        if (entry.name[0] == 0x00) // stop
            break;

        if (entry.name[0] == (char)0x01) // free
            continue;

        auto dirEntry = std::shared_ptr<DirectoryEntry>(new DirectoryEntry(m_fat32, shared_from_this(), entryPosition, entry));
        m_entries.insert(std::make_pair(dirEntry->getName(), dirEntry));
    }
}

std::vector<std::shared_ptr<DirectoryEntry>> IFat32Directory::entries()
{
    checkInitialized();

    std::vector<std::shared_ptr<DirectoryEntry>> result;

    for (auto &e : m_entries)
    {
        result.push_back(e.second);
    }

    return result;
}

std::shared_ptr<IFat32Directory> IFat32Directory::up()
{
    checkInitialized();

    auto &parent = m_entry->m_parent.lock();
    if (!parent)
        throw std::runtime_error(FatDirectoryFreedError);

    return parent;
}

std::shared_ptr<IFat32Directory> IFat32Directory::directory(const std::string &name)
{
    checkInitialized();

    auto item = m_entries.find(name);
    if (item == m_entries.end())
        throw std::runtime_error("entry doesn't exist");

    if ((item->second->getAttributes() & (char)FatAttrib::Directory) == 0)
        throw std::runtime_error("not a directory");

    auto firstCluster = item->second->m_entry.firstCluster;

    return m_fat32.lock()->getOrAddDirectory<Fat32Directory>(firstCluster, [&]() { return Fat32Directory(m_fat32, item->second); });
}

Fat32File IFat32Directory::file(const std::string &name)
{
    checkInitialized();

    auto item = m_entries.find(name);
    if (item == m_entries.end())
        throw std::runtime_error("entry doesn't exist");

    if ((item->second->getAttributes() & (char)FatAttrib::Directory) != 0)
        throw std::runtime_error("not a file");

    return Fat32File(m_fat32, item->second);
}

bool IFat32Directory::add(const std::string &name, FatAttrib attributes)
{
    checkInitialized();

    if (!isValidName(name))
        return false;

    if (exists(name))
        return false;

    Fat32File file(m_fat32, m_entry);
    Fat32DirectoryEntry entry;
    int entryPosition;

    // seek to first free entry
    while (true)
    {
        entryPosition = file.tell();

        if (file.read((char*)&entry, sizeof(Fat32DirectoryEntry)) != sizeof(Fat32DirectoryEntry))
            break;

        if (entry.name[0] == 0x00) // stop
            break;

        if (entry.name[0] == (char)0x01) // free
            break;
    }

    // write new entry
    entry = {};
    std::copy(name.begin(), name.end(), entry.name);
    entry.attrib = (char)attributes;
    entry.size = 0;
    entry.firstCluster = FatEof;

    file.seek(entryPosition); // just in case of a partial read
    file.write((char*)&entry, sizeof(Fat32DirectoryEntry));

    // add to entry list
    auto dirEntry = std::shared_ptr<DirectoryEntry>(new DirectoryEntry(m_fat32, shared_from_this(), entryPosition, entry));
    m_entries.insert(std::make_pair(dirEntry->getName(), dirEntry));

    return true;
}

bool IFat32Directory::remove(const std::string &name)
{
    checkInitialized();

    auto item = m_entries.find(name);
    if (item == m_entries.end())
        return false;

    auto &entry = item->second;

    if (entry->getAttributes() & (char)FatAttrib::Directory)
    {
        // need to recursively remove
        auto dir = directory(name);

        for (auto &e : dir->entries())
        {
            dir->remove(e->getName());
        }
    }

    // need to free clusters
    auto &fat = m_fat32.lock()->m_fat;
    int cluster = entry->m_entry.firstCluster;

    while (cluster < FatEof)
    {
        int nextCluster = fat.read(cluster);
        fat.free(cluster);
        cluster = nextCluster;
    }

    // mark the entry as free
    entry->m_entry.name[0] = 0x01;
    entry->save();

    m_entries.erase(item);

    return true;
}

bool IFat32Directory::exists(const std::string &name)
{
    checkInitialized();

    auto item = m_entries.find(name);
    return item != m_entries.end();
}

void IFat32Directory::update(const DirectoryEntry &entry)
{
    // TODO: defer this to dtor
    Fat32File file(m_fat32, m_entry);
    file.seek(entry.m_parentPosition);
    file.write((char*)&entry.m_entry, sizeof(Fat32DirectoryEntry));
}

const char illegalChars[] =
{
    '"', '*', '/', ':', '<', '>', '?', '\\',
    '|', 127, '+', ',', ';', '=', '[', ']'
};

bool IFat32Directory::isValidName(const std::string &name)
{
    if (name.length() > FatNameLength)
        return false;

    for (const char &ch : name)
    {
        for (const char &illegal : illegalChars)
        {
            if (ch == illegal || ch <= 31)
                return false;
        }
    }

    return true;
}
