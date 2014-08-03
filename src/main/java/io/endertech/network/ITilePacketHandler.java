package io.endertech.network;

// Derived from CoFH's ITilePacketHandler

public interface ITilePacketHandler
{
    public void handleTilePacket(PacketETBase payload, boolean isServer);
}
