package io.endertech.gui;

import io.endertech.tile.TileET;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiHandler// implements IGuiHandler
{
    public static final int TILE_ID = 0;

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        switch (ID)
        {
            case TILE_ID:
                TileEntity tileEntity = world.getTileEntity(x, y, z);
                if (tileEntity instanceof TileET)
                {
                    return ((TileET) tileEntity).getGuiServer(player.inventory);
                }
                break;
        }

        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        switch (ID)
        {
            case TILE_ID:
                TileEntity tileEntity = world.getTileEntity(x, y, z);
                if (tileEntity instanceof TileET)
                {
                    return ((TileET) tileEntity).getGuiClient(player.inventory);
                }
                break;
        }

        return null;
    }
}
