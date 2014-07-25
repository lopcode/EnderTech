package io.endertech.multiblock.controller;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.endertech.multiblock.IMultiblockPart;
import io.endertech.multiblock.MultiblockControllerBase;
import io.endertech.multiblock.MultiblockValidationException;
import io.endertech.multiblock.block.BlockTankPart;
import io.endertech.multiblock.rectangular.RectangularMultiblockControllerBase;
import io.endertech.multiblock.tile.TileTankPart;
import io.endertech.util.LogHelper;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import java.util.*;

public class ControllerTank extends RectangularMultiblockControllerBase
{
    protected boolean active;
    private Set<TileTankPart> attachedControllers;
    public int random_number;

    public ControllerTank(World world)
    {
        super(world);
        active = false;
        attachedControllers = new HashSet<TileTankPart>();
        random_number = new Random().nextInt(1000000000);
    }

    @Override
    public void onAttachedPartWithMultiblockData(IMultiblockPart part, NBTTagCompound data)
    {
        this.readFromNBT(data);
    }

    @Override
    protected void onBlockAdded(IMultiblockPart newPart)
    {
        if (newPart instanceof TileTankPart)
        {
            TileTankPart tankPart = (TileTankPart) newPart;
            if (BlockTankPart.isController(tankPart.getBlockMetadata()))
            {
                attachedControllers.add(tankPart);
            }
        }
    }

    @Override
    protected void onBlockRemoved(IMultiblockPart oldPart)
    {
        if (oldPart instanceof TileTankPart)
        {
            TileTankPart tankPart = (TileTankPart) oldPart;
            if (BlockTankPart.isController(tankPart.getBlockMetadata()))
            {
                attachedControllers.remove(tankPart);
            }
        }
    }

    @Override
    protected void isMachineWhole() throws MultiblockValidationException
    {
        if (attachedControllers.size() != 1)
        {
            throw new MultiblockValidationException("You must have 1 controller in the tank structure (currently " + attachedControllers.size() + ")");
        }

        super.isMachineWhole();
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
        if (assimilated instanceof ControllerTank)
        {
            this.random_number = ((ControllerTank) assimilated).random_number;
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
        return false;
    }

    @Override
    protected void updateClient() { }

    @Override
    public void writeToNBT(NBTTagCompound data)
    {
        data.setBoolean("tankActive", this.isActive());
        data.setInteger("randomNumber", this.random_number);
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
            this.random_number = data.getInteger("randomNumber");
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
    public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        return null;
    }

    @Override
    public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        return null;
    }

    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        String activeStatus = "Active: ";
        if (this.isActive())
        {
            activeStatus += EnumChatFormatting.GREEN + "yes" + EnumChatFormatting.RESET;
        } else
        {
            activeStatus += EnumChatFormatting.RED + "no" + EnumChatFormatting.RESET;
        }

        List<String> additions = new ArrayList<String>();
        additions.add(activeStatus);
        return additions;
    }

    @Override
    public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        return null;
    }
}
