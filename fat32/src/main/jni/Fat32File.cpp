#include "Fat32File.hpp"
#include "Disk.hpp"
#include "Fat32Common.hpp"
#include "Fat32Disk.hpp"
#include "Fat32AllocationTable.hpp"

Fat32File::Fat32File(std::weak_ptr<Fat32Disk> fat32, std::shared_ptr<DirectoryEntry> entry)
    : m_fat32(fat32), m_entry(entry)
{
    m_entryDirty = false;

    if (m_entry)
    {
        m_firstCluster = entry->m_entry.firstCluster;
        m_size = entry->getSize();
    }

    auto fat32Disk = m_fat32.lock();
    if (!fat32Disk)
        throw std::runtime_error(FatDiskFreedError);

    m_clusterSize = fat32Disk->getClusterSize();
    m_originalSize = m_size;

    m_buffer = std::make_unique<char[]>(m_clusterSize);
    std::memset(m_buffer.get(), 0, m_clusterSize);

    m_cluster = FatEof;
    m_clusterPosition = -1;
    m_clusterOffset = -1;
    m_clusterDirty = false;

    m_position = 0;
}

Fat32File::Fat32File(Fat32File &&other)
{
    m_fat32 = std::move(other.m_fat32);

    m_entry = std::move(other.m_entry);
    m_entryDirty = other.m_entryDirty;

    m_firstCluster = other.m_firstCluster;
    m_clusterSize = other.m_clusterSize;
    m_size = other.m_size;
    m_originalSize = other.m_originalSize;

    m_buffer = std::move(other.m_buffer);
    m_cluster = other.m_cluster;
    m_clusterPosition = other.m_clusterPosition;
    m_clusterOffset = other.m_clusterOffset;
    m_clusterDirty = other.m_clusterDirty;

    m_position = other.m_position;
}

Fat32File::~Fat32File()
{
    flush();

    if (m_size < m_originalSize)
    {
        auto fat32Disk = m_fat32.lock();
        if (!fat32Disk)
            throw std::runtime_error(FatDiskFreedError);

        auto clusterSize = fat32Disk->getClusterSize();

        // original size in clusters
        auto originalSizeClusters = (m_originalSize + clusterSize - 1) / clusterSize;

        // current size in clusters
        auto sizeClusters = (m_size + clusterSize - 1) / clusterSize;

        if (sizeClusters >= originalSizeClusters)
            return;

        seek(sizeClusters * clusterSize);

        if (!checkSeekToPosition(false))
            throw std::runtime_error("failed to shrink file (seek 1)");

        auto &fat = fat32Disk->m_fat;

        if (sizeClusters == 0)
        {
            m_firstCluster = FatEof;
            m_entryDirty = true;

            flush();
        }
        else
        {
            auto temp = m_cluster;

            // find the new last cluster
            seek((sizeClusters * clusterSize) - 1);

            if (!checkSeekToPosition(false))
                throw std::runtime_error("failed to shrink file (seek 2)");

            // mark it as eof
            fat.write(m_cluster, FatEof);

            m_cluster = temp;
        }

        // free excess clusters
        auto next = fat.read(m_cluster);
        fat.free(m_cluster);

        while (next <= FatEof)
        {
            auto cur = next;
            next = fat.read(cur);
            fat.free(cur);
        }
    }
}

void Fat32File::flush()
{
    if (m_clusterDirty)
    {
        auto fat32Disk = m_fat32.lock();
        if (!fat32Disk)
            throw std::runtime_error(FatDiskFreedError);

        fat32Disk->writeCluster(m_cluster, m_buffer.get());
        m_clusterDirty = false;
    }

    if (m_entry && m_entryDirty)
    {
        m_entry->m_entry.size = m_size;
        m_entry->m_entry.firstCluster = m_firstCluster;
        m_entry->save();
        m_entryDirty = false;
    }
}

void Fat32File::seek(size_t position)
{
    if (position < 0)
        throw std::runtime_error("can't seek to negative position");

    m_position = position;
}

size_t Fat32File::tell() const
{
    return m_position;
}

size_t Fat32File::read(char *buffer, size_t count)
{
    if (eof())
        return 0;

    if (!checkSeekToPosition())
        return 0;

    size_t bytesRead = 0;
    while (bytesRead < count)
    {
        if (eof() || !checkNextCluster())
            return bytesRead;

        *buffer++ = m_buffer[m_clusterOffset++];
        m_position++;
        bytesRead++;
    }

    return bytesRead;
}

void Fat32File::write(const char *buffer, size_t count)
{
    checkSeekToPosition(true);

    size_t bytesWritten = 0;
    while (bytesWritten < count)
    {
        checkNextCluster(true);

        m_buffer[m_clusterOffset++] = *buffer++;
        m_clusterDirty = true;
        m_position++;
        bytesWritten++;
    }

    if (m_position >= m_size)
    {
        m_size = m_position;
        m_entryDirty = true;
    }
}

bool Fat32File::eof() const
{
    return m_position >= m_size;
}

void Fat32File::truncate(size_t length)
{
    if (length == m_size)
        return;

    flush();

    auto oldSize = m_size;

    m_size = length;
    m_entryDirty = true;

    if (length <= oldSize)
        return; // shrinking is done in destructor

    // extend file
    auto originalPosition = tell();

    seek(length - 1);
    checkSeekToPosition(true); // allocates all the clusters

    seek(originalPosition);
}

// switches to the cluster at m_position if needed
// returns false if eof && !alloc
bool Fat32File::checkSeekToPosition(bool alloc)
{
    auto clusterPositionOffset = m_clusterPosition + m_clusterOffset;
    if (m_position == clusterPositionOffset)
        return true;

    flush();

    auto positionIndex = m_position / m_clusterSize;
    auto clusterIndex = clusterPositionOffset / m_clusterSize;

    if (m_position >= 0 && m_position < m_clusterSize) // moved to first cluster
    {
        if (!checkHasCluster(alloc))
            return false;

        m_cluster = m_firstCluster;
        m_clusterPosition = 0;
        m_clusterOffset = m_position;
    }
    else
    {
        if (m_position < clusterPositionOffset) // moved before current cluster, need to start over
        {
            if (!checkHasCluster(alloc))
                return false;

            m_cluster = m_firstCluster;
            m_clusterPosition = 0;
        }

        auto targetClusterPosition = positionIndex * m_clusterSize;

        while (m_clusterPosition != targetClusterPosition)
        {
            m_clusterOffset = m_clusterSize;
            checkNextCluster(alloc, false);
        }

        m_clusterOffset = m_position % m_clusterSize;
    }

    auto fat32Disk = m_fat32.lock();
    if (!fat32Disk)
        throw std::runtime_error(FatDiskFreedError);

    fat32Disk->readCluster(m_cluster, m_buffer.get());

    return true;
}

// switches to the next cluster if needed
// returns false if eof && !alloc
bool Fat32File::checkNextCluster(bool alloc, bool read)
{
    auto fat32Disk = m_fat32.lock();
    if (!fat32Disk)
        throw std::runtime_error(FatDiskFreedError);

    if (m_clusterOffset < m_clusterSize)
        return true;

    flush();

    auto currentCluster = m_cluster;

    if (m_firstCluster >= FatEof)
        throw std::runtime_error("first cluster is invalid in checkNextCluster");

    if (m_cluster >= FatEof)
        m_cluster = m_firstCluster;
    else
        m_cluster = fat32Disk->m_fat.read(m_cluster);

    if (m_cluster == FatBad)
        throw std::runtime_error("bad cluster in chain");

    if (m_cluster == FatUnassign)
        throw std::runtime_error("unassigned cluster in chain");

    if (m_cluster == FatFree)
        throw std::runtime_error("free cluster in chain");

    if (m_cluster == FatEof)
    {
        if (!alloc)
            return false;

        auto &fat = fat32Disk->m_fat;
        auto nextCluster = fat.alloc();
        fat.write(currentCluster, nextCluster);
        fat.write(nextCluster, FatEof);

        fat32Disk->zeroCluster(nextCluster);

        m_cluster = nextCluster;
    }

    m_clusterPosition += m_clusterSize;
    m_clusterOffset = 0;

    if (read)
        fat32Disk->readCluster(m_cluster, m_buffer.get());

    return true;
}

// allocates the first cluster if needed, to only be used by checkSeekToPosition
// returns false if eof && !alloc
bool Fat32File::checkHasCluster(bool alloc)
{
    if (m_firstCluster != FatEof)
        return true;

    if (!alloc)
        return false;

    if (!m_entry)
        throw std::runtime_error("tried to allocate on an empty entry-less file");

    auto fat32Disk = m_fat32.lock();
    if (!fat32Disk)
        throw std::runtime_error(FatDiskFreedError);

    m_firstCluster = fat32Disk->m_fat.alloc();
    fat32Disk->m_fat.write(m_firstCluster, FatEof);

    fat32Disk->zeroCluster(m_firstCluster);

    m_entryDirty = true;

    return true;
}
