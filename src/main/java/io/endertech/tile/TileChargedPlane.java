package io.endertech.tile;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import io.endertech.network.NetworkHandler;
import io.endertech.network.message.MessageTileUpdate;
import io.endertech.reference.Strings;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.tileentity.TileEntity;

public class TileChargedPlane extends TileET implements IMessageHandler<TileChargedPlane.MessageTileChargedPlaneUpdate, IMessage>
{
    public static void init()
    {
        GameRegistry.registerTileEntity(TileChargedPlane.class, "tile." + Strings.Blocks.CHARGED_PLANE_NAME);
        NetworkHandler.INSTANCE.registerMessage(TileChargedPlane.class, TileChargedPlane.MessageTileChargedPlaneUpdate.class, NetworkHandler.getDiscriminator(), Side.CLIENT);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbtTagCompound)
    {
        super.readFromNBT(nbtTagCompound);

        //        LogHelper.info("Reading charged plane NBT: " + this.toString());
    }

    @Override
    public void writeToNBT(NBTTagCompound nbtTagCompound)
    {
        super.writeToNBT(nbtTagCompound);

        //        LogHelper.info("Writing charged plane NBT: " + this.toString());
    }

    @Override
    public Packet getDescriptionPacket()
    {
        return NetworkHandler.INSTANCE.getPacketFrom(new MessageTileChargedPlaneUpdate(this));
    }

    @Override
    public String toString()
    {
        return "Charged plane: position " + this.xCoord + ", " + this.yCoord + ", " + this.zCoord;
    }

    @Override
    public IMessage onMessage(MessageTileChargedPlaneUpdate message, MessageContext ctx)
    {
        TileEntity tileEntity = FMLClientHandler.instance().getClient().theWorld.getTileEntity(message.x, message.y, message.z);

        if (tileEntity instanceof TileChargedPlane)
        {
            //            LogHelper.info("Setting charged plane data from message: " + tileEntity.toString());
        }

        return null;
    }

    public static class MessageTileChargedPlaneUpdate extends MessageTileUpdate
    {
        public MessageTileChargedPlaneUpdate() { }

        public MessageTileChargedPlaneUpdate(TileChargedPlane tileChargedPlane)
        {
            super(tileChargedPlane);
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            super.fromBytes(buf);
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            super.toBytes(buf);
        }
    }
}
