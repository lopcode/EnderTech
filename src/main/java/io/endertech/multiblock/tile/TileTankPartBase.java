package io.endertech.multiblock.tile;

import io.endertech.multiblock.IMultiblockPart;
import io.endertech.multiblock.MultiblockControllerBase;
import io.endertech.multiblock.block.BlockTankPart;
import io.endertech.multiblock.controller.ControllerTank;
import io.endertech.multiblock.rectangular.RectangularMultiblockTileEntityBase;
import io.endertech.util.BlockCoord;
import io.endertech.util.IOutlineDrawer;
import io.endertech.util.RGBA;
import io.endertech.util.RenderHelper;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import java.util.Set;

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
        World world = event.player.worldObj;

        MultiblockControllerBase controller = this.getMultiblockController();
        if (controller == null)
        {
            RenderHelper.renderBlockOutline(event.context, event.player, target, RGBA.Red.setAlpha(0.6f), 2.0f, event.partialTicks);
            return true;
        }

        Set<IMultiblockPart> connectedParts = controller.getConnectedParts();
        if (connectedParts.isEmpty())
        {
            RenderHelper.renderBlockOutline(event.context, event.player, target, RGBA.White.setAlpha(0.6f), 2.0f, event.partialTicks);
        }

        RGBA colour = RGBA.Blue.setAlpha(0.6f);
        if (controller.isAssembled())
        {
            colour = RGBA.Green.setAlpha(0.6f);
        }

        for (IMultiblockPart part : connectedParts)
        {
            BlockCoord partCoord = part.getWorldLocation();

            if (BlockTankPart.isController(world.getBlockMetadata(partCoord.x, partCoord.y, partCoord.z)))
                RenderHelper.renderBlockOutline(event.context, event.player, partCoord, RGBA.White.setAlpha(0.6f), 10.0f, event.partialTicks);
            else
                RenderHelper.renderBlockOutline(event.context, event.player, partCoord, colour, 2.0f, event.partialTicks);

            if (part.isMultiblockSaveDelegate())
                RenderHelper.renderBlockOutline(event.context, event.player, partCoord, RGBA.Red, 10.0f, event.partialTicks);
        }


        return true;
    }
}
