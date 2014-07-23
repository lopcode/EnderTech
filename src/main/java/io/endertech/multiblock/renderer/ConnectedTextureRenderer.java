package io.endertech.multiblock.renderer;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import io.endertech.proxy.ClientProxy;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;

public class ConnectedTextureRenderer implements ISimpleBlockRenderingHandler
{
    public static ConnectedRenderBlocks connectedRenderer = new ConnectedRenderBlocks();

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer)
    {
        return;
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
        return ClientProxy.connectedTexturesRenderID;
    }
}
