package io.endertech.block;

import cofh.lib.util.helpers.ServerHelper;
import io.endertech.EnderTech;
import io.endertech.tile.TileET;
import io.endertech.tile.TileInventory;
import io.endertech.util.helper.WorldHelper;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import java.util.ArrayList;

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

    public static ArrayList<ItemStack> dismantleBlockInWorld(EntityPlayer player, World world, int x, int y, int z, boolean returnDrops)
    {
        Block block = world.getBlock(x, y, z);
        int meta = world.getBlockMetadata(x, y, z);

        ItemStack drop = new ItemStack(block, 1, block.damageDropped(meta));
        ArrayList<ItemStack> drops = new ArrayList<ItemStack>();
        drops.add(drop);

        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile != null && tile instanceof TileInventory)
        {
            TileInventory tileInventory = (TileInventory)tile;

            for (ItemStack itemStack : tileInventory.inventory)
            {
                if (itemStack != null)
                    drops.add(itemStack);
            }
        }

        world.setBlockToAir(x, y, z);

        if (!returnDrops)
        {
            for (ItemStack itemStack : drops)
            {
                WorldHelper.spawnItemInWorldWithRandomness(itemStack, world, 0.3F, x, y, z, 2);
            }
        }

        return drops;
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

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float fx, float fy, float fz)
    {
        TileET tileET = (TileET) world.getTileEntity(x, y, z);
        if (tileET == null) return false;

        if (tileET.hasGui())
        {
            if (ServerHelper.isServerWorld(world))
            {
                tileET.openGui(player);
            }

            return true;
        }

        return false;
    }
}
