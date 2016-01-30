#include "Disk.hpp"
#include "Fat32AllocationTable.hpp"
#include "Fat32Disk.hpp"

Fat32AllocationTable::Fat32AllocationTable(Fat32Disk *fat32)
    : m_fat32(fat32)
{
    m_cacheDirty = false;
    m_cachedSector = -1;

    auto entriesPerSector = fat32->getDisk()->getSectorSize() / sizeof(FatCluster);
    m_cache = std::make_unique<FatCluster[]>(entriesPerSector);
    m_entryCount = m_fat32->m_bpb.fatSize * entriesPerSector;
}

Fat32AllocationTable::~Fat32AllocationTable()
{
    flush();
}

FatCluster Fat32AllocationTable::read(FatCluster index)
{
    if (index >= m_entryCount)
        throw std::runtime_error("FAT index out of range");

    auto sector = getFatSector(index);
    auto offset = getFatSectorOffset(index);
    auto disk = m_fat32->getDisk();

    if (sector != m_cachedSector)
    {
        flush();
        disk->readSector(sector, m_cache.get());
        m_cachedSector = sector;
    }

    return m_cache[offset];
}

void Fat32AllocationTable::write(FatCluster index, FatCluster value)
{
    if (index >= m_entryCount)
        throw std::runtime_error("FAT index out of range");

    auto sector = getFatSector(index);
    auto offset = getFatSectorOffset(index);
    auto disk = m_fat32->getDisk();

    if (sector != m_cachedSector)
    {
        flush();
        disk->readSector(sector, m_cache.get());
        m_cachedSector = sector;
    }

    m_cache[offset] = value;
    m_cacheDirty = true;
}

FatCluster Fat32AllocationTable::alloc()
{
    auto index = findFree(0);
    write(index, FatUnassign);
    return index;
}

void Fat32AllocationTable::free(FatCluster index)
{
    write(index, FatFree);
}

void Fat32AllocationTable::reset()
{
    flush();
    m_cachedSector = FatFree;
}

FatCluster Fat32AllocationTable::findFree(FatCluster startCluster)
{
    auto lastCluster = m_fat32->getClusterCount();
    auto cluster = startCluster;

    while (true)
    {
        if (cluster >= lastCluster)
            throw std::runtime_error("disk is full");

        auto value = read(cluster);
        if (value == FatFree)
            return cluster;

        cluster++;
    }
}

void Fat32AllocationTable::flush()
{
    if (!m_cacheDirty)
        return;

    m_fat32->getDisk()->writeSector(m_cachedSector, m_cache.get());
    m_cacheDirty = false;
}

size_t Fat32AllocationTable::getFatSector(FatCluster index)
{
    auto fatOffset = m_fat32->m_bpb.reservedSectors;
    return fatOffset + (index / (m_fat32->m_bpb.bytesPerSector / sizeof(FatCluster)));
}

size_t Fat32AllocationTable::getFatSectorOffset(FatCluster index)
{
    return index % (m_fat32->m_bpb.bytesPerSector / sizeof(FatCluster));
}
