package io.endertech.modules.dev.block;

import io.endertech.EnderTech;
import io.endertech.modules.dev.tile.TileChargedPlane;
import io.endertech.reference.Strings;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockChargedPlane extends BlockContainer
{
    public static ItemStack itemChargedPlane;

    public BlockChargedPlane()
    {
        super(Material.iron);
        this.setCreativeTab(EnderTech.tabET);
        this.setBlockName(Strings.Blocks.CHARGED_PLANE_NAME);
    }

    public void init()
    {
        TileChargedPlane.init();

        itemChargedPlane = new ItemStack(this, 1, 0);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata)
    {
        return new TileChargedPlane();
    }


}
