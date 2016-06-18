#pragma once

#include "Disk.hpp"
#include "../../../../api/src/main/jni/blockdevice.h"
#include <memory>

class BlockDeviceDisk : Disk
{
public:
    explicit BlockDeviceDisk(const BlockDevice& device);

    void writeSector(size_t sector, void *buffer) override;
    void readSector(size_t sector, void *buffer) override;

    size_t getSectorCount() const;
    size_t getSectorSize() const;
};