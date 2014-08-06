package io.endertech.block;

import io.endertech.EnderTech;
import io.endertech.tile.TileET;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockET extends Block
{
    public BlockET()
    {
        this(Material.iron);
    }

    public BlockET(Material material)
    {
        super(material);
        this.setCreativeTab(EnderTech.tabET);
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entityLiving, ItemStack itemStack)
    {
        if (world.getTileEntity(x, y, z) instanceof TileET)
        {
            int direction = 0;
            int facing = MathHelper.floor_double(entityLiving.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;

            if (facing == 0)
            {
                direction = ForgeDirection.NORTH.ordinal();
            } else if (facing == 1)
            {
                direction = ForgeDirection.EAST.ordinal();
            } else if (facing == 2)
            {
                direction = ForgeDirection.SOUTH.ordinal();
            } else if (facing == 3)
            {
                direction = ForgeDirection.WEST.ordinal();
            }

            ((TileET) world.getTileEntity(x, y, z)).setOrientation(direction);
        }
    }
}
