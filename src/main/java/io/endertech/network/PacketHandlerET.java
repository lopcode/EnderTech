package io.endertech.network;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;
import io.endertech.network.packet.PacketET;
import io.endertech.network.packet.ProtocolException;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;

public class PacketHandlerET implements IPacketHandler
{
    @Override
    public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player)
    {
        ByteArrayDataInput in = ByteStreams.newDataInput(packet.data);
        int packetId = in.readUnsignedByte();

        try
        {
            PacketET packetET = PacketET.constructPacket(packetId);
            packetET.read(in);
            packetET.execute(manager, player);
        } catch (ProtocolException e)
        {
            throw new RuntimeException("Failed to construct packet!", e);
        } catch (ReflectiveOperationException e)
        {
            throw new RuntimeException("Failed to construct packet!", e);
        }
    }
}
