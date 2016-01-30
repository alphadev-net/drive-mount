#pragma once

#include <iostream>
#include <string>
#include <fstream>
#include <memory>
#include <limits>

const size_t MinBytesPerSector = 512;
const size_t MaxBytesPerSector = 4096;

class Disk
{

public:

    Disk(const std::string &filename, size_t sectorSize = MinBytesPerSector);
    Disk(std::fstream &file, size_t sectorCount, size_t sectorSize);

    void writeSector(size_t sector, void *buffer);
    void readSector(size_t sector, void *buffer);

    size_t getSectorCount() const;
    size_t getSectorSize() const;

    static std::shared_ptr<Disk> create(const std::string &filename, size_t sectorCount, size_t sectorSize = MinBytesPerSector);

private:

    std::fstream m_file;

    size_t m_sectorCount;
    size_t m_sectorSize;
};
