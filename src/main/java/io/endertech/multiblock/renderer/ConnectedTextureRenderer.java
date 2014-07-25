package io.endertech.multiblock.renderer;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import io.endertech.multiblock.texture.ConnectedTextureIcon;
import io.endertech.proxy.CommonProxy;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import org.lwjgl.opengl.GL11;

public class ConnectedTextureRenderer implements ISimpleBlockRenderingHandler
{
    public static ConnectedRenderBlocks connectedRenderer = new ConnectedRenderBlocks();

    public void renderWholeSide(Block block, int metadata, RenderBlocks renderer, int side, float nx, float ny, float nz, double dx, double dy, double dz)
    {
        Tessellator tessellator = Tessellator.instance;
        ConnectedTextureIcon icon = (ConnectedTextureIcon) block.getIcon(side, metadata);
        for (int i = 0; i < ConnectedTextureIcon.ICON_TYPES.length; i++)
        {
            icon.setCurrentRenderIcon(i);
            tessellator.startDrawingQuads();
            tessellator.setNormal(nx, ny, nz);

            switch (side)
            {
                case 0:
                    renderer.renderFaceYNeg(block, dx, dy, dz, icon.getCurrentRenderIcon());
                    break;
                case 1:
                    renderer.renderFaceYPos(block, dx, dy, dz, icon.getCurrentRenderIcon());
                    break;
                case 2:
                    renderer.renderFaceXNeg(block, dx, dy, dz, icon.getCurrentRenderIcon());
                    break;
                case 3:
                    renderer.renderFaceXPos(block, dx, dy, dz, icon.getCurrentRenderIcon());
                    break;
                case 4:
                    renderer.renderFaceZNeg(block, dx, dy, dz, icon.getCurrentRenderIcon());
                    break;
                case 5:
                    renderer.renderFaceZPos(block, dx, dy, dz, icon.getCurrentRenderIcon());
                    break;
            }

            tessellator.draw();
        }
    }

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer)
    {
        block.setBlockBoundsForItemRender();
        renderer.setRenderBoundsFromBlock(block);
        GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
        GL11.glTranslatef(-0.5F, 0F, -0.5F);

        IIcon test_icon = block.getIcon(0, metadata);
        if (test_icon == null || !(test_icon instanceof ConnectedTextureIcon))
        {
            return;
        }

        renderWholeSide(block, metadata, renderer, 0, 0.0F, -1.0F, 0.0F, 0.0D, -0.5D, 0.0D);
        renderWholeSide(block, metadata, renderer, 1, 0.0F, 1.0F, 0.0F, 0.0D, -0.5D, 0.0D);
        renderWholeSide(block, metadata, renderer, 2, 0.0F, 0.0F, -1.0F, 0.0D, -0.5D, 0.0D);
        renderWholeSide(block, metadata, renderer, 3, 0.0F, 0.0F, 1.0F, 0.0D, -0.5D, 0.0D);
        renderWholeSide(block, metadata, renderer, 4, -1.0F, 0.0F, 0.0F, 0.0D, -0.5D, 0.0D);
        renderWholeSide(block, metadata, renderer, 5, 1.0F, 0.0F, 0.0F, 0.0D, -0.5D, 0.0D);

        GL11.glTranslatef(0.5F, 0.5F, 0.5F);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer)
    {
        this.connectedRenderer.setBlockAccess(world);
        this.connectedRenderer.setBlockToCompareTo(world.getBlock(x, y, z), world.getBlockMetadata(x, y, z));

        block.setBlockBoundsBasedOnState(this.connectedRenderer.blockAccess, x, y, z);
        this.connectedRenderer.setRenderBoundsFromBlock(block);

        return this.connectedRenderer.renderStandardBlock(block, x, y, z);
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId)
    {
        return true;
    }

    @Override
    public int getRenderId()
    {
        return CommonProxy.connectedTexturesRenderID;
    }
}
