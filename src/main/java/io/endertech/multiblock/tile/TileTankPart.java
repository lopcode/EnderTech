package io.endertech.multiblock.tile;

import cpw.mods.fml.common.registry.GameRegistry;
import io.endertech.block.ETBlocks;
import io.endertech.multiblock.MultiblockControllerBase;
import io.endertech.multiblock.MultiblockValidationException;
import io.endertech.multiblock.block.BlockTankPart;
import io.endertech.reference.Strings;
import io.endertech.util.BlockCoord;
import io.endertech.util.LogHelper;

public class TileTankPart extends TileTankPartBase
{
    public TileTankPart()
    {
        super();
    }

    public static void init()
    {
        GameRegistry.registerTileEntity(TileTankPart.class, "tile." + Strings.Blocks.TANK_PART_NAME);
    }

    @Override
    public void isGoodForFrame() throws MultiblockValidationException
    {
        int metadata = this.worldObj.getBlockMetadata(this.xCoord, this.yCoord, this.zCoord);
        if (BlockTankPart.isFrame(metadata)) { return; }

        throw new MultiblockValidationException(String.format("%d, %d, %d - Only frames may be used as part of a tank's frame", xCoord, yCoord, zCoord));
    }

    @Override
    public void isGoodForSides() throws MultiblockValidationException
    {
        // All parts are valid for sides, by default
    }

    @Override
    public void isGoodForTop() throws MultiblockValidationException
    {
        int metadata = this.worldObj.getBlockMetadata(this.xCoord, this.yCoord, this.zCoord);
        if (BlockTankPart.isFrame(metadata)) { return; }

        throw new MultiblockValidationException(String.format("%d, %d, %d - This part may not be placed on a tank's top face", xCoord, yCoord, zCoord));
    }

    @Override
    public void isGoodForBottom() throws MultiblockValidationException
    {
        int metadata = this.worldObj.getBlockMetadata(this.xCoord, this.yCoord, this.zCoord);
        if (BlockTankPart.isFrame(metadata)) { return; }

        throw new MultiblockValidationException(String.format("%d, %d, %d - This part may not be placed on a tank's bottom face", xCoord, yCoord, zCoord));
    }

    @Override
    public void isGoodForInterior() throws MultiblockValidationException
    {
        throw new MultiblockValidationException(String.format("%d, %d, %d - This part may not be placed in the tank's interior", xCoord, yCoord, zCoord));
    }

    @Override
    public void onMachineActivated()
    {
        if (this.worldObj.isRemote) { return; }

        if (getBlockType() == ETBlocks.blockTankPart)
        {
            int metadata = this.getBlockMetadata();
            if (BlockTankPart.isController(metadata))
            {
                this.worldObj.setBlockMetadataWithNotify(this.xCoord, this.yCoord, this.zCoord, BlockTankPart.CONTROLLER_ACTIVE, 2);
            }
        }
    }

    @Override
    public void onMachineDeactivated()
    {
        if (this.worldObj.isRemote) { return; }

        if (getBlockType() == ETBlocks.blockTankPart)
        {
            int metadata = this.getBlockMetadata();
            if (BlockTankPart.isController(metadata))
            {
                this.worldObj.setBlockMetadataWithNotify(this.xCoord, this.yCoord, this.zCoord, BlockTankPart.CONTROLLER_IDLE, 2);
            }
        }
    }

    @Override
    public void onMachineAssembled(MultiblockControllerBase multiblockController)
    {
        super.onMachineAssembled(multiblockController);

        if (this.worldObj.isRemote) { return; }
        if (multiblockController == null)
        {
            throw new IllegalArgumentException("Being assembled into a null controller. This should never happen. Please report this stacktrace to http://github.com/Drayshak/EnderTech/");
        }

        if (this.getMultiblockController() == null)
        {
            LogHelper.warn("Reactor part at (%d, %d, %d) is being assembled without being attached to a reactor. Attempting to auto-heal. Fully destroying and re-building this reactor is recommended if errors persist.", xCoord, yCoord, zCoord);
            this.onAttached(multiblockController);
        }

        if (getBlockType() == ETBlocks.blockTankPart)
        {
            int metadata = this.getBlockMetadata();
            if (BlockTankPart.isFrame(metadata))
            {
                this.setCasingMetadataBasedOnWorldPosition();
            } else if (BlockTankPart.isController(metadata))
            {
                // This is called during world loading as well, so controllers can start active.
                if (!this.getTankController().isActive())
                {
                    this.worldObj.setBlockMetadataWithNotify(this.xCoord, this.yCoord, this.zCoord, BlockTankPart.CONTROLLER_IDLE, 2);
                } else
                {
                    this.worldObj.setBlockMetadataWithNotify(this.xCoord, this.yCoord, this.zCoord, BlockTankPart.CONTROLLER_ACTIVE, 2);
                }
            }
        }
    }

    @Override
    public void onMachineBroken()
    {
        super.onMachineBroken();

        if (this.worldObj.isRemote) { return; }

        if (getBlockType() == ETBlocks.blockTankPart)
        {
            int metadata = this.getBlockMetadata();
            if (BlockTankPart.isFrame(metadata))
            {
                this.worldObj.setBlockMetadataWithNotify(this.xCoord, this.yCoord, this.zCoord, BlockTankPart.FRAME_METADATA_BASE, 2);
            } else if (BlockTankPart.isController(metadata))
            {
                this.worldObj.setBlockMetadataWithNotify(this.xCoord, this.yCoord, this.zCoord, BlockTankPart.CONTROLLER_METADATA_BASE, 2);
            }
        }
    }

    private void setCasingMetadataBasedOnWorldPosition()
    {
        MultiblockControllerBase controller = this.getMultiblockController();
        assert (controller != null);
        BlockCoord minCoord = controller.getMinimumCoord();
        BlockCoord maxCoord = controller.getMaximumCoord();

        int extremes = 0;
        boolean xExtreme, yExtreme, zExtreme;
        xExtreme = yExtreme = zExtreme = false;

        if (xCoord == minCoord.x)
        {
            extremes++;
            xExtreme = true;
        }
        if (yCoord == minCoord.y)
        {
            extremes++;
            yExtreme = true;
        }
        if (zCoord == minCoord.z)
        {
            extremes++;
            zExtreme = true;
        }

        if (xCoord == maxCoord.x)
        {
            extremes++;
            xExtreme = true;
        }
        if (yCoord == maxCoord.y)
        {
            extremes++;
            yExtreme = true;
        }
        if (zCoord == maxCoord.z)
        {
            extremes++;
            zExtreme = true;
        }

        if (extremes == 3)
        {
            // Corner
            this.worldObj.setBlockMetadataWithNotify(this.xCoord, this.yCoord, this.zCoord, BlockTankPart.FRAME_CORNER, 2);
        } else if (extremes == 2)
        {
            if (!xExtreme)
            {
                // Y/Z - must be east/west
                this.worldObj.setBlockMetadataWithNotify(this.xCoord, this.yCoord, this.zCoord, BlockTankPart.FRAME_EASTWEST, 2);
            } else if (!zExtreme)
            {
                // X/Y - must be north-south
                this.worldObj.setBlockMetadataWithNotify(this.xCoord, this.yCoord, this.zCoord, BlockTankPart.FRAME_NORTHSOUTH, 2);
            } else
            {
                // Not a y-extreme, must be vertical
                this.worldObj.setBlockMetadataWithNotify(this.xCoord, this.yCoord, this.zCoord, BlockTankPart.FRAME_VERTICAL, 2);
            }
        } else if (extremes == 1)
        {
            this.worldObj.setBlockMetadataWithNotify(this.xCoord, this.yCoord, this.zCoord, BlockTankPart.FRAME_CENTER, 2);
        } else
        {
            // This shouldn't happen.
            this.worldObj.setBlockMetadataWithNotify(this.xCoord, this.yCoord, this.zCoord, BlockTankPart.FRAME_METADATA_BASE, 2);
        }
    }
}
