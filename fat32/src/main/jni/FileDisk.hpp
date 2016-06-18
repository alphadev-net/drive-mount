#pragma once

#include <iostream>
#include <string>
#include <fstream>
#include <memory>
#include <limits>

#include "Disk.hpp"

class FileDisk : public Disk
{
public:
    FileDisk(const std::string &filename, size_t sectorSize = MinBytesPerSector);
    FileDisk(std::fstream &file, size_t sectorCount, size_t sectorSize);

    void writeSector(size_t sector, void *buffer) override;
    void readSector(size_t sector, void *buffer) override;

    size_t getSectorCount() const override;
    size_t getSectorSize() const override;

    static std::shared_ptr<Disk> create(const std::string &filename, size_t sectorCount, size_t sectorSize = MinBytesPerSector);

private:
    std::fstream m_file;
};
