package io.endertech.multiblock.controller;

import cofh.api.energy.IEnergyContainerItem;
import cofh.api.energy.IEnergyStorage;
import cofh.lib.util.helpers.EnergyHelper;
import cofh.lib.util.helpers.ServerHelper;
import com.google.common.math.IntMath;
import io.endertech.config.GeneralConfig;
import io.endertech.multiblock.IMultiblockPart;
import io.endertech.multiblock.MultiblockControllerBase;
import io.endertech.multiblock.MultiblockValidationException;
import io.endertech.multiblock.block.BlockTankController;
import io.endertech.multiblock.rectangular.RectangularMultiblockControllerBase;
import io.endertech.multiblock.tile.TileTankController;
import io.endertech.multiblock.tile.TileTankEnergyInput;
import io.endertech.multiblock.tile.TileTankPart;
import io.endertech.multiblock.tile.TileTankValve;
import io.endertech.network.ITilePacketHandler;
import io.endertech.network.PacketETBase;
import io.endertech.util.BlockCoord;
import io.endertech.util.IChargeableFromSlot;
import io.endertech.util.IOutlineDrawer;
import io.endertech.util.RGBA;
import io.endertech.util.helper.*;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import java.util.*;

public class ControllerTank extends RectangularMultiblockControllerBase implements IOutlineDrawer, ITilePacketHandler, IEnergyStorage, IChargeableFromSlot, IInventory
{
    public static final int MAX_ENERGY_STORAGE = 10 * 1000000;
    private static final String TANK_NAME = "MainTank";
    public FluidTank tank;
    public FluidTank lastTank;
    public boolean renderedOnce = false;
    public int renderAddition = 0;
    public ItemStack[] inventory;
    protected boolean active;
    private Set<TileTankController> attachedControllers;
    private Set<TileTankValve> attachedValves;
    private Set<TileTankEnergyInput> attachedEnergyInputs;
    private int storedEnergy = 0;
    private int random_number = 0;
    private int ticksSinceUpdate = 0;

    public ControllerTank(World world)
    {
        super(world);
        active = false;
        attachedControllers = new HashSet<TileTankController>();
        attachedValves = new HashSet<TileTankValve>();
        attachedEnergyInputs = new HashSet<TileTankEnergyInput>();
        tank = new FluidTank(FluidContainerRegistry.BUCKET_VOLUME);
        lastTank = new FluidTank(FluidContainerRegistry.BUCKET_VOLUME);

        this.inventory = new ItemStack[1];
    }

    public int getRandomNumber()
    {
        return this.random_number;
    }

    public void setRandomNumber(int newRandomNumber)
    {
        //        LogHelper.info("Setting random number from " + this.random_number + " to " + newRandomNumber);
        this.random_number = newRandomNumber;
    }

    public int getStoredEnergy()
    {
        return this.storedEnergy;
    }

    public void setStoredEnergy(int energyStored)
    {
        this.storedEnergy = energyStored;
    }

    @Override
    public void onAttachedPartWithMultiblockNBT(IMultiblockPart part, NBTTagCompound nbt)
    {
        this.readFromNBT(nbt);
    }

    @Override
    public void onAttachedPartWithMultiblockPacket(IMultiblockPart part, PacketETBase packetETBase)
    {
        //        LogHelper.info("My random number before: " + this.random_number);
        this.handleTilePacket(packetETBase, true);
        //        LogHelper.info("My random number after:" + this.random_number);
    }

    @Override
    protected void onBlockAdded(IMultiblockPart newPart)
    {
        if (newPart instanceof TileTankController)
        {
            TileTankPart tankPart = (TileTankPart) newPart;
            if (BlockTankController.isController(tankPart.getBlockMetadata()))
            {
                attachedControllers.add((TileTankController) tankPart);
            }
        }

        if (newPart instanceof TileTankValve)
        {
            attachedValves.add((TileTankValve) newPart);
        }

        if (newPart instanceof TileTankEnergyInput)
        {
            attachedEnergyInputs.add((TileTankEnergyInput) newPart);
        }
    }

    @Override
    protected void onBlockRemoved(IMultiblockPart oldPart)
    {
        if (oldPart instanceof TileTankController)
        {
            TileTankPart tankPart = (TileTankPart) oldPart;
            if (BlockTankController.isController(tankPart.getBlockMetadata()))
            {
                attachedControllers.remove(tankPart);
            }
        }

        if (oldPart instanceof TileTankValve)
        {
            attachedValves.remove((TileTankValve) oldPart);
        }

        if (oldPart instanceof TileTankEnergyInput)
        {
            attachedEnergyInputs.remove((TileTankEnergyInput) oldPart);
        }
    }

    @Override
    protected void isMachineWhole() throws MultiblockValidationException
    {
        if (attachedControllers.size() != 1)
        {
            throw new MultiblockValidationException("You must have 1 controller in the tank structure (currently " + attachedControllers.size() + ")");
        }

        if (attachedValves.size() < 1)
        {
            throw new MultiblockValidationException("You must have at least 1 valve in the tank structure (currently " + attachedValves.size() + ")");
        }

        if (attachedEnergyInputs.size() < 1)
        {
            throw new MultiblockValidationException("You must have at least 1 energy input in the tank structure (currently " + attachedEnergyInputs.size() + ")");
        }

        super.isMachineWhole();
    }

    @Override
    protected void onMachineAssembled()
    {
        if (this.getRandomNumber() == 0) setRandomNumber(new Random().nextInt(1000000));
        //        LogHelper.info("Tank assembled with R: " + this.random_number + "!");

        BlockCoord minCoord = this.getMinimumCoord();
        BlockCoord maxCoord = this.getMaximumCoord();
        BlockCoord dimensionCoord = new BlockCoord(maxCoord.x - minCoord.x - 1, maxCoord.y - minCoord.y - 1, maxCoord.z - minCoord.z - 1);

        int interiorSize = dimensionCoord.x * dimensionCoord.y * dimensionCoord.z;
        int internalCapacity = interiorSize * FluidContainerRegistry.BUCKET_VOLUME;

        int tankCapacity;
        try {
            tankCapacity = IntMath.checkedMultiply(internalCapacity, GeneralConfig.tankStorageMultiplier);
        } catch (ArithmeticException e) {
            tankCapacity = Integer.MAX_VALUE;
        }

        this.tank.setCapacity(tankCapacity);

        if (this.lastTank != null) this.lastTank.setCapacity(tankCapacity);

        this.setActive(true);

        this.sendUpdatePacketToClosePlayers();
    }

    @Override
    protected void onMachineRestored()
    {
        //        LogHelper.info("Tank restored");
    }

    @Override
    protected void onMachinePaused()
    {
        //        LogHelper.info("Tank paused");
    }

    public void popInventoryContentsOut(World world, int x, int y, int z)
    {
        for (ItemStack itemStack : this.inventory)
        {
            if (itemStack != null)
            {
                WorldHelper.spawnItemInWorldWithRandomness(itemStack, world, 0.3F, x, y, z, 2);
            }
        }

        this.inventory = new ItemStack[this.inventory.length];
    }

    @Override
    protected void onMachineDisassembled()
    {
        if (ServerHelper.isServerWorld(this.worldObj))
        {
            if (this.attachedControllers.size() > 0)
            {
                TileTankController tankController = this.attachedControllers.iterator().next();
                if (tankController != null)
                {
                    ForgeDirection out = tankController.getFirstOutwardsDir();
                    if (out == null) out = tankController.getOrientation();

                    this.popInventoryContentsOut(tankController.getWorldObj(), tankController.xCoord + out.offsetX, tankController.yCoord + out.offsetY, tankController.zCoord + out.offsetZ);
                }
            }
        }

        this.setActive(false);
        //        LogHelper.info("Tank disassembled");
    }

    @Override
    protected int getMinimumNumberOfBlocksForAssembledMachine()
    {
        return getMinimumXSize() * getMinimumZSize() * getMinimumYSize() - 7;
    }

    @Override
    protected int getMaximumXSize()
    {
        return 8;
    }

    @Override
    protected int getMinimumXSize() { return 3; }

    @Override
    protected int getMaximumZSize()
    {
        return 8;
    }

    @Override
    protected int getMinimumZSize() { return 3; }

    @Override
    protected int getMaximumYSize()
    {
        return 10;
    }

    @Override
    protected int getMinimumYSize() { return 3; }

    @Override
    protected void onAssimilate(MultiblockControllerBase assimilated)
    {
        //LogHelper.debug("Assimilation occurred: ");
        //LogHelper.debug("Me: " + this.toString());
        //LogHelper.debug("Assimilated: " + assimilated.toString());

        if (assimilated instanceof ControllerTank)
        {
            ControllerTank assimilatedController = (ControllerTank) assimilated;

            boolean thisHasContents = (this.tank.getFluidAmount() > 0 || this.storedEnergy > 0);
            boolean assimilatedHasContents = (assimilatedController.tank.getFluidAmount() > 0 || assimilatedController.storedEnergy > 0);

            ControllerTank candidate = null;
            if (!thisHasContents && assimilatedHasContents) candidate = assimilatedController;
            else if (thisHasContents && !assimilatedHasContents) candidate = this;
            else if (!thisHasContents && !assimilatedHasContents) candidate = assimilatedController;

            if (candidate == null)
            {
                if (this.tank.getFluidAmount() >= assimilatedController.tank.getFluidAmount()) candidate = this;
                else candidate = assimilatedController;

                if (ServerHelper.isServerWorld(this.worldObj))
                {
                    if(this.getRandomNumber() == candidate.getRandomNumber()
                            && this.tank.getFluid() == candidate.tank.getFluid()
                            && this.getEnergyStored() == candidate.getEnergyStored()) {
                        // Strange edge case with loading:
                        //  Two identical controller were merged
                        //  Seems to be an issue with lag and loading order?
                        //  Not actually destructive either way - log a different message

                        //LogHelper.(LocalisationHelper.localiseString("error.multiblock.tank.destructive_assimilation_same"));
                        //Lazy removal of logging
                        int i = 1;
                    } else {
                        LogHelper.error(LocalisationHelper.localiseString("error.multiblock.tank.destructive_assimilation"));
                        LogHelper.error(this.toString());
                        LogHelper.error(candidate.toString());
                    }
                }
            }

            setRandomNumber(candidate.getRandomNumber());
            this.tank = new FluidTank(candidate.tank.getFluid(), candidate.tank.getCapacity());
            this.storedEnergy = candidate.storedEnergy;
        }

        //LogHelper.debug("Result: " + this.toString());
    }

    @Override
    protected void onAssimilated(MultiblockControllerBase assimilator)
    {
        this.attachedControllers.clear();
        this.attachedEnergyInputs.clear();
        this.attachedValves.clear();

        this.storedEnergy = 0;
        this.tank = new FluidTank(null, 0);
    }

    @Override
    protected boolean updateServer()
    {
        if (ticksSinceUpdate > 20)
        {
            ticksSinceUpdate = 0;
            return true;
        }

        this.chargeFromGUISlot();

        ticksSinceUpdate++;
        return false;
    }

    @Override
    protected void updateClient() { }

    @Override
    public void writeToNBT(NBTTagCompound data)
    {
        data.setBoolean("tankActive", this.isActive());
        data.setInteger("randomNumber", this.random_number);
        data.setInteger("storedEnergy", this.storedEnergy);
        NBTTagCompound tankNBT = new NBTTagCompound();
        tank.writeToNBT(tankNBT);
        data.setTag(TANK_NAME, tankNBT);
        NBTHelper.writeInventoryToNBT(data, this.inventory);

        //        LogHelper.info("Writing tank to NBT: " + this.toString());
    }

    public String toString()
    {
        return "R: " + this.getRandomNumber() + " A: " + this.active + " F: " + getFluidStringOrNone(this.tank.getFluid()) + " " + this.tank.getFluidAmount() + "/" + this.tank.getCapacity() + " Cs: " + this.attachedControllers.size() + " Vs: " + this.attachedValves.size() + " E: " + this.storedEnergy + "/" + MAX_ENERGY_STORAGE;
    }

    private String getFluidStringOrNone(FluidStack fluid)
    {
        if (fluid == null) return "none";
        else return getFluidStringOrNone(fluid.getFluid());
    }

    private String getFluidStringOrNone(Fluid fluid)
    {
        if (fluid == null) return "none";
        else return fluid.getLocalizedName();
    }

    @Override
    public void readFromNBT(NBTTagCompound data)
    {
        if (data.hasKey("tankActive"))
        {
            setActive(data.getBoolean("tankActive"));
        }

        if (data.hasKey("randomNumber"))
        {
            this.setRandomNumber(data.getInteger("randomNumber"));
            //            LogHelper.info("Read random number from NBT: " + this.random_number);
        }

        if (data.hasKey(TANK_NAME))
        {
            this.tank.readFromNBT(data.getCompoundTag(TANK_NAME));
        }

        if (data.hasKey("storedEnergy"))
        {
            this.storedEnergy = data.getInteger("storedEnergy");
        }

        if (data.hasKey("Inventory"))
        {
            this.inventory = NBTHelper.readInventoryFromNBT(data, 1);
        }
    }

    @Override
    public PacketETBase getPacket(PacketETBase packetSaveDelegateBase)
    {
        packetSaveDelegateBase.addInt(this.random_number);
        packetSaveDelegateBase.addFluidStack(this.tank.getFluid());
        packetSaveDelegateBase.addInt(this.tank.getCapacity());
        packetSaveDelegateBase.addInt(this.storedEnergy);
        packetSaveDelegateBase.addInventory(this.inventory);

        return packetSaveDelegateBase;
    }

    @Override
    public void handleTilePacket(PacketETBase packetETBase, boolean isServer)
    {
        int random_number = packetETBase.getInt();
        FluidStack fluidStack = packetETBase.getFluidStack();
        int capacity = packetETBase.getInt();
        int storedEnergy = packetETBase.getInt();
        ItemStack[] inventory = packetETBase.getInventory(1);

        if (!isServer)
        {
            this.random_number = random_number;
            this.tank.setCapacity(capacity);
            this.lastTank = new FluidTank(this.tank.getFluid(), this.tank.getCapacity());
            this.renderAddition = 0;
            this.tank.setFluid(fluidStack);
            this.storedEnergy = storedEnergy;
            this.inventory = inventory;
        }
    }

    @Override
    public String getName()
    {
        // TODO: localise
        return "Ender Tank";
    }

    public boolean isActive()
    {
        return this.active;
    }

    public void setActive(boolean active)
    {
        if (active == this.active) { return; }
        this.active = active;

        for (IMultiblockPart part : connectedParts)
        {
            if (this.active) { part.onMachineActivated(); } else { part.onMachineDeactivated(); }
        }
    }

    @Override
    protected void isBlockGoodForInterior(World world, int x, int y, int z) throws MultiblockValidationException
    {
        if (world.isAirBlock(x, y, z)) { return; } // Air is OK
        else throw new MultiblockValidationException("Interior must be air");
    }

    @Override
    public List<String> getWailaBody()
    {
        List<String> additions = new ArrayList<String>();

        if (this.tank.getFluid() != null && this.tank.getFluidAmount() > 0)
        {
            additions.add(StringHelper.getFluidName(this.tank.getFluid()));
            additions.add(this.tank.getFluidAmount() + " / " + this.tank.getCapacity() + " mB");
        } else
        {
            additions.add("Empty");
        }

        additions.add(StringHelper.getEnergyString(this.storedEnergy) + " / " + StringHelper.getEnergyString(MAX_ENERGY_STORAGE) + " RF");


        return additions;
    }

    @Override
    public boolean shouldConsume(MultiblockControllerBase otherController)
    {
        boolean shouldConsume = super.shouldConsume(otherController);
        if (shouldConsume)
        {
            ControllerTank otherTank = (ControllerTank) otherController;
            if ((this.getStoredEnergy() > 0 || this.tank.getFluidAmount() > 0) && (otherTank.getStoredEnergy() > 0 || otherTank.tank.getFluidAmount() > 0))
            {
                if (ServerHelper.isServerWorld(this.worldObj))
                    LogHelper.warn(LocalisationHelper.localiseString("warning.multiblock.tank.destructive_assimilation_check"));
            }
        }

        return shouldConsume;
    }

    public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate)
    {
        return this.receiveEnergy(maxReceive, simulate);
    }

    public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate)
    {
        int energy = this.getStoredEnergy();
        int energyExtracted = Math.min(energy, Math.min(10 * 1000000, maxExtract));

        if (!simulate)
        {
            energy -= energyExtracted;
            this.setStoredEnergy(energy);
        }

        return energyExtracted;
    }

    @Override
    public boolean drawOutline(DrawBlockHighlightEvent event)
    {
        BlockCoord target = new BlockCoord(event.target.blockX, event.target.blockY, event.target.blockZ);
        World world = event.player.worldObj;

        if (GeneralConfig.debugRender)
        {
            Set<IMultiblockPart> connectedParts = this.getConnectedParts();
            if (connectedParts.isEmpty())
            {
                RenderHelper.renderBlockOutline(event.context, event.player, target, RGBA.White.setAlpha(0.6f), 2.0f, event.partialTicks);
            }

            RGBA colour = RGBA.Blue.setAlpha(0.6f);
            if (this.isAssembled())
            {
                colour = RGBA.Green.setAlpha(0.6f);
            }

            for (IMultiblockPart part : connectedParts)
            {
                BlockCoord partCoord = part.getWorldLocation();
                Block blockPart = world.getBlock(partCoord.x, partCoord.y, partCoord.z);
                if (blockPart instanceof BlockTankController)
                {
                    if (BlockTankController.isController(world.getBlockMetadata(partCoord.x, partCoord.y, partCoord.z)))
                        RenderHelper.renderBlockOutline(event.context, event.player, partCoord, RGBA.White.setAlpha(0.6f), 10.0f, event.partialTicks);

                } else
                {
                    RenderHelper.renderBlockOutline(event.context, event.player, partCoord, colour, 2.0f, event.partialTicks);
                }

                if (part.isMultiblockSaveDelegate())
                    RenderHelper.renderBlockOutline(event.context, event.player, partCoord, RGBA.Red, 10.0f, event.partialTicks);
            }
        } else
        {
            BlockCoord minCoord = this.getMinimumCoord();
            BlockCoord maxCoord = this.getMaximumCoord();
            AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(minCoord.x, minCoord.y, minCoord.z, maxCoord.x + 1, maxCoord.y + 1, maxCoord.z + 1);
            RenderHelper.renderAABBOutline(event.context, event.player, aabb, RGBA.Black, 1.0f, event.partialTicks);
        }

        return true;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate)
    {
        int energy = this.getStoredEnergy();
        int energyReceived = Math.min(ControllerTank.MAX_ENERGY_STORAGE - energy, Math.min(TileTankEnergyInput.MAX_INPUT_RATE, maxReceive));

        if (!simulate)
        {
            energy += energyReceived;
            this.setStoredEnergy(energy);
        }

        return energyReceived;
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

    // INVENTORY

    @Override
    public int getMaxEnergyStored()
    {
        return ControllerTank.MAX_ENERGY_STORAGE;
    }

    @Override
    public int getSizeInventory()
    {
        return this.inventory.length;
    }

    @Override
    public ItemStack getStackInSlot(int slot)
    {
        return this.inventory[slot];
    }

    @Override
    public ItemStack decrStackSize(int slot, int amount)
    {
        if (this.inventory[slot] == null) return null;

        if (this.inventory[slot].stackSize <= amount) amount = this.inventory[slot].stackSize;

        ItemStack itemStack = this.inventory[slot].splitStack(amount);

        if (this.inventory[slot].stackSize <= 0) this.inventory[slot] = null;

        return itemStack;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot)
    {
        if (this.inventory[slot] == null) return null;

        ItemStack itemStack = this.inventory[slot];
        this.inventory[slot] = null;

        return itemStack;
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack itemStack)
    {
        this.inventory[slot] = itemStack;

        int inventoryStackLimit = this.getInventoryStackLimit();
        if (itemStack != null && itemStack.stackSize > inventoryStackLimit) itemStack.stackSize = inventoryStackLimit;

        // TODO: Mark save delegate chunk as dirty
    }

    @Override
    public String getInventoryName()
    {
        return this.getName();
    }

    @Override
    public boolean hasCustomInventoryName()
    {
        return !this.getName().isEmpty();
    }

    @Override
    public int getInventoryStackLimit()
    {
        return 64;
    }

    @Override
    public void markDirty()
    {

    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player)
    {
        return true;
    }

    @Override
    public void openInventory()
    {

    }

    @Override
    public void closeInventory()
    {

    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack itemStack)
    {
        return true;
    }

    @Override
    public int getChargeSlot()
    {
        return this.inventory.length - 1;
    }

    @Override
    public boolean hasChargeSlot()
    {
        return true;
    }

    @Override
    public void chargeFromGUISlot()
    {
        int chargeSlot = getChargeSlot();
        ItemStack chargeItemStack = this.inventory[chargeSlot];
        if (!this.hasChargeSlot() || !EnergyHelper.isEnergyContainerItem(chargeItemStack)) return;

        int chargeAmount = Math.min(TileTankEnergyInput.MAX_INPUT_RATE, ControllerTank.MAX_ENERGY_STORAGE - this.getEnergyStored());

        IEnergyContainerItem energyContainerItem = (IEnergyContainerItem) chargeItemStack.getItem();
        if (energyContainerItem == null) return;

        int extractedAmount = energyContainerItem.extractEnergy(chargeItemStack, chargeAmount, false);
        this.receiveEnergy(extractedAmount, false);

        if (chargeItemStack.stackSize <= 0) this.inventory[chargeSlot] = null;
    }
}
