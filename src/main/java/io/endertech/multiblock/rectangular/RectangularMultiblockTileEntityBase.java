package io.endertech.multiblock.rectangular;

import io.endertech.multiblock.MultiblockControllerBase;
import io.endertech.multiblock.MultiblockTileEntityBase;
import io.endertech.multiblock.MultiblockValidationException;
import io.endertech.util.BlockCoord;
import net.minecraftforge.common.util.ForgeDirection;
import java.util.HashSet;
import java.util.Set;

public abstract class RectangularMultiblockTileEntityBase extends MultiblockTileEntityBase
{
    PartPosition position;
    Set<ForgeDirection> outwards;

    public RectangularMultiblockTileEntityBase()
    {
        super();

        position = PartPosition.Unknown;
        outwards = new HashSet<ForgeDirection>();
    }

    // Positional Data
    public Set<ForgeDirection> getOutwardsDir()
    {
        return outwards;
    }

    public ForgeDirection getFirstOutwardsDir()
    {
        if (outwards == null || outwards.isEmpty())
            return null;
        else
            return outwards.iterator().next();
    }

    public PartPosition getPartPosition()
    {
        return position;
    }

    // Handlers from MultiblockTileEntityBase
    @Override
    public void onAttached(MultiblockControllerBase newController)
    {
        super.onAttached(newController);
        recalculateOutwardsDirection(newController.getMinimumCoord(), newController.getMaximumCoord());
    }


    @Override
    public void onMachineAssembled(MultiblockControllerBase controller)
    {
        BlockCoord maxCoord = controller.getMaximumCoord();
        BlockCoord minCoord = controller.getMinimumCoord();

        // Discover where I am on the reactor
        recalculateOutwardsDirection(minCoord, maxCoord);
    }

    @Override
    public void onMachineBroken()
    {
        position = PartPosition.Unknown;
        outwards = new HashSet<ForgeDirection>();
    }

    private boolean isOnBottomFace(BlockCoord minCoord, BlockCoord coord)
    {
        return minCoord.y == coord.y;
    }

    private boolean isOnTopFace(BlockCoord maxCoord, BlockCoord coord)
    {
        return maxCoord.y == coord.y;
    }

    private boolean isOnWestFace(BlockCoord minCoord, BlockCoord coord)
    {
        return minCoord.x == coord.x;
    }

    private boolean isOnEastFace(BlockCoord maxCoord, BlockCoord coord)
    {
        return maxCoord.x == coord.x;
    }

    private boolean isOnFrontFace(BlockCoord minCoord, BlockCoord coord)
    {
        return minCoord.z == coord.z;
    }

    private boolean isOnBackFace(BlockCoord maxCoord, BlockCoord coord)
    {
        return maxCoord.z == coord.z;
    }

    // Positional helpers
    public void recalculateOutwardsDirection(BlockCoord minCoord, BlockCoord maxCoord)
    {
        position = PartPosition.Unknown;

        BlockCoord coord = new BlockCoord(this.xCoord, this.yCoord, this.zCoord);

        int facesMatching = 0;
        if (maxCoord.x == coord.x || minCoord.x == coord.x) { facesMatching++; }
        if (maxCoord.y == coord.y || minCoord.y == coord.y) { facesMatching++; }
        if (maxCoord.z == coord.z || minCoord.z == coord.z) { facesMatching++; }

        if (facesMatching <= 0)
        {
            position = PartPosition.Interior;
        } else if (facesMatching >= 3)
        {
            position = PartPosition.FrameCorner;
        } else if (facesMatching == 2)
        {
            position = PartPosition.Frame;
        } else
        {
            // 1 face matches
            if (maxCoord.x == this.xCoord)
            {
                position = PartPosition.EastFace;
            } else if (minCoord.x == this.xCoord)
            {
                position = PartPosition.WestFace;
            } else if (maxCoord.z == this.zCoord)
            {
                position = PartPosition.SouthFace;
            } else if (minCoord.z == this.zCoord)
            {
                position = PartPosition.NorthFace;
            } else if (maxCoord.y == this.yCoord)
            {
                position = PartPosition.TopFace;
            } else
            {
                position = PartPosition.BottomFace;
            }
        }

        if (isOnTopFace(maxCoord, coord)) outwards.add(ForgeDirection.UP);

        if (isOnBottomFace(minCoord, coord)) outwards.add(ForgeDirection.DOWN);

        if (isOnEastFace(maxCoord, coord)) outwards.add(ForgeDirection.EAST);

        if (isOnWestFace(minCoord, coord)) outwards.add(ForgeDirection.WEST);

        if (isOnFrontFace(maxCoord, coord)) outwards.add(ForgeDirection.SOUTH);

        if (isOnBackFace(minCoord, coord)) outwards.add(ForgeDirection.NORTH);
    }

    ///// Validation Helpers (IMultiblockPart)
    public abstract void isGoodForFrame() throws MultiblockValidationException;

    public abstract void isGoodForSides() throws MultiblockValidationException;

    public abstract void isGoodForTop() throws MultiblockValidationException;

    public abstract void isGoodForBottom() throws MultiblockValidationException;

    public abstract void isGoodForInterior() throws MultiblockValidationException;
}
