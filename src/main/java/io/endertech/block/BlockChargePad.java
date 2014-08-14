package io.endertech.block;

import cofh.api.block.IDismantleable;
import cofh.lib.util.helpers.ServerHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.endertech.EnderTech;
import io.endertech.multiblock.block.BlockTankController;
import io.endertech.reference.Strings;
import io.endertech.tile.TileChargePad;
import io.endertech.tile.TileET;
import io.endertech.util.BlockCoord;
import io.endertech.util.IOutlineDrawer;
import io.endertech.util.helper.LogHelper;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.common.util.ForgeDirection;
import java.util.ArrayList;
import java.util.List;

public class BlockChargePad extends BlockET implements ITileEntityProvider, IDismantleable, IOutlineDrawer
{
    public static ItemStack itemChargePadCreative;
    public static ItemStack itemChargePadResonant;
    public static ItemStack itemChargePadRedstone;

    public IIcon sideIcon;
    public IIcon topIcon;
    public IIcon bottomIcon;
    public static final String TEXTURE_BASE = "endertech:chargePad/";
    public IIcon[] activeIcons;
    public IIcon[] inactiveIcons;

    public BlockChargePad()
    {
        super(Material.iron);
        this.setCreativeTab(EnderTech.tabET);
        this.setBlockName(Strings.Blocks.CHARGE_PAD);
    }

    public void init()
    {
        TileChargePad.init();

        itemChargePadCreative = new ItemStack(this, 1, 0);
        itemChargePadResonant = new ItemStack(this, 1, 2);
        itemChargePadRedstone = new ItemStack(this, 1, 1);
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entityLiving, ItemStack itemStack)
    {
        if (world.getTileEntity(x, y, z) instanceof TileChargePad)
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

    public Types metadataToType(int meta)
    {
        if (meta < 0 || meta > arrayTypes.length - 1) meta = arrayTypes.length - 1;

        return arrayTypes[meta];
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata)
    {
        return new TileChargePad();
    }

    public IIcon getPrimaryIcon(int meta, TileChargePad tile)
    {
        if (tile.isActive)
        {
            return this.activeIcons[meta];
        } else
        {
            return this.inactiveIcons[meta];
        }
    }

    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta)
    {
        ForgeDirection orientation = ForgeDirection.getOrientation(side);
        if (orientation == ForgeDirection.UP) return this.topIcon;
        else if (orientation == ForgeDirection.DOWN) return this.bottomIcon;
        else if (orientation == ForgeDirection.SOUTH) return this.inactiveIcons[meta];

        return this.sideIcon;
    }

    @SideOnly(Side.CLIENT)
    public IIcon getIcon(IBlockAccess blockAccess, int x, int y, int z, int iSide)
    {
        TileEntity tileEntity = blockAccess.getTileEntity(x, y, z);
        if (tileEntity == null || !(tileEntity instanceof TileChargePad)) return this.sideIcon;

        int meta = blockAccess.getBlockMetadata(x, y, z);

        TileChargePad tile = (TileChargePad) tileEntity;
        ForgeDirection out = tile.getOrientation();
        ForgeDirection side = ForgeDirection.getOrientation(iSide);

        if (out == side) return this.getPrimaryIcon(meta, tile);

        if (side == ForgeDirection.UP) return this.topIcon;
        else if (side == ForgeDirection.DOWN) return this.bottomIcon;
        else return this.sideIcon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister)
    {
        this.activeIcons = new IIcon[arrayTypes.length];
        this.inactiveIcons = new IIcon[arrayTypes.length];

        for (int i = 0; i < stringTypes.length; i++)
        {
            this.activeIcons[i] = iconRegister.registerIcon(TEXTURE_BASE + this.getUnlocalizedName().replace("tile.", "") + "." + stringTypes[i] + ".active");
            this.inactiveIcons[i] = iconRegister.registerIcon(TEXTURE_BASE + this.getUnlocalizedName().replace("tile.", "") + "." + stringTypes[i] + ".inactive");
        }

        this.sideIcon = iconRegister.registerIcon(BlockTankController.TEXTURE_BASE + ".controllerSide");
        this.topIcon = iconRegister.registerIcon(BlockTankController.TEXTURE_BASE + ".controllerTop");
        this.bottomIcon = iconRegister.registerIcon(BlockTankController.TEXTURE_BASE + ".controllerBottom");
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int faceHit, float par7, float par8, float par9)
    {
        if (!ServerHelper.isServerWorld(world)) return false;

        TileEntity tileEntity = world.getTileEntity(x, y, z);
        if (tileEntity == null || !(tileEntity instanceof TileChargePad))
            LogHelper.info("I don't have a tile entity :(");

        TileChargePad tile = (TileChargePad) tileEntity;
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

    @Override
    public void getSubBlocks(Item item, CreativeTabs par2CreativeTabs, List par3List)
    {
        for (int i = 0; i < arrayTypes.length; i++)
            par3List.add(new ItemStack(this, 1, i));
    }

    @Override
    public int damageDropped(int meta)
    {
        return meta;
    }

    public static int getMaxEnergyStored(int meta)
    {
        return CAPACITY[meta];
    }

    public static int getMaxReceiveRate(int meta)
    {
        return RECEIVE[meta];
    }

    public static int getMaxSendRate(int meta)
    {
        return SEND[meta];
    }

    public static boolean isCreative(int meta)
    {
        return meta == 0;
    }

    @Override
    public boolean drawOutline(DrawBlockHighlightEvent event)
    {
        BlockCoord target = new BlockCoord(event.target.blockX, event.target.blockY, event.target.blockZ);
        World world = event.player.worldObj;

        TileEntity tile = world.getTileEntity(target.x, target.y, target.z);
        if (tile == null)
        {
            return false;
        }

        if (tile instanceof TileChargePad)
        {
            return ((TileChargePad) tile).drawOutline(event);
        }

        return false;
    }

    public static enum Types
    {
        CREATIVE, REDSTONE, RESONANT;
    }

    public static Types[] arrayTypes;
    public static String[] stringTypes;

    static
    {
        arrayTypes = Types.values();
        stringTypes = new String[arrayTypes.length];
        for (int i = 0; i < arrayTypes.length; i++)
        {
            stringTypes[i] = arrayTypes[i].name().toLowerCase();
        }
    }

    public static final int[] RECEIVE = {0, 1 * 2000, 10 * 2000};
    public static final int[] SEND = {10 * 1000000, 1 * 2000, 10 * 2000};
    public static final int[] CAPACITY = {Integer.MAX_VALUE, 1 * 2000000, 10 * 1000000};
}
