package io.endertech.multiblock.controller;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.endertech.multiblock.IMultiblockPart;
import io.endertech.multiblock.MultiblockControllerBase;
import io.endertech.multiblock.MultiblockValidationException;
import io.endertech.multiblock.rectangular.RectangularMultiblockControllerBase;
import io.endertech.util.LogHelper;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class ControllerTank extends RectangularMultiblockControllerBase
{
    protected boolean active;

    public ControllerTank(World world)
    {
        super(world);
        active = false;
    }

    @Override
    public void onAttachedPartWithMultiblockData(IMultiblockPart part, NBTTagCompound data)
    {
        this.readFromNBT(data);
    }

    @Override
    protected void onBlockAdded(IMultiblockPart newPart)
    {

    }

    @Override
    protected void onBlockRemoved(IMultiblockPart oldPart)
    {

    }

    @Override
    protected void onMachineAssembled()
    {
        LogHelper.info("Tank assembled!");
    }

    @Override
    protected void onMachineRestored()
    {
        LogHelper.info("Tank restored");
    }

    @Override
    protected void onMachinePaused()
    {
        LogHelper.info("Tank paused");
    }

    @Override
    protected void onMachineDisassembled()
    {
        LogHelper.info("Tank disassembled");
    }

    @Override
    protected int getMinimumNumberOfBlocksForAssembledMachine()
    {
        return getMinimumXSize() * getMinimumZSize() * getMinimumYSize() - 7;
    }

    @Override
    protected int getMaximumXSize()
    {
        return 5;
    }

    @Override
    protected int getMinimumXSize() { return 3; }

    @Override
    protected int getMaximumZSize()
    {
        return 5;
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

    }

    @Override
    protected void onAssimilated(MultiblockControllerBase assimilator)
    {

    }

    @Override
    protected boolean updateServer()
    {
        return false;
    }

    @Override
    protected void updateClient() { }

    @Override
    public void writeToNBT(NBTTagCompound data)
    {
        data.setBoolean("tankActive", this.isActive());
    }

    @Override
    public void readFromNBT(NBTTagCompound data)
    {
        if (data.hasKey("tankActive"))
        {
            setActive(data.getBoolean("tankActive"));
        }
    }

    @Override
    public IMessage formatMessage()
    {
        return null;
    }

    @Override
    public void decodeMessage(IMessage message)
    {

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
}
