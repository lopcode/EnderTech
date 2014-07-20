package io.endertech.network.message;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.endertech.tile.TileET;
import io.netty.buffer.ByteBuf;

public class MessageTileUpdate implements IMessage
{
    public int x, y, z;

    public MessageTileUpdate() { }

    public MessageTileUpdate(TileET tile)
    {
        this.x = tile.xCoord;
        this.y = tile.yCoord;
        this.z = tile.zCoord;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.x = buf.readInt();
        this.y = buf.readInt();
        this.z = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(this.x);
        buf.writeInt(this.y);
        buf.writeInt(this.z);
    }
}
