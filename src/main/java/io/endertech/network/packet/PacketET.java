package io.endertech.network.packet;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import io.endertech.lib.Reference;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;

public abstract class PacketET
{
    private static final BiMap<Integer, Class<? extends PacketET>> idMap;

    static
    {
        ImmutableBiMap.Builder<Integer, Class<? extends PacketET>> packetMapBuilder = ImmutableBiMap.builder();
        packetMapBuilder.put(0, PacketKeyPressed.class);
        idMap = packetMapBuilder.build();
    }

    public static PacketET constructPacket(int packetId) throws ProtocolException, ReflectiveOperationException
    {
        Class<? extends PacketET> clazz = idMap.get(packetId);
        if (clazz == null)
        {
            throw new ProtocolException("Unknown packet ID: " + packetId);
        }
        else
        {
            return clazz.newInstance();
        }
    }

    public final int getPacketId()
    {
        if (idMap.inverse().containsKey(getClass()))
        {
            return idMap.inverse().get(getClass()).intValue();
        }
        else
        {
            throw new RuntimeException("Unknown packet mapping: " + getClass().getSimpleName());
        }
    }

    public final Packet makePacket()
    {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeByte(this.getPacketId());
        this.write(out);

        return PacketDispatcher.getPacket(Reference.MOD_ID, out.toByteArray());
    }


    public abstract void write(ByteArrayDataOutput output);

    public abstract void read(ByteArrayDataInput input) throws ProtocolException;

    public abstract void execute(INetworkManager networkManager, Player player) throws ProtocolException;
}
