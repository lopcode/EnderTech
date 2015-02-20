package io.endertech.integration.waila;

import io.endertech.multiblock.IMultiblockPart;
import io.endertech.multiblock.MultiblockControllerBase;
import io.endertech.multiblock.MultiblockTileEntityBase;
import io.endertech.util.helper.LocalisationHelper;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaRegistrar;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

import java.util.List;

public class MultiblockWailaProvider implements IWailaDataProvider
{
    public static void callbackRegister(IWailaRegistrar registrar)
    {
        registrar.registerHeadProvider(new MultiblockWailaProvider(), MultiblockTileEntityBase.class);
        registrar.registerBodyProvider(new MultiblockWailaProvider(), MultiblockTileEntityBase.class);
    }

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

            String assembledStatus = LocalisationHelper.localiseString("info.multiblock.assembled_status");
            boolean assembled = (controller != null) && (controller.isAssembled());

            if (!assembled)
            {
                currenttip.add(EnumChatFormatting.RED + LocalisationHelper.localiseString("info.multiblock.not_assembled") + EnumChatFormatting.RESET);
            } else
            {
                if (controller != null)
                {
                    List<String> body = controller.getWailaBody();
                    if (body != null) currenttip.addAll(body);
                }
            }
        }
        return currenttip;
    }

    @Override
    public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        return currenttip;
    }

    @Override
    public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, int x, int y, int z) {
        return tag;
    }
}