#pragma once

#include <memory>
#include "Fat32Common.hpp"
#include "DirectoryEntry.hpp"

class Fat32;
class Fat32Disk;
class DirectoryEntry;

class Fat32File
{

public:

    Fat32File(std::weak_ptr<Fat32Disk> fat32, std::shared_ptr<DirectoryEntry> entry); // internal
    Fat32File(Fat32File &&other);
    ~Fat32File();

    void flush();
    void seek(size_t position);
    size_t tell() const;

    size_t read(char *buffer, size_t count);
    void write(const char *buffer, size_t count);

    bool eof() const;

    void truncate(size_t length = 0);

private:

    std::weak_ptr<Fat32Disk> m_fat32;

    std::shared_ptr<DirectoryEntry> m_entry;
    bool m_entryDirty;

    size_t m_firstCluster;
    size_t m_clusterSize;
    size_t m_originalSize;
    size_t m_size;

    std::unique_ptr<char[]> m_buffer;
    size_t m_cluster;
    size_t m_clusterPosition;
    size_t m_clusterOffset;
    bool m_clusterDirty;

    size_t m_position;

    bool checkSeekToPosition(bool alloc = false);
    bool checkNextCluster(bool alloc = false, bool read = true);
    bool checkHasCluster(bool alloc = false);

};
