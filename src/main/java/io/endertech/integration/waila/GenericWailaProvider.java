package io.endertech.integration.waila;

import io.endertech.multiblock.IMultiblockPart;
import io.endertech.multiblock.MultiblockControllerBase;
import io.endertech.multiblock.MultiblockTileEntityBase;
import io.endertech.tile.TileET;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaRegistrar;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import java.util.List;

public class GenericWailaProvider implements IWailaDataProvider
{
    @Override
    public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        TileEntity tile = accessor.getTileEntity();
        if (tile instanceof IWailaDataProvider)
        {
            return ((IWailaDataProvider) tile).getWailaStack(accessor, config);
        }

        return null;
    }

    @Override
    public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        TileEntity tile = accessor.getTileEntity();
        if (tile instanceof IWailaDataProvider)
        {
            return ((IWailaDataProvider) tile).getWailaHead(itemStack, currenttip, accessor, config);
        }

        return currenttip;
    }

    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        TileEntity tile = accessor.getTileEntity();
        if (tile instanceof IWailaDataProvider)
        {
            return ((IWailaDataProvider) tile).getWailaBody(itemStack, currenttip, accessor, config);
        }

        return currenttip;
    }

    @Override
    public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        TileEntity tile = accessor.getTileEntity();
        if (tile instanceof IWailaDataProvider)
        {
            return ((IWailaDataProvider) tile).getWailaTail(itemStack, currenttip, accessor, config);
        }

        return currenttip;
    }

    public static void callbackRegister(IWailaRegistrar registrar)
    {
        registrar.registerHeadProvider(new GenericWailaProvider(), TileET.class);
        registrar.registerBodyProvider(new GenericWailaProvider(), TileET.class);
    }
}