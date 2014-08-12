package io.endertech.modules.dev.tile;

import cofh.api.energy.IEnergyHandler;
import cofh.api.tileentity.IReconfigurableFacing;
import cofh.lib.util.helpers.ServerHelper;
import cpw.mods.fml.common.registry.GameRegistry;
import io.endertech.modules.dev.block.BlockChargePad;
import io.endertech.network.PacketETBase;
import io.endertech.reference.Strings;
import io.endertech.tile.TileET;
import io.endertech.util.helper.StringHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.util.ForgeDirection;
import java.util.List;

public class TileChargePad extends TileET implements IReconfigurableFacing, IEnergyHandler
{
    public static final short TICKS_PER_UPDATE = 20;
    public short ticksSinceLastUpdate = 0;
    public boolean isActive = false;
    public int storedEnergy = 0;

    public static void init()
    {
        GameRegistry.registerTileEntity(TileChargePad.class, "tile." + Strings.Blocks.CHARGE_PAD);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbtTagCompound)
    {
        super.readFromNBT(nbtTagCompound);

        if (nbtTagCompound.hasKey("Energy")) this.storedEnergy = nbtTagCompound.getInteger("Energy");
    }

    @Override
    public void writeToNBT(NBTTagCompound nbtTagCompound)
    {
        super.writeToNBT(nbtTagCompound);

        nbtTagCompound.setInteger("Energy", this.storedEnergy);
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
        packet.addBool(this.isActive);
        packet.addInt(this.storedEnergy);

        return packet;
    }

    @Override
    public void handleTilePacket(PacketETBase tilePacket, boolean isServer)
    {
        super.handleTilePacket(tilePacket, isServer);

        boolean isActive = tilePacket.getBool();
        int storedEnergy = tilePacket.getInt();

        if (!isServer)
        {
            this.isActive = isActive;
            this.storedEnergy = storedEnergy;
        }
    }

    @Override
    public void updateEntity()
    {
        if (ServerHelper.isServerWorld(this.worldObj))
        {
            boolean oldActive = this.isActive;
            int meta = this.worldObj.getBlockMetadata(this.xCoord, this.yCoord, this.zCoord);
            this.isActive = this.storedEnergy > 0 || BlockChargePad.isCreative(meta);

            boolean shouldSendUpdate = false;
            shouldSendUpdate = shouldSendUpdate || (this.isActive != oldActive);

            if (this.ticksSinceLastUpdate == TICKS_PER_UPDATE)
            {
                this.ticksSinceLastUpdate = 0;
                shouldSendUpdate = true;
            }

            if (shouldSendUpdate)
            {
                this.sendDescriptionPacket();
            }

            this.ticksSinceLastUpdate++;
            if (this.ticksSinceLastUpdate > TICKS_PER_UPDATE) this.ticksSinceLastUpdate = TICKS_PER_UPDATE;
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
        if (!canReceive) return 0;

        int blockMeta = this.worldObj.getBlockMetadata(this.xCoord, this.yCoord, this.zCoord);
        int energyReceived = Math.min(BlockChargePad.getMaxEnergyStored(blockMeta) - this.storedEnergy, Math.min(BlockChargePad.getMaxReceiveRate(blockMeta), maxReceive));

        if (!simulate)
        {
            this.storedEnergy += energyReceived;
        }

        return energyReceived;
    }

    @Override
    public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate)
    {
        return 0;
    }

    @Override
    public int getEnergyStored(ForgeDirection from)
    {
        return this.storedEnergy;
    }

    @Override
    public int getMaxEnergyStored(ForgeDirection from)
    {
        int blockMeta = this.worldObj.getBlockMetadata(this.xCoord, this.yCoord, this.zCoord);
        return BlockChargePad.getMaxEnergyStored(blockMeta);
    }

    @Override
    public boolean canConnectEnergy(ForgeDirection from)
    {
        return from != this.getOrientation();
    }

    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip)
    {
        if (this.isActive)
        {
            currenttip.add(EnumChatFormatting.GREEN + "Active" + EnumChatFormatting.RESET);
        } else
        {
            currenttip.add(EnumChatFormatting.RED + "Inactive" + EnumChatFormatting.RESET);
        }

        int blockMeta = this.worldObj.getBlockMetadata(this.xCoord, this.yCoord, this.zCoord);
        if (BlockChargePad.isCreative(blockMeta)) currenttip.add("Charge: Infinite");
        else
            currenttip.add("Charge: " + StringHelper.getEnergyString(this.storedEnergy) + " / " + StringHelper.getEnergyString(BlockChargePad.getMaxEnergyStored(blockMeta)));

        return currenttip;
    }
}
