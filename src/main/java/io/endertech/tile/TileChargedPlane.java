package io.endertech.tile;

import cpw.mods.fml.common.registry.GameRegistry;
import io.endertech.network.NetworkHandler;
import io.endertech.network.message.MessageTileChargedPlane;
import io.endertech.reference.Strings;
import io.endertech.util.LogHelper;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;

public class TileChargedPlane extends TileET
{
    public static void init()
    {
        GameRegistry.registerTileEntity(TileChargedPlane.class, "tile." + Strings.Blocks.CHARGED_PLANE_NAME);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbtTagCompound)
    {
        super.readFromNBT(nbtTagCompound);

        LogHelper.info("Reading charged plane NBT: " + this.toString());
    }

    @Override
    public void writeToNBT(NBTTagCompound nbtTagCompound)
    {
        super.writeToNBT(nbtTagCompound);

        LogHelper.info("Writing charged plane NBT: " + this.toString());
    }

    @Override
    public Packet getDescriptionPacket()
    {
        return NetworkHandler.INSTANCE.getPacketFrom(new MessageTileChargedPlane(this));
    }

    @Override
    public String toString()
    {
        return "Charged plane: position " + this.xCoord + ", " + this.yCoord + ", " + this.zCoord;
    }
}
