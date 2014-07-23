package io.endertech.multiblock.renderer;

import io.endertech.multiblock.texture.ConnectedTextureIcon;
import io.endertech.util.BlockHelper;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

public class ConnectedRenderBlocks extends RenderBlocks
{
    private Block block;
    private int meta;

    public void setBlockToCompareTo(Block block, int meta)
    {
        this.block = block;
        this.meta = meta;
    }

    public void setBlockAccess(IBlockAccess blockAccess)
    {
        this.blockAccess = blockAccess;
    }

    public static final ForgeDirection neighborsBySide[][] = new ForgeDirection[][] {{ForgeDirection.NORTH, ForgeDirection.SOUTH, ForgeDirection.WEST, ForgeDirection.EAST}, {ForgeDirection.NORTH, ForgeDirection.SOUTH, ForgeDirection.WEST, ForgeDirection.EAST}, {ForgeDirection.UP, ForgeDirection.DOWN, ForgeDirection.EAST, ForgeDirection.WEST}, {ForgeDirection.UP, ForgeDirection.DOWN, ForgeDirection.WEST, ForgeDirection.EAST}, {ForgeDirection.UP, ForgeDirection.DOWN, ForgeDirection.NORTH, ForgeDirection.SOUTH}, {ForgeDirection.UP, ForgeDirection.DOWN, ForgeDirection.SOUTH, ForgeDirection.NORTH}};

    public static boolean checkBit(int a, int b)
    {
        return (a & b) == b;
    }

    public void renderSideFace(ConnectedTextureIcon icon, int index, double x, double y, double z, int side)
    {
        icon.setCurrentRenderIcon(index);
        switch (side)
        {
            case 0:
                super.renderFaceYNeg(block, x, y, z, icon);
                break;
            case 1:
                super.renderFaceYPos(block, x, y, z, icon);
                break;
            case 2:
                super.renderFaceZNeg(block, x, y, z, icon);
                break;
            case 3:
                super.renderFaceZPos(block, x, y, z, icon);
                break;
            case 4:
                super.renderFaceXNeg(block, x, y, z, icon);
                break;
            case 5:
                super.renderFaceXPos(block, x, y, z, icon);
        }

    }


    public void renderSide(Block tBlock, double x, double y, double z, ConnectedTextureIcon icon, int side)
    {
        int ix = (int) x;
        int iy = (int) y;
        int iz = (int) z;
        ForgeDirection dir;

        // First check if we have a block in front of us of the same type - if so, just be completely transparent on this side
        ForgeDirection out = ForgeDirection.getOrientation(side);
        if (BlockHelper.areBlocksEqual(blockAccess, block, meta, ix, iy, iz, out))
        {
            return;
        }

        renderSideFace(icon, 0, x, y, z, side);

        // Calculate icon index based on whether the blocks around this block match it
        // 1 = Connected on top, 2 = connected on bottom, 4 = connected on left, 8 = connected on right
        int iconIdx = 0;
        for (int i = 0; i < 4; i++)
        {
            dir = neighborsBySide[side][i];
            // Same blockID and metadata on this side?
            if (BlockHelper.areBlocksEqual(blockAccess, block, meta, ix, iy, iz, dir))
            {
                // Connected!
                iconIdx |= 1 << i;
            }
        }

        if (!checkBit(iconIdx, 1))
        {
            renderSideFace(icon, ConnectedTextureIcon.TOP, x, y, z, side);
        }

        if (!checkBit(iconIdx, 2))
        {
            renderSideFace(icon, ConnectedTextureIcon.BOTTOM, x, y, z, side);
        }

        if (!checkBit(iconIdx, 4))
        {
            renderSideFace(icon, ConnectedTextureIcon.LEFT, x, y, z, side);
        }

        if (!checkBit(iconIdx, 8))
        {
            renderSideFace(icon, ConnectedTextureIcon.RIGHT, x, y, z, side);
        }
        // Potential for corners:
        //  eg connected top+left = render top left corner

        icon.setCurrentRenderIcon(0);
        return;
    }

    @Override
    public void renderFaceYNeg(Block block, double x, double y, double z, IIcon icon)
    {
        if (this.hasOverrideBlockTexture()) icon = this.overrideBlockTexture;

        if (icon instanceof ConnectedTextureIcon) this.renderSide(block, x, y, z, (ConnectedTextureIcon) icon, 0);
        else super.renderFaceYNeg(block, x, y, z, icon);
    }

    @Override
    public void renderFaceYPos(Block block, double x, double y, double z, IIcon icon)
    {
        if (this.hasOverrideBlockTexture()) icon = this.overrideBlockTexture;

        if (icon instanceof ConnectedTextureIcon) this.renderSide(block, x, y, z, (ConnectedTextureIcon) icon, 1);
        else super.renderFaceYPos(block, x, y, z, icon);
    }

    @Override
    public void renderFaceZNeg(Block block, double x, double y, double z, IIcon icon)
    {
        if (this.hasOverrideBlockTexture()) icon = this.overrideBlockTexture;

        if (icon instanceof ConnectedTextureIcon) this.renderSide(block, x, y, z, (ConnectedTextureIcon) icon, 2);
        else super.renderFaceZNeg(block, x, y, z, icon);
    }

    @Override
    public void renderFaceZPos(Block block, double x, double y, double z, IIcon icon)
    {
        if (this.hasOverrideBlockTexture()) icon = this.overrideBlockTexture;

        if (icon instanceof ConnectedTextureIcon) this.renderSide(block, x, y, z, (ConnectedTextureIcon) icon, 3);
        else super.renderFaceZPos(block, x, y, z, icon);
    }

    @Override
    public void renderFaceXNeg(Block block, double x, double y, double z, IIcon icon)
    {
        if (this.hasOverrideBlockTexture()) icon = this.overrideBlockTexture;

        if (icon instanceof ConnectedTextureIcon) this.renderSide(block, x, y, z, (ConnectedTextureIcon) icon, 4);
        else super.renderFaceXNeg(block, x, y, z, icon);
    }

    @Override
    public void renderFaceXPos(Block block, double x, double y, double z, IIcon icon)
    {
        if (this.hasOverrideBlockTexture()) icon = this.overrideBlockTexture;

        if (icon instanceof ConnectedTextureIcon) this.renderSide(block, x, y, z, (ConnectedTextureIcon) icon, 5);
        else super.renderFaceXPos(block, x, y, z, icon);
    }
}
