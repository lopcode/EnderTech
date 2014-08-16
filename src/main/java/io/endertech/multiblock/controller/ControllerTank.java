package io.endertech.multiblock.controller;

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
import io.endertech.util.IOutlineDrawer;
import io.endertech.util.RGBA;
import io.endertech.util.helper.LogHelper;
import io.endertech.util.helper.RenderHelper;
import io.endertech.util.helper.StringHelper;
import net.minecraft.block.Block;
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

public class ControllerTank extends RectangularMultiblockControllerBase implements IOutlineDrawer, ITilePacketHandler
{
    protected boolean active;
    private Set<TileTankPart> attachedControllers;
    private Set<TileTankValve> attachedValves;
    private Set<TileTankEnergyInput> attachedEnergyInputs;
    private int storedEnergy = 0;
    private int random_number = 0;
    public FluidTank tank;
    public FluidTank lastTank;
    public boolean renderedOnce = false;
    public int renderAddition = 0;
    private int ticksSinceUpdate = 0;
    private static final String TANK_NAME = "MainTank";
    public static final int MAX_ENERGY_STORAGE = 10 * 1000000;

    public ControllerTank(World world)
    {
        super(world);
        active = false;
        attachedControllers = new HashSet<TileTankPart>();
        attachedValves = new HashSet<TileTankValve>();
        attachedEnergyInputs = new HashSet<TileTankEnergyInput>();
        tank = new FluidTank(FluidContainerRegistry.BUCKET_VOLUME * GeneralConfig.tankStorageMultiplier);
        lastTank = new FluidTank(FluidContainerRegistry.BUCKET_VOLUME * GeneralConfig.tankStorageMultiplier);
    }

    public void setRandomNumber(int newRandomNumber)
    {
        //        LogHelper.info("Setting random number from " + this.random_number + " to " + newRandomNumber);
        this.random_number = newRandomNumber;
    }

    public int getRandomNumber()
    {
        return this.random_number;
    }

    public void setStoredEnergy(int energyStored)
    {
        this.storedEnergy = energyStored;
    }

    public int getStoredEnergy()
    {
        return this.storedEnergy;
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
                attachedControllers.add(tankPart);
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
        int tankCapacity = interiorSize * FluidContainerRegistry.BUCKET_VOLUME * GeneralConfig.tankStorageMultiplier;
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

    @Override
    protected void onMachineDisassembled()
    {
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
        if (assimilated instanceof ControllerTank)
        {
            ControllerTank controller = (ControllerTank) assimilated;
            if (controller.getRandomNumber() != 0)
            {
                //                LogHelper.info("Setting new random number from assimilated controller");
                setRandomNumber(((ControllerTank) assimilated).getRandomNumber());
            } else
            {
                //                LogHelper.info("Not setting random number as new assimilated tank had 0");
            }
        }
    }

    @Override
    protected void onAssimilated(MultiblockControllerBase assimilator)
    {
        this.attachedControllers.clear();
    }

    @Override
    protected boolean updateServer()
    {
        if (ticksSinceUpdate > 20)
        {
            ticksSinceUpdate = 0;
            return true;
        }

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
    }

    @Override
    public PacketETBase getPacket(PacketETBase packetSaveDelegateBase)
    {
        packetSaveDelegateBase.addInt(this.random_number);
        packetSaveDelegateBase.addFluidStack(this.tank.getFluid());
        packetSaveDelegateBase.addInt(this.storedEnergy);

        return packetSaveDelegateBase;
    }

    @Override
    public void handleTilePacket(PacketETBase packetETBase, boolean isServer)
    {
        int random_number = packetETBase.getInt();
        FluidStack fluidStack = packetETBase.getFluidStack();
        int storedEnergy = packetETBase.getInt();

        if (!isServer)
        {
            this.random_number = random_number;
            this.lastTank = new FluidTank(this.tank.getFluid(), this.tank.getCapacity());
            this.renderAddition = 0;
            this.tank.setFluid(fluidStack);
            this.storedEnergy = storedEnergy;
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
            additions.add(this.tank.getFluidAmount() + "/" + this.tank.getCapacity() + " mB");
        } else
        {
            additions.add("Empty");
        }

        additions.add(StringHelper.getEnergyString(this.storedEnergy) + "/" + StringHelper.getEnergyString(MAX_ENERGY_STORAGE) + " RF");


        return additions;
    }

    @Override
    public boolean shouldConsume(MultiblockControllerBase otherController)
    {
        boolean shouldConsume = super.shouldConsume(otherController);
        if (shouldConsume)
        {
            ControllerTank otherTank = (ControllerTank) otherController;
            if (this.getRandomNumber() != 0 && otherTank.getRandomNumber() != 0)
            {
                LogHelper.warn("Warning: two tank structures with information in both were joined.");
            }
        }

        return shouldConsume;
    }

    public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate)
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
}
