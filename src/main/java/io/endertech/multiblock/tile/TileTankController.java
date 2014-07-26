package io.endertech.multiblock.tile;

import cpw.mods.fml.common.registry.GameRegistry;
import io.endertech.multiblock.MultiblockValidationException;
import io.endertech.reference.Strings;

public class TileTankController extends TileTankPart
{
    public static void init()
    {
        GameRegistry.registerTileEntity(TileTankController.class, "tile." + Strings.Blocks.TANK_CONTROLLER_NAME);
    }

    @Override
    public void isGoodForFrame() throws MultiblockValidationException
    {

    }

    @Override
    public void isGoodForSides() throws MultiblockValidationException
    {
        throw new MultiblockValidationException("Controllers cannot be used for tank sides (only the frame).");
    }

    @Override
    public void isGoodForTop() throws MultiblockValidationException
    {
        throw new MultiblockValidationException("Controllers cannot be used for tank top (only the frame).");
    }

    @Override
    public void isGoodForBottom() throws MultiblockValidationException
    {
        throw new MultiblockValidationException("Controllers cannot be used for tank bottom (only the frame).");
    }

    @Override
    public void isGoodForInterior() throws MultiblockValidationException
    {
        throw new MultiblockValidationException("Controllers cannot be used for tank interior (only the frame).");
    }

    @Override
    public void onMachineActivated()
    {

    }

    @Override
    public void onMachineDeactivated()
    {

    }
}
