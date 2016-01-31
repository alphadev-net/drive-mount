#pragma once

#include <functional>
#include <unordered_map>
#include <unordered_set>
#include "Disk.hpp"
#include "Fat32Common.hpp"
#include "Fat32AllocationTable.hpp"
#include "Fat32Root.hpp"
#include "DirectoryEntry.hpp"

extern const char *FatDiskFreedError;

class DirectoryEntry;
class Fat32File;
class Disk;

class Fat32Disk : public std::enable_shared_from_this<Fat32Disk>
{
    friend class Fat32AllocationTable;
    friend class IFat32Directory;
    friend class Fat32File;
    friend class Fat32Root;

public:

    Fat32Disk(std::shared_ptr<Disk> disk);

    std::shared_ptr<Disk> getDisk() const;

    size_t getClusterSize() const;
    size_t getClusterCount() const;
    void readCluster(FatCluster cluster, char *buffer);
    void writeCluster(FatCluster cluster, char *buffer);
    void zeroCluster(FatCluster cluster);

    std::shared_ptr<IFat32Directory> root();

    static void format(std::shared_ptr<Disk> disk, const std::string &volumeLabel, size_t sectorsPerCluster = 1);

private:

    std::shared_ptr<Disk> m_disk;
    Fat32Bpb m_bpb;
    Fat32AllocationTable m_fat;
    std::shared_ptr<Fat32Root> m_root;

    std::unique_ptr<char[]> m_zeroCluster;

    std::unordered_map<size_t, std::weak_ptr<IFat32Directory>> m_directories;

    Fat32Disk *loadBpbBeforeFat();

    template<typename T>
    std::shared_ptr<IFat32Directory> getOrAddDirectory(FatCluster firstCluster, std::function<T()> ctor)
    {
        auto item = m_directories.find(firstCluster);
        std::shared_ptr<T> result;

        if (item == m_directories.end())
        {
            result = std::make_shared<T>(ctor());
            m_directories.insert(std::make_pair(firstCluster, result));
        }
        else
        {
            std::shared_ptr<IFat32Directory> existing = item->second.lock();
            if (existing)
            {
                return existing;
            }
            else
            {
                result = std::make_shared<T>(ctor());
                item->second = result;
            }
        }

        return result;
    }

};
