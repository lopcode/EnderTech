package io.endertech.block;

import io.endertech.EnderTech;
import io.endertech.lib.Strings;
import io.endertech.tile.TileSpinningCube;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockSpinningCube extends BlockContainer
{
    public static ItemStack itemCube;

    public BlockSpinningCube()
    {
        super(Material.iron);
        this.setCreativeTab(EnderTech.tabET);
        this.setBlockName(Strings.Blocks.SPINNING_CUBE_NAME);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata)
    {
        return new TileSpinningCube();
    }

    public void init()
    {
        TileSpinningCube.init();

        itemCube = new ItemStack(this, 1, 0);
    }

    public int getRenderType()
    {
        return -1;
    }

    @Override
    public boolean isOpaqueCube()
    {
        return false;
    }
}
