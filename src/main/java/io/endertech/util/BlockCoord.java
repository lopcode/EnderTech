package io.endertech.util;

public class BlockCoord
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

}
