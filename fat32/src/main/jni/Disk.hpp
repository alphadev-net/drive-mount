#pragma once

#include <memory>

const size_t MinBytesPerSector = 512;
const size_t MaxBytesPerSector = 4096;

class Disk
{
public:
    Disk(size_t sectorSize = MinBytesPerSector)
        : m_sectorSize(sectorSize){
    };

    virtual void writeSector(size_t sector, void *buffer) = 0;
    virtual void readSector(size_t sector, void *buffer) = 0;
    virtual size_t getSectorCount() const = 0;
    virtual size_t getSectorSize() const = 0;

protected:
    size_t m_sectorCount;
    size_t m_sectorSize;
};
typedef std::shared_ptr<Disk> DiskPtr;
