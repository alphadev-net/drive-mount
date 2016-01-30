#pragma once

#include <string>
#include "Fat32Common.hpp"
#include "Fat32File.hpp"

class Fat32Disk;
class IFat32Directory;

class DirectoryEntry
{
    friend class Fat32Root;
    friend class IFat32Directory;
    friend class Fat32File;

public:

    const std::string& getName() const;
    char getAttributes() const;
    size_t getSize() const;

private:

    DirectoryEntry(std::weak_ptr<Fat32Disk> fat32, std::weak_ptr<IFat32Directory> parent, int parentPosition, Fat32DirectoryEntry entry);

    void save() const;

    std::weak_ptr<Fat32Disk> m_fat32;
    std::weak_ptr<IFat32Directory> m_parent;
    int m_parentPosition;
    Fat32DirectoryEntry m_entry;
    std::string m_name;

};
