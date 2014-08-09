package io.endertech.modules.dev.tile;

import cofh.api.tileentity.IReconfigurableFacing;
import cofh.lib.util.helpers.ServerHelper;
import cpw.mods.fml.common.registry.GameRegistry;
import io.endertech.network.PacketETBase;
import io.endertech.reference.Strings;
import io.endertech.tile.TileET;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import java.util.Random;

public class TileChargedPlane extends TileET implements IReconfigurableFacing
{
    public short ticksSinceLastChargeEvent = -1;
    public static final short TICKS_PER_CHARGE_MINIMUM = 20 * 3;
    public boolean isActive = false;

    public static void init()
    {
        GameRegistry.registerTileEntity(TileChargedPlane.class, "tile.endertech." + Strings.Blocks.CHARGED_PLANE_NAME);
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
        packet.addBool(this.isActive);

        return packet;
    }


    @Override
    public void handleTilePacket(PacketETBase tilePacket, boolean isServer)
    {
        super.handleTilePacket(tilePacket, isServer);

        short ticksSinceLastChargeEvent = tilePacket.getShort();
        boolean isActive = tilePacket.getBool();
        if (!isServer)
        {
            this.ticksSinceLastChargeEvent = ticksSinceLastChargeEvent;
            this.isActive = isActive;
        }
    }

    @Override
    public void updateEntity()
    {
        if (ServerHelper.isServerWorld(this.worldObj))
        {
            if (this.ticksSinceLastChargeEvent == TICKS_PER_CHARGE_MINIMUM)
            {
                //LogHelper.info("Minimum met for " + this.toString());
                this.ticksSinceLastChargeEvent = 0;
                this.isActive = new Random().nextBoolean();
                this.sendDescriptionPacket();
            }

            this.ticksSinceLastChargeEvent++;
            if (this.ticksSinceLastChargeEvent > TICKS_PER_CHARGE_MINIMUM)
                this.ticksSinceLastChargeEvent = TICKS_PER_CHARGE_MINIMUM;
        }
    }

    @Override
    public int getFacing()
    {
        return this.getOrientation().ordinal();
    }

    @Override
    public boolean allowYAxisFacing()
    {
        return true;
    }

    @Override
    public boolean rotateBlock()
    {
        int orientation = this.getFacing();
        orientation++;
        if (orientation >= ForgeDirection.VALID_DIRECTIONS.length) orientation = 0;

        return this.setFacing(orientation);
    }

    @Override
    public boolean setFacing(int side)
    {
        if (side == this.getOrientation().ordinal()) return false;
        else
        {
            this.setOrientation(side);
            this.sendDescriptionPacket();
            return true;
        }
    }
}
