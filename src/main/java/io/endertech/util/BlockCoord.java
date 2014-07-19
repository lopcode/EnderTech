package io.endertech.util;

import net.minecraft.world.ChunkCoordIntPair;

public class BlockCoord implements Comparable
{
    public int x;
    public int y;
    public int z;

    public BlockCoord(int x, int y, int z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public boolean equals(Object o)
    {
        if (!(o instanceof BlockCoord)) return false;
        if (o == this) return true;
        BlockCoord blockCoord = (BlockCoord) o;
        return (blockCoord.x == this.x && blockCoord.y == this.y && blockCoord.z == this.z);
    }

    public boolean equals(int x, int y, int z)
    {
        return (x == this.x && y == this.y && z == this.z);
    }

    @Override
    public int compareTo(Object o)
    {
        if (o instanceof BlockCoord)
        {
            BlockCoord other = (BlockCoord) o;
            if (this.x < other.x) { return -1; } else if (this.x > other.x) { return 1; } else if (this.y < other.y)
            {
                return -1;
            } else if (this.y > other.y) { return 1; } else if (this.z < other.z)
            {
                return -1;
            } else if (this.z > other.z) { return 1; } else { return 0; }
        }
        return 0;
    }

    public int compareTo(int xCoord, int yCoord, int zCoord)
    {
        if (this.x < xCoord) { return -1; } else if (this.x > xCoord) { return 1; } else if (this.y < yCoord)
        {
            return -1;
        } else if (this.y > yCoord) { return 1; } else if (this.z < zCoord) { return -1; } else if (this.z > zCoord)
        {
            return 1;
        } else { return 0; }
    }

    public int getChunkX() { return x >> 4; }

    public int getChunkZ() { return z >> 4; }

    public long getChunkXZHash() { return ChunkCoordIntPair.chunkXZ2Int(x >> 4, z >> 4); }

    public BlockCoord copy()
    {
        return new BlockCoord(x, y, z);
    }

    public void copy(BlockCoord other)
    {
        this.x = other.x;
        this.y = other.y;
        this.z = other.z;
    }

}
