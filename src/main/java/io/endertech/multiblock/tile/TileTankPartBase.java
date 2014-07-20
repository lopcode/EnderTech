package io.endertech.multiblock.tile;

import io.endertech.multiblock.MultiblockControllerBase;
import io.endertech.multiblock.controller.ControllerTank;
import io.endertech.multiblock.rectangular.RectangularMultiblockTileEntityBase;

public abstract class TileTankPartBase extends RectangularMultiblockTileEntityBase
{
    public TileTankPartBase() { }

    @Override
    public MultiblockControllerBase createNewMultiblock()
    {
        return new ControllerTank(this.worldObj);
    }

    @Override
    public Class<? extends MultiblockControllerBase> getMultiblockControllerType()
    {
        return ControllerTank.class;
    }

    @Override
    public boolean canUpdate()
    {
        return false;
    }

    public ControllerTank getTankController() { return (ControllerTank) this.getMultiblockController(); }
}
