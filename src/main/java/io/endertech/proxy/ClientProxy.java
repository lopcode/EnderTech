package io.endertech.proxy;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import io.endertech.client.handler.DrawBlockHighlightEventHandler;
import io.endertech.client.handler.KeyBindingHandler;
import io.endertech.client.renderer.SpinningCubeRenderer;
import io.endertech.multiblock.handler.MultiblockClientTickHandler;
import io.endertech.multiblock.renderer.ConnectedTextureRenderer;
import io.endertech.tile.TileSpinningCube;
import net.minecraftforge.common.MinecraftForge;

public class ClientProxy extends CommonProxy
{
    public static int connectedTexturesRenderID = 0;

    @Override
    public void registerTickerHandlers()
    {
        super.registerTickerHandlers();

        FMLCommonHandler.instance().bus().register(new KeyBindingHandler());
        FMLCommonHandler.instance().bus().register(new MultiblockClientTickHandler());
        MinecraftForge.EVENT_BUS.register(new DrawBlockHighlightEventHandler());
    }

    @Override
    public void registerTESRs()
    {
        ClientRegistry.bindTileEntitySpecialRenderer(TileSpinningCube.class, new SpinningCubeRenderer());
    }

    @Override
    public void registerRenderers()
    {
        connectedTexturesRenderID = RenderingRegistry.getNextAvailableRenderId();
        RenderingRegistry.registerBlockHandler(new ConnectedTextureRenderer());
    }
}
