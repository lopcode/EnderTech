package io.endertech.integration.waila;

import io.endertech.multiblock.IMultiblockPart;
import io.endertech.multiblock.MultiblockControllerBase;
import io.endertech.multiblock.MultiblockTileEntityBase;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaRegistrar;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import java.util.List;

public class MultiblockWailaProvider implements IWailaDataProvider
{
    @Override
    public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        return null;
    }

    @Override
    public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        TileEntity tile = accessor.getTileEntity();
        if (tile instanceof IMultiblockPart)
        {
            IMultiblockPart multiblock = (IMultiblockPart) tile;
            MultiblockControllerBase controller = multiblock.getMultiblockController();

            if (controller.isAssembled())
            {
                currenttip.clear();
                currenttip.add(EnumChatFormatting.WHITE + controller.getName() + EnumChatFormatting.RESET);
                return currenttip;
            }
        }

        return currenttip;
    }

    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        TileEntity tile = accessor.getTileEntity();
        if (tile instanceof IMultiblockPart)
        {
            IMultiblockPart multiblock = (IMultiblockPart) tile;
            MultiblockControllerBase controller = multiblock.getMultiblockController();

            String assembledStatus = "Assembled: ";
            boolean assembled = (controller != null) && (controller.isAssembled());

            if (!assembled)
            {
                assembledStatus += EnumChatFormatting.RED + "no" + EnumChatFormatting.RESET;
            } else
            {
                assembledStatus += EnumChatFormatting.GREEN + "yes" + EnumChatFormatting.RESET;
            }

            currenttip.add(assembledStatus);

            if (controller != null)
            {
                List<String> body = controller.getWailaBody();
                if (body != null) currenttip.addAll(body);
            }
        }
        return currenttip;
    }

    @Override
    public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        return currenttip;
    }

    public static void callbackRegister(IWailaRegistrar registrar)
    {
        registrar.registerHeadProvider(new MultiblockWailaProvider(), MultiblockTileEntityBase.class);
        registrar.registerBodyProvider(new MultiblockWailaProvider(), MultiblockTileEntityBase.class);
    }
}