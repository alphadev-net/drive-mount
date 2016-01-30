#include "IFat32Directory.hpp"
#include "Fat32Directory.hpp"
#include "Fat32Root.hpp"
#include "Fat32File.hpp"

#include <algorithm>
#include <string>

Fat32Directory::Fat32Directory(std::weak_ptr<Fat32Disk> fat32, std::shared_ptr<DirectoryEntry> entry)
    : IFat32Directory(fat32)
{
    m_fat32 = fat32;
    m_entry = entry;
}

Fat32Directory::Fat32Directory(Fat32Directory &&other)
    : IFat32Directory(std::move(other))
{
    m_fat32 = std::move(other.m_fat32);
    m_entry = std::move(other.m_entry);
}

void Fat32Directory::initialize()
{
    IFat32Directory::m_entry = std::move(m_entry);
    IFat32Directory::initialize();
}
