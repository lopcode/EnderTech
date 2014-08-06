package io.endertech.multiblock.tile;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.endertech.block.ETBlocks;
import io.endertech.multiblock.MultiblockControllerBase;
import io.endertech.multiblock.MultiblockValidationException;
import io.endertech.multiblock.block.BlockTankController;
import io.endertech.multiblock.controller.ControllerTank;
import io.endertech.reference.Strings;
import io.endertech.util.BlockCoord;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.ForgeDirection;
import java.util.Set;

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
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox()
    {
        if (!this.isConnected()) return INFINITE_EXTENT_AABB;
        else
        {
            ControllerTank controller = this.getTankController();
            BlockCoord min = controller.getMinimumCoord();
            BlockCoord max = controller.getMaximumCoord();

            return AxisAlignedBB.getBoundingBox(min.x, min.y, min.z, max.x, max.y, max.z);
        }
    }

    @Override
    public double getMaxRenderDistanceSquared()
    {
        return 16384D;
    }

    @Override
    public void onMachineAssembled(MultiblockControllerBase controller)
    {
        super.onMachineAssembled(controller);

        Set<ForgeDirection> out = this.getOutwardsDir();
        if (out.isEmpty())
        {
            this.setOrientation(ForgeDirection.SOUTH);
        } else if (!out.contains(this.getOrientation()))
        {
            for (int i = 2; i < 6; i++)
            {
                ForgeDirection orientation = ForgeDirection.getOrientation(i);
                if (out.contains(orientation))
                {
                    this.setOrientation(orientation);
                    return;
                }
            }
        }
    }

    @Override
    public void onMachineBroken()
    {
        super.onMachineBroken();

        if (this.worldObj.isRemote) { return; }

        if (blockType == ETBlocks.blockTankController)
        {
            int metadata = this.getBlockMetadata();

            if (BlockTankController.isController(metadata))
            {
                this.worldObj.setBlockMetadataWithNotify(this.xCoord, this.yCoord, this.zCoord, BlockTankController.CONTROLLER_METADATA_BASE, 2);
            }
        }
    }
}
