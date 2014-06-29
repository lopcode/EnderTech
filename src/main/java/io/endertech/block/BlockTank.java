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

    public BlockTank()
    {
        super(Material.glass);
        this.setCreativeTab(EnderTech.tabET);
        this.setBlockName(Strings.TANK_NAME);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata)
    {
        return new TileTank();
    }

    public void init()
    {
        TileTank.init();

        itemTank = new ItemStack(this, 1, 0);
    }
}
