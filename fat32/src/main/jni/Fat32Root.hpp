#pragma once

#include "Disk.hpp"
#include "Fat32File.hpp"
#include "Fat32Directory.hpp"
#include "Fat32Disk.hpp"
#include "Interface/IFat32Directory.hpp"

class Fat32Root : public IFat32Directory
{

public:

    Fat32Root(std::weak_ptr<Fat32Disk> fat32);
    Fat32Root(Fat32Root &&other);

protected:

    virtual void initialize();

private:

    std::weak_ptr<Fat32Disk> m_fat32;

};
