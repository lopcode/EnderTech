package io.endertech.tile;

import cofh.api.energy.IEnergyContainerItem;
import cofh.api.energy.IEnergyHandler;
import cofh.api.tileentity.IReconfigurableFacing;
import cofh.lib.util.helpers.EnergyHelper;
import cpw.mods.fml.common.registry.GameRegistry;
import io.endertech.config.GeneralConfig;
import io.endertech.network.PacketETBase;
import io.endertech.reference.Strings;
import io.endertech.util.IChargeableFromSlot;
import io.endertech.util.IOutlineDrawer;
import io.endertech.util.RGBA;
import io.endertech.util.helper.LocalisationHelper;
import io.endertech.util.helper.NBTHelper;
import io.endertech.util.helper.RenderHelper;
import net.minecraft.block.Block;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Conduit tile and routing logic.
 *
 * @author Arkan <arkan@drakon.io>
 */
@SuppressWarnings("PointlessArithmeticExpression") // Make Java ignore the '1 * ...' expressions below.
public class TileConduit extends TileInventory implements ISidedInventory, IReconfigurableFacing, IEnergyHandler, IOutlineDrawer, IChargeableFromSlot
{
    private static final int RECV_BASE = 2000;
    private static final int CAPACITY_BASE = 2000000;
    private static final int GROUPS_PER_TICK_BASE = 4;
    private static final int RANGE_BASE = 8;

    public static final int[] RECEIVE = {0, 1 * RECV_BASE, 10 * RECV_BASE};
    public static final int[] CAPACITY = {-1, 1 * CAPACITY_BASE, 10 * CAPACITY_BASE};
    public static final int[] GROUPS_PER_TICK = {32, 1 * GROUPS_PER_TICK_BASE, 4 * GROUPS_PER_TICK_BASE};
    public static final int[] RANGE = {32, 1 * RANGE_BASE, 2 * RANGE_BASE};

    public static final int INVENTORY_SIZE = 10;
    public boolean isActive = false;
    public short ticksSinceLastUpdate = 0;
    public int storedEnergy = 0;
    public boolean isCreative = false;

    public TileConduit()
    {
        super();

        this.inventory = new ItemStack[INVENTORY_SIZE];
    }

    public static void init()
    {
        GameRegistry.registerTileEntity(TileConduit.class, "tile." + Strings.Blocks.CONDUIT_NAME);
    }

    public String getName()
    {
        Block block = this.worldObj.getBlock(this.xCoord, this.yCoord, this.zCoord);
        int blockMeta = this.worldObj.getBlockMetadata(this.xCoord, this.yCoord, this.zCoord);

        return LocalisationHelper.localiseString(block.getUnlocalizedName() + "." + blockMeta + ".name");
    }

    public static void writeDefaultTag(NBTTagCompound nbtTagCompound)
    {
        nbtTagCompound.setInteger("Energy", 0);
        NBTHelper.writeInventoryToNBT(nbtTagCompound, new ItemStack[INVENTORY_SIZE]);
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

        return this.receiveEnergy(maxReceive, simulate);
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
        return this.getMaxEnergyStored(blockMeta);
    }

    @Override
    public boolean canConnectEnergy(ForgeDirection from)
    {
        return from != this.getOrientation();
    }

    @Override
    public boolean hasItemState()
    {
        return true;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbtTagCompound)
    {
        super.readFromNBT(nbtTagCompound);

        this.readStateFromNBT(nbtTagCompound);
    }

    @Override
    public void readStateFromNBT(NBTTagCompound nbtTagCompound)
    {
        if (nbtTagCompound.hasKey("Energy")) this.storedEnergy = nbtTagCompound.getInteger("Energy");

        if (nbtTagCompound.hasKey("Inventory"))
        {
            this.inventory = NBTHelper.readInventoryFromNBT(nbtTagCompound, INVENTORY_SIZE);
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbtTagCompound)
    {
        super.writeToNBT(nbtTagCompound);

        this.writeStateToNBT(nbtTagCompound);
    }

    @Override
    public void writeStateToNBT(NBTTagCompound nbtTagCompound)
    {
        nbtTagCompound.setInteger("Energy", this.storedEnergy);
        NBTHelper.writeInventoryToNBT(nbtTagCompound, this.inventory);
    }

    @Override
    public PacketETBase getPacket()
    {
        PacketETBase packet = super.getPacket();
        packet.addBool(this.isActive);
        packet.addInt(this.storedEnergy);
        packet.addInventory(this.inventory);

        return packet;
    }

    @Override
    public void handleTilePacket(PacketETBase tilePacket, boolean isServer)
    {
        super.handleTilePacket(tilePacket, isServer);

        boolean isActive = tilePacket.getBool();
        int storedEnergy = tilePacket.getInt();
        ItemStack[] inventory = tilePacket.getInventory(INVENTORY_SIZE);

        if (!isServer)
        {
            this.isActive = isActive;
            this.storedEnergy = storedEnergy;
            this.inventory = inventory;
        }
    }

    public AxisAlignedBB getAABBInFront(int distance)
    {
        ForgeDirection orientation = this.getOrientation();
        AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(this.xCoord, this.yCoord, this.zCoord,
                this.xCoord + 1 + orientation.offsetX * distance, this.yCoord + 1 + orientation.offsetY * distance,
                this.zCoord + 1 + orientation.offsetZ * distance);
        return orientation == ForgeDirection.UP ? aabb.offset(0, 1, 0) : aabb;
    }

    @Override
    public boolean hasGui()
    {
        return true;
    }

    public int getChargeSlot()
    {
        return this.inventory.length - 1;
    }

    public boolean hasChargeSlot() { return true; }

    public void chargeFromGUISlot()
    {
        if (this.isCreative) return;

        int chargeSlot = getChargeSlot();
        ItemStack chargeItemStack = this.inventory[chargeSlot];
        if (!this.hasChargeSlot() || !EnergyHelper.isEnergyContainerItem(chargeItemStack)) return;

        int meta = this.worldObj.getBlockMetadata(this.xCoord, this.yCoord, this.zCoord);
        int chargeAmount = Math.min(this.getMaxReceiveRate(meta), this.getMaxEnergyStored(meta) - this.getEnergyStored());

        IEnergyContainerItem energyContainerItem = (IEnergyContainerItem) chargeItemStack.getItem();
        if (energyContainerItem == null) return;

        int extractedAmount = energyContainerItem.extractEnergy(chargeItemStack, chargeAmount, false);
        this.receiveEnergy(extractedAmount, false);

        if (chargeItemStack.stackSize <= 0) this.inventory[chargeSlot] = null;
    }

    @Override
    public boolean drawOutline(DrawBlockHighlightEvent event)
    {
        if (GeneralConfig.debugRender)
        {
            int meta = this.worldObj.getBlockMetadata(this.xCoord, this.yCoord, this.zCoord);
            AxisAlignedBB front = this.getAABBInFront(RANGE[meta]);
            RenderHelper.renderAABBOutline(event.context, event.player, front, RGBA.Red.setAlpha(0.6f), 2.0f, event.partialTicks);
            return true;
        } else
        {
            return false;
        }
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate)
    {
        return 0;
    }

    @Override
    public int getEnergyStored()
    {
        return this.storedEnergy;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate)
    {
        if (this.isCreative) return 0;

        int blockMeta = this.worldObj.getBlockMetadata(this.xCoord, this.yCoord, this.zCoord);
        int energyReceived = Math.min(this.getMaxEnergyStored(blockMeta) - this.storedEnergy, Math.min(this.getMaxReceiveRate(blockMeta), maxReceive));

        if (!simulate)
        {
            this.storedEnergy += energyReceived;
        }

        return energyReceived;
    }

    @Override
    public int getMaxEnergyStored()
    {
        int blockMeta = this.worldObj.getBlockMetadata(this.xCoord, this.yCoord, this.zCoord);
        return getMaxEnergyStored(blockMeta);
    }

    public int getMaxReceiveRate(int meta)
    {
        return RECEIVE[meta];
    }

    public int getMaxEnergyStored(int meta)
    {
        return CAPACITY[meta];
    }

    /* ISidedInventory */
    @Override
    public int[] getAccessibleSlotsFromSide(int side)
    {
        if (side == this.getOrientation().ordinal()) return new int[0];
        else
        {
            int[] validSlots = new int[9];
            for (int i = 0; i < 9; i++) validSlots[i] = i; // wtb range(0..8) Java.
            return validSlots;
        }
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack istack, int side)
    {
        return side != this.getOrientation().ordinal() && super.isItemValidForSlot(slot, istack);
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack istack, int side)
    {
        return side != this.getOrientation().ordinal();
    }
    /* __ISidedInventory */
}
