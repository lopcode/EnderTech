package io.endertech.multiblock.tile;

import cofh.api.energy.IEnergyHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import io.endertech.multiblock.MultiblockControllerBase;
import io.endertech.multiblock.MultiblockValidationException;
import io.endertech.multiblock.controller.ControllerTank;
import io.endertech.reference.Strings;
import net.minecraftforge.common.util.ForgeDirection;

public class TileTankEnergyInput extends TileTankPart implements IEnergyHandler
{
    public static final int MAX_INPUT_RATE = 10 * 1000;

    public static void init()
    {
        GameRegistry.registerTileEntity(TileTankEnergyInput.class, "tile." + Strings.Blocks.TANK_ENERGY_INPUT_NAME);
    }

    @Override
    public void isGoodForFrame() throws MultiblockValidationException
    {

    }

    @Override
    public void isGoodForSides() throws MultiblockValidationException
    {
        throw new MultiblockValidationException("Tank energy inputs cannot be used for tank sides (only the frame).");
    }

    @Override
    public void isGoodForTop() throws MultiblockValidationException
    {
        throw new MultiblockValidationException("Tank energy inputs cannot be used for tank top (only the frame).");
    }

    @Override
    public void isGoodForBottom() throws MultiblockValidationException
    {
        throw new MultiblockValidationException("Tank energy inputs cannot be used for tank bottom (only the frame).");
    }

    @Override
    public void isGoodForInterior() throws MultiblockValidationException
    {
        throw new MultiblockValidationException("Tank energy inputs cannot be used for tank interior.");
    }


    @Override
    public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate)
    {
        if (!canInteractFromDirection(from) || !isConnected())
        {
            return 0;
        }

        ControllerTank controller = this.getTankController();
        return controller.receiveEnergy(from, maxReceive, simulate);
    }

    @Override
    public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate)
    {
        return 0;
    }

    @Override
    public int getEnergyStored(ForgeDirection from)
    {
        if (!canInteractFromDirection(from) || !isConnected()) return 0;

        ControllerTank controller = this.getTankController();
        return controller.getStoredEnergy();
    }

    @Override
    public int getMaxEnergyStored(ForgeDirection from)
    {
        if (!canInteractFromDirection(from)) return 0;

        return ControllerTank.MAX_ENERGY_STORAGE;
    }

    @Override
    public boolean canConnectEnergy(ForgeDirection from)
    {
        return canInteractFromDirection(from);
    }

    @Override
    public void onMachineAssembled(MultiblockControllerBase controller)
    {
        super.onMachineAssembled(controller);

        updateOutwardNeighbours();
    }

    @Override
    public void onMachineBroken()
    {
        updateOutwardNeighbours();

        super.onMachineBroken();
    }
}
