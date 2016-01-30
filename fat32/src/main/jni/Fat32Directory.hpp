#pragma once

#include "Interface/IFat32Directory.hpp"
#include "Fat32Common.hpp"

#include <string>

class Fat32Disk;

class Fat32Directory : public IFat32Directory
{

public:

    Fat32Directory(std::weak_ptr<Fat32Disk> fat32, std::shared_ptr<DirectoryEntry> entry); // internal
    Fat32Directory(Fat32Directory &&other);

protected:

    virtual void initialize();

private:

    std::weak_ptr<Fat32Disk> m_fat32;
    std::shared_ptr<DirectoryEntry> m_entry;

};
