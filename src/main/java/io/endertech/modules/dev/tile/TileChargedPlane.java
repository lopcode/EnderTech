package io.endertech.modules.dev.tile;

import cofh.lib.util.helpers.ServerHelper;
import cpw.mods.fml.common.registry.GameRegistry;
import io.endertech.network.PacketETBase;
import io.endertech.reference.Strings;
import io.endertech.tile.TileET;
import io.endertech.util.helper.LogHelper;
import net.minecraft.nbt.NBTTagCompound;

public class TileChargedPlane extends TileET
{
    public short ticksSinceLastChargeEvent = -1;
    public static final short TICKS_PER_CHARGE_MINIMUM = 20 * 30;

    public static void init()
    {
        GameRegistry.registerTileEntity(TileChargedPlane.class, "tile." + Strings.Blocks.CHARGED_PLANE_NAME);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbtTagCompound)
    {
        super.readFromNBT(nbtTagCompound);

        if (nbtTagCompound.hasKey("ticksSinceLastChargeEvent"))
        {
            this.ticksSinceLastChargeEvent = nbtTagCompound.getShort("ticksSinceLastChargeEvent");
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbtTagCompound)
    {
        super.writeToNBT(nbtTagCompound);

        nbtTagCompound.setShort("ticksSinceLastChargeEvent", this.ticksSinceLastChargeEvent);
    }

    @Override
    public String toString()
    {
        return "Charged plane: position " + this.xCoord + ", " + this.yCoord + ", " + this.zCoord;
    }

    @Override
    public PacketETBase getPacket()
    {
        PacketETBase packet = super.getPacket();
        packet.addShort(this.ticksSinceLastChargeEvent);

        return packet;
    }


    @Override
    public void handleTilePacket(PacketETBase tilePacket, boolean isServer)
    {
        super.handleTilePacket(tilePacket, isServer);

        short ticksSinceLastChargeEvent = tilePacket.getShort();
        if (!isServer)
        {
            this.ticksSinceLastChargeEvent = ticksSinceLastChargeEvent;
        }
    }

    @Override
    public void updateEntity()
    {
        if (ServerHelper.isServerWorld(this.worldObj))
        {
            if (this.ticksSinceLastChargeEvent == TICKS_PER_CHARGE_MINIMUM)
            {
                LogHelper.info("Minimum met for " + this.toString());
                this.ticksSinceLastChargeEvent = 0;
                this.sendDescriptionPacket();
            }

            this.ticksSinceLastChargeEvent++;
            if (this.ticksSinceLastChargeEvent > TICKS_PER_CHARGE_MINIMUM)
                this.ticksSinceLastChargeEvent = TICKS_PER_CHARGE_MINIMUM;
        }
    }
}
