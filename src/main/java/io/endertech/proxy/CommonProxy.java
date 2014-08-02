package io.endertech.proxy;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.IGuiHandler;
import io.endertech.handler.WorldEventHandler;
import io.endertech.multiblock.handler.MultiblockEventHandler;
import io.endertech.multiblock.handler.MultiblockServerTickHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class CommonProxy implements IGuiHandler
{
    public static int connectedTexturesRenderID = 0;

    public void registerTickerHandlers()
    {
        FMLCommonHandler.instance().bus().register(new WorldEventHandler());
        FMLCommonHandler.instance().bus().register(new MultiblockServerTickHandler());
        MinecraftForge.EVENT_BUS.register(new MultiblockEventHandler());
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
}
