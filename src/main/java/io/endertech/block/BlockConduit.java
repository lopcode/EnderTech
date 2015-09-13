package io.endertech.block;

import cofh.api.block.IDismantleable;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.endertech.EnderTech;
import io.endertech.client.render.IconRegistry;
import io.endertech.reference.Strings;
import io.endertech.reference.Textures;
import io.endertech.tile.TileConduit;
import io.endertech.util.BlockCoord;
import io.endertech.util.IOutlineDrawer;
import io.endertech.util.helper.TextureHelper;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
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
import java.util.List;

/**
 * Sends specific patterns of items down a line of inventories. Heavily borrows from BlockPad.
 *
 * @author Arkan <arkan@drakon.io>
 */
public class BlockConduit extends BlockET implements ITileEntityProvider, IDismantleable, IOutlineDrawer
{
    public static ItemStack itemConduitCreative;
    public static ItemStack itemConduitResonant;
    public static ItemStack itemConduitRedstone;

    public BlockConduit()
    {
        super(Material.iron);
        this.setCreativeTab(EnderTech.tabET);
        this.setHardness(10.0f);
        this.setResistance(20.0f);

        this.setBlockName(Strings.Blocks.CONDUIT_NAME);
    }

    public void init()
    {
        TileConduit.init();

        itemConduitCreative = new ItemStack(this, 1, 0);
        itemConduitResonant = new ItemStack(this, 1, 2);
        itemConduitRedstone = new ItemStack(this, 1, 1);
    }

    @Override
    public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_)
    {
        return new TileConduit();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void getSubBlocks(Item item, CreativeTabs creativeTabs, List blockList)
    {
        blockList.add(new ItemStack(this, 1, 0));
        blockList.add(new ItemStack(this, 1, 1));
        blockList.add(new ItemStack(this, 1, 2));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister)
    {
        String[] types = {"Creative", "Redstone", "Resonant"};
        for (String type : types)
        {
            IconRegistry.addAndRegisterIcon("Conduit_" + type + "_Active", Textures.CONDUIT_BASE + type + "_Active", iconRegister);
            IconRegistry.addAndRegisterIcon("Conduit_" + type + "_Inactive", Textures.CONDUIT_BASE + type + "_Inactive", iconRegister);
        }
    }

    public IIcon getActiveIcon(int meta)
    {
        return IconRegistry.getIcon("Conduit_" + TextureHelper.metaToType(meta) + "_Active");
    }

    public IIcon getInactiveIcon(int meta)
    {
        return IconRegistry.getIcon("Conduit_" + TextureHelper.metaToType(meta) + "_Inactive");
    }

    @Override
    public ArrayList<ItemStack> dismantleBlock(EntityPlayer player, World world, int x, int y, int z, boolean returnDrops)
    {
        // TODO
        return null;
    }

    @Override
    public boolean canDismantle(EntityPlayer player, World world, int x, int y, int z)
    {
        // TODO See above
        return false;
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
        if (!(tileEntity instanceof TileConduit)) return this.getSideIcon(meta);

        TileConduit tile = (TileConduit) tileEntity;
        ForgeDirection out = tile.getOrientation();
        ForgeDirection side = ForgeDirection.getOrientation(iSide);

        if (out == side) return this.getPrimaryIcon(meta, tile);

        if (side == ForgeDirection.UP) return this.getTopIcon(meta);
        else if (side == ForgeDirection.DOWN) return this.getBottomIcon(meta);
        else return this.getSideIcon(meta);
    }

    public IIcon getPrimaryIcon(int meta, TileConduit tile)
    {
        return tile.isActive ? this.getActiveIcon(meta) : this.getInactiveIcon(meta);
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entityLiving, ItemStack itemStack)
    {
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        if (tileEntity instanceof TileConduit)
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

            TileConduit tile = (TileConduit) tileEntity;
            if (direction == -1) super.onBlockPlacedBy(world, x, y, z, entityLiving, itemStack);
            else tile.setOrientation(direction);

            NBTTagCompound nbtTagCompound = itemStack.stackTagCompound;
            if (nbtTagCompound == null) nbtTagCompound = new NBTTagCompound();

            tile.readStateFromNBT(nbtTagCompound);
        }
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
        if (tile instanceof TileConduit)
        {
            return ((TileConduit) tile).drawOutline(event);
        }

        return false;
    }

    @Override
    public boolean rotateBlock(World worldObj, int x, int y, int z, ForgeDirection axis)
    {
        TileEntity tile = worldObj.getTileEntity(x, y, z);
        if (!(tile instanceof TileConduit)) return false;

        TileConduit tileConduit = (TileConduit) tile;
        return tileConduit.setFacing(axis.ordinal());
    }

    @Override
    public ForgeDirection[] getValidRotations(World worldObj, int x, int y, int z)
    {
        return ForgeDirection.VALID_DIRECTIONS;
    }

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

    public static enum Types
    {
        CREATIVE, REDSTONE, RESONANT;
    }
}
