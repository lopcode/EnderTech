package io.endertech.modules.dev.block;

import cofh.api.block.IDismantleable;
import cofh.lib.util.helpers.ServerHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.endertech.EnderTech;
import io.endertech.block.BlockET;
import io.endertech.modules.dev.tile.TileChargedPlane;
import io.endertech.multiblock.block.BlockTankController;
import io.endertech.multiblock.block.BlockTankPart;
import io.endertech.reference.Strings;
import io.endertech.tile.TileET;
import io.endertech.util.helper.LogHelper;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import java.util.ArrayList;

public class BlockChargedPlane extends BlockET implements ITileEntityProvider, IDismantleable
{
    public static ItemStack itemChargedPlane;
    public IIcon activeIcon;
    public IIcon inactiveIcon;
    public IIcon sideIcon;
    public IIcon topIcon;
    public IIcon bottomIcon;
    public static final String TEXTURE_BASE = "endertech:chargedPlane";

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
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entityLiving, ItemStack itemStack)
    {
        if (world.getTileEntity(x, y, z) instanceof TileChargedPlane)
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

            if (direction == -1) super.onBlockPlacedBy(world, x, y, z, entityLiving, itemStack);
            else ((TileET) world.getTileEntity(x, y, z)).setOrientation(direction);
        }
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata)
    {
        return new TileChargedPlane();
    }

    public IIcon getPrimaryIcon(TileChargedPlane tile)
    {
        if (tile.isActive)
        {
            return this.activeIcon;
        } else
        {
            return this.inactiveIcon;
        }
    }

    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta)
    {
        ForgeDirection orientation = ForgeDirection.getOrientation(side);
        if (orientation == ForgeDirection.UP) return this.topIcon;
        else if (orientation == ForgeDirection.DOWN) return this.bottomIcon;
        else if (orientation == ForgeDirection.SOUTH) return this.inactiveIcon;

        return this.sideIcon;
    }

    @SideOnly(Side.CLIENT)
    public IIcon getIcon(IBlockAccess blockAccess, int x, int y, int z, int iSide)
    {
        TileEntity tileEntity = blockAccess.getTileEntity(x, y, z);
        if (tileEntity == null || !(tileEntity instanceof TileChargedPlane)) return this.sideIcon;

        TileChargedPlane tile = (TileChargedPlane) tileEntity;
        ForgeDirection out = tile.getOrientation();
        ForgeDirection side = ForgeDirection.getOrientation(iSide);

        if (out == side) return this.getPrimaryIcon(tile);

        if (side == ForgeDirection.UP) return this.topIcon;
        else if (side == ForgeDirection.DOWN) return this.bottomIcon;
        else return this.sideIcon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister)
    {
        this.inactiveIcon = iconRegister.registerIcon(BlockTankPart.TEXTURE_BASE + ".frameCorner");
        this.activeIcon = iconRegister.registerIcon(TEXTURE_BASE + ".active");
        this.sideIcon = iconRegister.registerIcon(BlockTankController.TEXTURE_BASE + ".controllerSide");
        this.topIcon = iconRegister.registerIcon(BlockTankController.TEXTURE_BASE + ".controllerTop");
        this.bottomIcon = iconRegister.registerIcon(BlockTankController.TEXTURE_BASE + ".controllerBottom");
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int faceHit, float par7, float par8, float par9)
    {
        if (!ServerHelper.isServerWorld(world)) return false;

        TileEntity tileEntity = world.getTileEntity(x, y, z);
        if (tileEntity == null || !(tileEntity instanceof TileChargedPlane))
            LogHelper.info("I don't have a tile entity :(");

        TileChargedPlane tile = (TileChargedPlane) tileEntity;
        ForgeDirection out = tile.getOrientation();

        LogHelper.info("My direction is: " + out.name());

        return false;
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
}
