package io.endertech.network;

// Derived from CoFH's PacketTile

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

public class PacketTile extends PacketETBase
{
    public PacketTile() { }

    public PacketTile(TileEntity theTile)
    {
        addInt(theTile.xCoord);
        addInt(theTile.yCoord);
        addInt(theTile.zCoord);
    }

    public static void init()
    {
        PacketHandler.instance.registerPacket(PacketTile.class);
    }

    public static PacketTile newPacket(TileEntity theTile)
    {
        return new PacketTile(theTile);
    }

    @Override
    public void handleClientSide(EntityPlayer player)
    {
        handlePacket(player, false);
    }

    @Override
    public void handleServerSide(EntityPlayer player)
    {
        handlePacket(player, true);
    }

    @Override
    public void handlePacket(EntityPlayer player, boolean isServer)
    {
        TileEntity tile = player.worldObj.getTileEntity(getInt(), getInt(), getInt());

        if (tile instanceof ITilePacketHandler)
        {
            ((ITilePacketHandler) tile).handleTilePacket(this, isServer);
        } else
        {
            // TODO: Throw error, bad packet
        }
    }
}

