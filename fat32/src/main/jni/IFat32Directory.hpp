#pragma once

#include <memory>
#include <string>
#include <unordered_map>

#include "Fat32Common.hpp"
#include "DirectoryEntry.hpp"

extern const char *FatDirectoryFreedError;

class Fat32Disk;
class Fat32Directory;
class Fat32File;

class IFat32Directory : public std::enable_shared_from_this<IFat32Directory>
{
    friend class DirectoryEntry;

public:

    IFat32Directory(std::weak_ptr<Fat32Disk> fat32);
    IFat32Directory(IFat32Directory &&other);

    std::vector<std::shared_ptr<DirectoryEntry>> entries();

    std::shared_ptr<IFat32Directory> up();
    std::shared_ptr<IFat32Directory> directory(const std::string &name);
    Fat32File file(const std::string &name);

    bool add(const std::string &name, FatAttrib attributes);
    bool remove(const std::string &name);
    bool exists(const std::string &name);

    static bool isValidName(const std::string &name);

protected:

    std::shared_ptr<DirectoryEntry> m_entry;

    virtual void initialize();

private:

    std::weak_ptr<Fat32Disk> m_fat32;
    std::unordered_map<std::string, std::shared_ptr<DirectoryEntry>> m_entries;

    void checkInitialized();
    void update(const DirectoryEntry &entry);

};
