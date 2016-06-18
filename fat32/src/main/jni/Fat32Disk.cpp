#include "Disk.hpp"
#include "Fat32Disk.hpp"
#include "Fat32Directory.hpp"
#include "Fat32File.hpp"
#include "Fat32AllocationTable.hpp"
#include "Fat32Common.hpp"

const char *FatDiskFreedError = "Fat32Disk instance was freed";

Fat32Disk::Fat32Disk(std::shared_ptr<Disk> disk)
    : m_disk(disk), m_fat(loadBpbBeforeFat())
{

    auto name = std::string(m_bpb.fsysName, strnlen(m_bpb.fsysName, sizeof(Fat32Bpb::fsysName)));
    if (name != "NSFAT32")
        throw std::runtime_error("not a nsfat32 disk");

    m_zeroCluster = std::make_unique<char[]>(getClusterSize());
    std::memset(m_zeroCluster.get(), 0, getClusterSize());
}

Fat32Disk *Fat32Disk::loadBpbBeforeFat()
{
    auto buffer = std::make_unique<char[]>(m_disk->getSectorSize());
    m_disk->readSector(0, buffer.get());
    m_bpb = *(Fat32Bpb*)buffer.get();

    return this;
}

std::shared_ptr<Disk> Fat32Disk::getDisk() const
{
    return m_disk;
}

size_t Fat32Disk::getClusterSize() const
{
    return m_bpb.sectorsPerCluster * m_bpb.bytesPerSector;
}

size_t Fat32Disk::getClusterCount() const
{
    return m_bpb.totalSectors / m_bpb.sectorsPerCluster;
}

void Fat32Disk::readCluster(FatCluster cluster, char *buffer)
{
    if (cluster >= getClusterCount())
        throw std::runtime_error("cluster out of range");

    int clusterOffset = m_bpb.reservedSectors + m_bpb.fatSize;
    int sector = clusterOffset + (cluster * m_bpb.sectorsPerCluster);

    for (int i = 0; i < m_bpb.sectorsPerCluster; i++)
    {
        m_disk->readSector(sector++, buffer);
        buffer += m_bpb.bytesPerSector;
    }
}

void Fat32Disk::writeCluster(FatCluster cluster, char *buffer)
{
    if (cluster >= getClusterCount())
        throw std::runtime_error("cluster out of range");

    int clusterOffset = m_bpb.reservedSectors + m_bpb.fatSize;
    int sector = clusterOffset + (cluster * m_bpb.sectorsPerCluster);

    for (int i = 0; i < m_bpb.sectorsPerCluster; i++)
    {
        m_disk->writeSector(sector++, buffer);
        buffer += m_bpb.bytesPerSector;
    }
}

void Fat32Disk::zeroCluster(FatCluster cluster)
{
    writeCluster(cluster, m_zeroCluster.get());
}

std::shared_ptr<IFat32Directory> Fat32Disk::root()
{
    if (!m_root)
    {
        m_root = std::make_shared<Fat32Root>(shared_from_this());
    }

    return m_root;
}

void Fat32Disk::format(std::shared_ptr<Disk> disk, const std::string &volumeLabel, size_t sectorsPerCluster)
{
    if (volumeLabel.length() > 16)
        throw std::runtime_error("invalid volume label");

    auto bytesPerCluster = disk->getSectorSize() * sectorsPerCluster;

    if (sectorsPerCluster < 1 || sectorsPerCluster > 255)
        throw std::runtime_error("invalid sectors per cluster");

    Fat32Bpb bpb = {};

    const std::string nsfat32 = "NSFAT32";

    std::copy(nsfat32.begin(), nsfat32.end(), bpb.fsysName);
    bpb.fsysVersion = 0;

    bpb.bytesPerSector = (uint16_t)disk->getSectorSize();
    bpb.totalSectors = (uint32_t)disk->getSectorCount();
    bpb.reservedSectors = 1; // the bpb

    bpb.sectorsPerCluster = (uint8_t)sectorsPerCluster;

    auto totalClusters = bpb.totalSectors / bpb.sectorsPerCluster;
    bpb.fatSize = (totalClusters * sizeof(FatCluster)) / bpb.bytesPerSector; // TODO: fatSize is too large with this method as it includes the fat in the calculation

    bpb.rootCluster = 0;

    std::copy(volumeLabel.begin(), volumeLabel.end(), bpb.label);

    auto buffer = std::make_unique<char[]>(bpb.bytesPerSector);
    std::memcpy(buffer.get(), &bpb, sizeof(Fat32Bpb));
    disk->writeSector(0, buffer.get());

    auto fatEntriesPerSector = bpb.bytesPerSector / sizeof(FatCluster);
    auto fatBuffer = std::make_unique<FatCluster[]>(fatEntriesPerSector);

    for (size_t i = 0; i < fatEntriesPerSector; i++)
        fatBuffer[i] = FatFree;

    for (size_t i = 0; i < bpb.fatSize; i++)
    {
        if (i == 0)
            fatBuffer[0] = FatEof;

        disk->writeSector(bpb.reservedSectors + i, fatBuffer.get());

        if (i == 0)
            fatBuffer[0] = FatFree;
    }
}
