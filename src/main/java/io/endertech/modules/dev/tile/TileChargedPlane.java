package io.endertech.modules.dev.tile;

import cofh.api.energy.IEnergyHandler;
import cofh.api.tileentity.IReconfigurableFacing;
import cofh.lib.util.helpers.ServerHelper;
import cpw.mods.fml.common.registry.GameRegistry;
import io.endertech.modules.dev.fluid.DevETFluids;
import io.endertech.network.PacketETBase;
import io.endertech.reference.Strings;
import io.endertech.tile.TileET;
import io.endertech.util.BlockCoord;
import io.endertech.util.Geometry;
import io.endertech.util.helper.LogHelper;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.IFluidBlock;
import java.util.List;
import java.util.Set;

public class TileChargedPlane extends TileET implements IReconfigurableFacing, IEnergyHandler
{
    public short ticksSinceLastChargeEvent = -1;
    public static final short TICKS_PER_CHARGE_MINIMUM = 20 * 3;
    public static final short TICKS_PER_UPDATE = 20;
    public static final short TICKS_PER_ENDER_CHECK = 20 * 5;
    public short ticksSinceLastUpdate = 0;
    public short ticksSinceLastEnderCheck = TICKS_PER_ENDER_CHECK;
    public boolean isActive = false;
    public boolean nearEnderSource = false;
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
        packet.addBool(this.gotEnergyLastTick);
        packet.addBool(this.nearEnderSource);

        return packet;
    }


    @Override
    public void handleTilePacket(PacketETBase tilePacket, boolean isServer)
    {
        super.handleTilePacket(tilePacket, isServer);

        short ticksSinceLastChargeEvent = tilePacket.getShort();
        boolean isActive = tilePacket.getBool();
        boolean gotEnergyLastTick = tilePacket.getBool();
        boolean nearEnderSource = tilePacket.getBool();

        if (!isServer)
        {
            this.ticksSinceLastChargeEvent = ticksSinceLastChargeEvent;
            this.isActive = isActive;
            this.gotEnergyLastTick = gotEnergyLastTick;
            this.nearEnderSource = nearEnderSource;
        }
    }

    public void searchForEnderNearby()
    {
        this.nearEnderSource = false;

        for (int radius = 1; radius <= 2 && !this.nearEnderSource; radius++)
        {
            Set<BlockCoord> planeAtRadius = Geometry.squareSet(radius, new BlockCoord(this.xCoord, this.yCoord, this.zCoord), ForgeDirection.UP);
            for (BlockCoord coord : planeAtRadius)
            {
                Block block = this.worldObj.getBlock(coord.x, coord.y, coord.z);
                if(block == DevETFluids.blockFluidCoFHEnder && ((IFluidBlock)block).canDrain(this.worldObj, coord.x, coord.y, coord.z))
                {
                    this.nearEnderSource = true;
                    return;
                }
            }
        }
    }

    @Override
    public void updateEntity()
    {
        if (ServerHelper.isServerWorld(this.worldObj))
        {
            boolean shouldSendUpdate = false;
            shouldSendUpdate = shouldSendUpdate || (this.isActive != this.gotEnergyLastTick);

            if (this.ticksSinceLastChargeEvent == TICKS_PER_CHARGE_MINIMUM)
            {
                this.ticksSinceLastChargeEvent = 0;
            }

            if (this.ticksSinceLastEnderCheck == TICKS_PER_ENDER_CHECK)
            {
                this.ticksSinceLastEnderCheck = 0;

                boolean oldSearchForEnder = this.nearEnderSource;
                this.searchForEnderNearby();

                shouldSendUpdate = shouldSendUpdate || (oldSearchForEnder != this.nearEnderSource);
            }

            this.isActive = this.gotEnergyLastTick && this.nearEnderSource && this.getOrientation().equals(ForgeDirection.UP);

            if (this.ticksSinceLastUpdate == TICKS_PER_UPDATE)
            {
                this.ticksSinceLastUpdate = 0;
                shouldSendUpdate = true;
            }

            if (shouldSendUpdate)
            {
                this.sendDescriptionPacket();
            }

            this.ticksSinceLastChargeEvent++;
            if (this.ticksSinceLastChargeEvent > TICKS_PER_CHARGE_MINIMUM)
                this.ticksSinceLastChargeEvent = TICKS_PER_CHARGE_MINIMUM;

            this.ticksSinceLastUpdate++;
            if (this.ticksSinceLastUpdate > TICKS_PER_UPDATE) this.ticksSinceLastUpdate = TICKS_PER_UPDATE;

            this.ticksSinceLastEnderCheck++;
            if (this.ticksSinceLastEnderCheck > TICKS_PER_ENDER_CHECK) this.ticksSinceLastEnderCheck = TICKS_PER_ENDER_CHECK;

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

    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip)
    {
        if (this.gotEnergyLastTick)
        {
            currenttip.add(EnumChatFormatting.GREEN + "Powered" + EnumChatFormatting.RESET);
        } else
        {
            currenttip.add(EnumChatFormatting.RED + "Not powered" + EnumChatFormatting.RESET);
        }

        if (this.orientation.equals(ForgeDirection.UP))
        {
            currenttip.add(EnumChatFormatting.GREEN + "Upright" + EnumChatFormatting.RESET);
        } else
        {
            currenttip.add(EnumChatFormatting.RED + "Not upright" + EnumChatFormatting.RESET);
        }

        if (this.nearEnderSource)
        {
            currenttip.add(EnumChatFormatting.GREEN + "Near Ender" + EnumChatFormatting.RESET);
        } else
        {
            currenttip.add(EnumChatFormatting.RED + "Not near Ender" + EnumChatFormatting.RESET);
        }

        if (this.isActive)
        {
            currenttip.add(EnumChatFormatting.GREEN + "Active" + EnumChatFormatting.RESET);
        } else
        {
            currenttip.add(EnumChatFormatting.RED + "Inactive" + EnumChatFormatting.RESET);
        }

        return currenttip;
    }
}
