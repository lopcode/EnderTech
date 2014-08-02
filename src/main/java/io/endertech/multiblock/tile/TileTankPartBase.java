package io.endertech.multiblock.tile;

import io.endertech.config.GeneralConfig;
import io.endertech.multiblock.MultiblockControllerBase;
import io.endertech.multiblock.controller.ControllerTank;
import io.endertech.multiblock.rectangular.RectangularMultiblockTileEntityBase;
import io.endertech.util.BlockCoord;
import io.endertech.util.IOutlineDrawer;
import io.endertech.util.RGBA;
import io.endertech.util.RenderHelper;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;

public abstract class TileTankPartBase extends RectangularMultiblockTileEntityBase implements IOutlineDrawer
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

    @Override
    public boolean drawOutline(DrawBlockHighlightEvent event)
    {
        BlockCoord target = new BlockCoord(event.target.blockX, event.target.blockY, event.target.blockZ);

        MultiblockControllerBase controller = this.getMultiblockController();
        if (controller == null)
        {
            if (GeneralConfig.debugRender)
            {
                RenderHelper.renderBlockOutline(event.context, event.player, target, RGBA.Red.setAlpha(0.6f), 2.0f, event.partialTicks);
                return true;
            } else
            {
                return false;
            }
        }

        return controller.drawOutline(event);
    }
}
