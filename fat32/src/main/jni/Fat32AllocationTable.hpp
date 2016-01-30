#pragma once

#include <memory>
#include "Fat32Common.hpp"

class Fat32Disk;

class Fat32AllocationTable
{

public:

    Fat32AllocationTable(Fat32Disk* fat32);
    ~Fat32AllocationTable();

    FatCluster read(FatCluster index);
    void write(FatCluster index, FatCluster value);
    FatCluster alloc();
    void free(FatCluster index);
    void reset();

private:

    Fat32Disk *m_fat32;

    bool m_cacheDirty;
    size_t m_cachedSector;
    std::unique_ptr<FatCluster[]> m_cache;
    size_t m_entryCount;

    FatCluster findFree(FatCluster startCluster);
    void flush();
    size_t getFatSector(FatCluster index);
    size_t getFatSectorOffset(FatCluster index);
};
