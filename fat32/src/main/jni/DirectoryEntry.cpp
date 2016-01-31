#include "DirectoryEntry.hpp"
#include "IFat32Directory.hpp"

DirectoryEntry::DirectoryEntry(std::weak_ptr<Fat32Disk> fat32, std::weak_ptr<IFat32Directory> parent, int parentPosition, Fat32DirectoryEntry entry)
{
    m_fat32 = fat32;
    m_parent = parent;
    m_parentPosition = parentPosition;
    m_entry = entry;
    m_name = std::string(m_entry.name, strnlen(m_entry.name, FatNameLength));
}

const std::string& DirectoryEntry::getName() const
{
    return m_name;
}

char DirectoryEntry::getAttributes() const
{
    return m_entry.attrib;
}

size_t DirectoryEntry::getSize() const
{
    return m_entry.size;
}

void DirectoryEntry::save() const
{
    auto parent = m_parent.lock();
    if (!parent)
        throw std::runtime_error(FatDirectoryFreedError);

    parent->update(*this);
}
