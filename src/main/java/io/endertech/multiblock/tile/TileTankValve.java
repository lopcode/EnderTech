package io.endertech.multiblock.tile;

import cpw.mods.fml.common.registry.GameRegistry;
import io.endertech.block.ETBlocks;
import io.endertech.multiblock.MultiblockControllerBase;
import io.endertech.multiblock.MultiblockValidationException;
import io.endertech.multiblock.controller.ControllerTank;
import io.endertech.reference.Strings;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import java.util.Set;

public class TileTankValve extends TileTankPart implements IFluidHandler
{
    public static void init()
    {
        GameRegistry.registerTileEntity(TileTankValve.class, "tile." + Strings.Blocks.TANK_VALVE_NAME);
    }

    @Override
    public void isGoodForFrame() throws MultiblockValidationException
    {

    }

    @Override
    public void isGoodForSides() throws MultiblockValidationException
    {
        throw new MultiblockValidationException("Tank valves cannot be used for tank sides (only the frame).");
    }

    @Override
    public void isGoodForTop() throws MultiblockValidationException
    {
        throw new MultiblockValidationException("Tank valves cannot be used for tank top (only the frame).");
    }

    @Override
    public void isGoodForBottom() throws MultiblockValidationException
    {
        throw new MultiblockValidationException("Tank valves cannot be used for tank bottom (only the frame).");
    }

    @Override
    public void isGoodForInterior() throws MultiblockValidationException
    {
        throw new MultiblockValidationException("Tank valves cannot be used for tank interior.");
    }

    private boolean canInteractFromDirection(ForgeDirection from)
    {
        return (isConnected() && this.getTankController().isAssembled() && getOutwardsDir().contains(from));
    }

    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill)
    {
        if (!canInteractFromDirection(from)) { return 0; }

        ControllerTank controller = this.getTankController();
        return controller.tank.fill(resource, doFill);
    }

    @Override
    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain)
    {
        if (!canInteractFromDirection(from)) { return null; }

        if (this.canDrain(from, null))
        {
            return this.drain(from, resource.amount, doDrain);
        } else
        {
            return null;
        }
    }

    @Override
    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain)
    {
        if (!canInteractFromDirection(from)) { return null; }

        if (this.canDrain(from, null))
        {
            return this.getTankController().tank.drain(maxDrain, doDrain);
        } else
        {
            return null;
        }
    }

    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid)
    {
        return canInteractFromDirection(from);
    }

    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid)
    {
        return canInteractFromDirection(from);
    }

    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection from)
    {
        if (!canInteractFromDirection(from)) { return null; }
        return new FluidTankInfo[] {this.getTankController().tank.getInfo()};
    }

    @Override
    public void onMachineAssembled(MultiblockControllerBase controllerBase)
    {
        super.onMachineAssembled(controllerBase);

        Set<ForgeDirection> outs = this.getOutwardsDir();
        for (ForgeDirection out : outs)
            worldObj.notifyBlockOfNeighborChange(xCoord + out.offsetX, yCoord + out.offsetY, zCoord + out.offsetZ, ETBlocks.blockTankPart);
    }

    @Override
    public void onMachineBroken()
    {
        super.onMachineBroken();

        Set<ForgeDirection> outs = this.getOutwardsDir();
        for (ForgeDirection out : outs)
            worldObj.notifyBlockOfNeighborChange(xCoord + out.offsetX, yCoord + out.offsetY, zCoord + out.offsetZ, ETBlocks.blockTankPart);
    }
}
