package io.endertech.multiblock.tile;

import cpw.mods.fml.common.registry.GameRegistry;
import io.endertech.multiblock.MultiblockValidationException;
import io.endertech.reference.Strings;

public class TileTankGlass extends TileTankPart
{
    public static void init()
    {
        GameRegistry.registerTileEntity(TileTankGlass.class, "tile.endertech." + Strings.Blocks.MULTIBLOCK_GLASS_NAME);
    }

    @Override
    public void isGoodForFrame() throws MultiblockValidationException
    {
        throw new MultiblockValidationException("Tank glass cannot be used for tank frame (only the top, bottom and sides).");
    }

    @Override
    public void isGoodForSides() throws MultiblockValidationException
    {

    }

    @Override
    public void isGoodForTop() throws MultiblockValidationException
    {

    }

    @Override
    public void isGoodForBottom() throws MultiblockValidationException
    {

    }

    @Override
    public void isGoodForInterior() throws MultiblockValidationException
    {
        throw new MultiblockValidationException("Tank glass cannot be used for tank interior (only the top, bottom and sides).");
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
