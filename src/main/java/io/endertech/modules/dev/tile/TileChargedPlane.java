package io.endertech.modules.dev.tile;

import cofh.api.energy.IEnergyHandler;
import cofh.api.tileentity.IReconfigurableFacing;
import cofh.lib.util.helpers.ServerHelper;
import cpw.mods.fml.common.registry.GameRegistry;
import io.endertech.network.PacketETBase;
import io.endertech.reference.Strings;
import io.endertech.tile.TileET;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

public class TileChargedPlane extends TileET implements IReconfigurableFacing, IEnergyHandler
{
    public short ticksSinceLastChargeEvent = -1;
    public static final short TICKS_PER_CHARGE_MINIMUM = 20 * 3;
    public static final short TICKS_PER_UPDATE = 20;
    public short ticksSinceLastUpdate = 0;
    public boolean isActive = false;
    public boolean gotEnergyLastTick = false;

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
            this.isActive = this.gotEnergyLastTick;

            if (this.ticksSinceLastChargeEvent == TICKS_PER_CHARGE_MINIMUM)
            {
                this.ticksSinceLastChargeEvent = 0;
            }

            if (this.ticksSinceLastUpdate == TICKS_PER_UPDATE)
            {
                this.ticksSinceLastUpdate = 0;
                this.sendDescriptionPacket();
            }

            this.ticksSinceLastChargeEvent++;
            if (this.ticksSinceLastChargeEvent > TICKS_PER_CHARGE_MINIMUM)
                this.ticksSinceLastChargeEvent = TICKS_PER_CHARGE_MINIMUM;

            this.ticksSinceLastUpdate++;
            if (this.ticksSinceLastUpdate > TICKS_PER_UPDATE) this.ticksSinceLastUpdate = TICKS_PER_UPDATE;

            this.gotEnergyLastTick = false;
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

    @Override
    public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate)
    {
        boolean canReceive = from != this.getOrientation();
        if (!simulate)
        {
            gotEnergyLastTick = canReceive;
        }
        if (canReceive) return 1;
        else return 0;
    }

    @Override
    public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate)
    {
        return 0;
    }

    @Override
    public int getEnergyStored(ForgeDirection from)
    {
        return 0;
    }

    @Override
    public int getMaxEnergyStored(ForgeDirection from)
    {
        return 1;
    }

    @Override
    public boolean canConnectEnergy(ForgeDirection from)
    {
        return from != this.getOrientation();
    }
}
