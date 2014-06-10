package io.endertech.block;

import io.endertech.EnderTech;
import io.endertech.lib.Strings;
import io.endertech.tile.TileTank;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockTank extends BlockContainer
{
    public static ItemStack itemTank;

    public BlockTank(int id)
    {
        super(id, Material.glass);
        setCreativeTab(EnderTech.tabET);
        setUnlocalizedName(Strings.TANK_TILE_NAME);
    }

    @Override
    public TileEntity createNewTileEntity(World world)
    {
        return new TileTank();
    }

    public void init()
    {
        TileTank.init();

        itemTank = new ItemStack(this, 1, 0);
    }
}
