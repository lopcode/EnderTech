package io.endertech.block;

import cofh.api.block.IDismantleable;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.endertech.EnderTech;
import io.endertech.client.render.IconRegistry;
import io.endertech.tile.TilePad;
import io.endertech.util.BlockCoord;
import io.endertech.util.IOutlineDrawer;
import io.endertech.util.helper.TextureHelper;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.common.util.ForgeDirection;
import java.util.ArrayList;

public abstract class BlockPad extends BlockET implements ITileEntityProvider, IDismantleable, IOutlineDrawer
{
    public BlockPad()
    {
        super(Material.iron);
        this.setCreativeTab(EnderTech.tabET);
        setHardness(10.0f);
        setResistance(20.0f);
    }

    public IIcon getPrimaryIcon(int meta, TilePad tile)
    {
        return tile.isActive ? this.getActiveIcon(meta) : this.getInactiveIcon(meta);
    }

    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta)
    {
        ForgeDirection orientation = ForgeDirection.getOrientation(side);
        if (orientation == ForgeDirection.UP) return this.getTopIcon(meta);
        else if (orientation == ForgeDirection.DOWN) return this.getBottomIcon(meta);
        else if (orientation == ForgeDirection.SOUTH) return this.getInactiveIcon(meta);

        return this.getSideIcon(meta);
    }

    @SideOnly(Side.CLIENT)
    public IIcon getIcon(IBlockAccess blockAccess, int x, int y, int z, int iSide)
    {
        TileEntity tileEntity = blockAccess.getTileEntity(x, y, z);
        int meta = blockAccess.getBlockMetadata(x, y, z);
        if (!(tileEntity instanceof TilePad)) return this.getSideIcon(meta);

        TilePad tile = (TilePad) tileEntity;
        ForgeDirection out = tile.getOrientation();
        ForgeDirection side = ForgeDirection.getOrientation(iSide);

        if (out == side) return this.getPrimaryIcon(meta, tile);

        if (side == ForgeDirection.UP) return this.getTopIcon(meta);
        else if (side == ForgeDirection.DOWN) return this.getBottomIcon(meta);
        else return this.getSideIcon(meta);
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entityLiving, ItemStack itemStack)
    {
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        if (tileEntity instanceof TilePad)
        {
            int direction = -1;

            if (MathHelper.abs((float) entityLiving.posX - (float) x) < 2.0F && MathHelper.abs((float) entityLiving.posZ - (float) z) < 2.0F)
            {
                double d0 = entityLiving.posY + 1.82D - (double) entityLiving.yOffset;
                if (d0 - (double) y > 2.0D)
                {
                    direction = ForgeDirection.UP.ordinal();
                }
                if ((double) y - d0 > 0.0D)
                {
                    direction = ForgeDirection.DOWN.ordinal();
                }
            }

            TilePad tile = (TilePad) tileEntity;
            if (direction == -1) super.onBlockPlacedBy(world, x, y, z, entityLiving, itemStack);
            else tile.setOrientation(direction);

            NBTTagCompound nbtTagCompound = itemStack.stackTagCompound;
            if (nbtTagCompound == null) nbtTagCompound = new NBTTagCompound();

            tile.readStateFromNBT(nbtTagCompound);
        }
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int faceHit, float par7, float par8, float par9)
    {
        return super.onBlockActivated(world, x, y, z, player, faceHit, par7, par8, par9);
    }

    @Override
    public ArrayList<ItemStack> dismantleBlock(EntityPlayer player, World world, int x, int y, int z, boolean returnDrops)
    {
        return BlockET.dismantleBlockInWorld(player, world, x, y, z, returnDrops);
    }

    @Override
    public boolean canDismantle(EntityPlayer player, World world, int x, int y, int z)
    {
        return true;
    }

    public boolean canCreatureSpawn(EnumCreatureType type, IBlockAccess world, int x, int y, int z)
    {
        return false;
    }

    @Override
    public int damageDropped(int meta)
    {
        return meta;
    }

    @Override
    public boolean drawOutline(DrawBlockHighlightEvent event)
    {
        BlockCoord target = new BlockCoord(event.target.blockX, event.target.blockY, event.target.blockZ);
        World world = event.player.worldObj;

        TileEntity tile = world.getTileEntity(target.x, target.y, target.z);
        if (tile instanceof TilePad)
        {
            return ((TilePad) tile).drawOutline(event);
        }

        return false;
    }

    @Override
    public boolean rotateBlock(World worldObj, int x, int y, int z, ForgeDirection axis)
    {
        TileEntity tile = worldObj.getTileEntity(x, y, z);
        if (!(tile instanceof TilePad)) return false;

        TilePad TilePad = (TilePad) tile;
        return TilePad.setFacing(axis.ordinal());
    }

    @Override
    public ForgeDirection[] getValidRotations(World worldObj, int x, int y, int z)
    {
        return ForgeDirection.VALID_DIRECTIONS;
    }

    public abstract IIcon getActiveIcon(int meta);

    public abstract IIcon getInactiveIcon(int meta);

    public IIcon getTopIcon(int meta)
    {
        return IconRegistry.getIcon("Machine_" + TextureHelper.metaToType(meta) + "_Top");
    }

    public IIcon getBottomIcon(int meta)
    {
        return IconRegistry.getIcon("Machine_" + TextureHelper.metaToType(meta) + "_Bottom");
    }

    public IIcon getSideIcon(int meta)
    {
        return IconRegistry.getIcon("Machine_" + TextureHelper.metaToType(meta) + "_Side");
    }

}
