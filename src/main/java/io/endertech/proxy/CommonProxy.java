package io.endertech.proxy;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.endertech.handler.WorldEventHandler;
import io.endertech.modules.dev.handler.MappingEventHandler;
import io.endertech.multiblock.handler.MultiblockEventHandler;
import io.endertech.multiblock.handler.MultiblockServerTickHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;

public class CommonProxy implements IGuiHandler
{
    public static int connectedTexturesRenderID = 0;

    public void registerTickerHandlers()
    {
        FMLCommonHandler.instance().bus().register(new WorldEventHandler());
        FMLCommonHandler.instance().bus().register(new MultiblockServerTickHandler());
        MinecraftForge.EVENT_BUS.register(new MultiblockEventHandler());

        FMLCommonHandler.instance().bus().register(new MappingEventHandler());
    }

    public void registerTESRs() { }

    public void registerRenderers() { }

    public void registerItemRenderers() { }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        return null;
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void registerIcons(TextureStitchEvent.Pre event)
    {
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void initializeIcons(TextureStitchEvent.Post event)
    {
    }
}
