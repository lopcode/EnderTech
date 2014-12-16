package io.endertech.multiblock.block;

import cofh.api.block.IDismantleable;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.endertech.EnderTech;
import io.endertech.block.BlockET;
import io.endertech.multiblock.texture.ConnectedTextureIcon;
import io.endertech.multiblock.tile.TileTankGlass;
import io.endertech.multiblock.tile.TileTankPart;
import io.endertech.proxy.CommonProxy;
import io.endertech.util.BlockCoord;
import io.endertech.util.IOutlineDrawer;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import java.util.ArrayList;
import java.util.List;

public class BlockMultiblockGlass extends BlockET implements ITileEntityProvider, IOutlineDrawer, IDismantleable
{
    public static final int METADATA_TANK = 0;
    private static final String TEXTURE_BASE = "endertech:multiblockGlass";
    public static ItemStack itemBlockMultiblockGlass;
    private static String[] subBlocks = new String[] {"tank"};
    private ConnectedTextureIcon[] icons;

    public BlockMultiblockGlass()
    {
        super(Material.glass);

        setStepSound(soundTypeGlass);
        setHardness(10.0f);
        setResistance(20.0f);
        setBlockName("enderTankMultiblockGlass");
        this.setBlockTextureName(TEXTURE_BASE);
        setCreativeTab(EnderTech.tabET);

        icons = new ConnectedTextureIcon[6];
    }

    public void init()
    {
        TileTankGlass.init();

        itemBlockMultiblockGlass = new ItemStack(this, 1, METADATA_TANK);
    }

    @Override
    public TileEntity createTileEntity(World world, int metadata)
    {
        switch (metadata)
        {
            case METADATA_TANK:
                return new TileTankGlass();
            default:
                throw new IllegalArgumentException("Unrecognized metadata");
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister)
    {
        for (int side = 0; side < 6; side++)
        {
            this.icons[side] = new ConnectedTextureIcon(iconRegister, TEXTURE_BASE + "." + subBlocks[0]);
        }
    }

    @Override
    public IIcon getIcon(int side, int metadata)
    {
        return this.icons[side];
    }

    @Override
    public int damageDropped(int metadata)
    {
        return metadata;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item item, CreativeTabs par2CreativeTabs, List par3List)
    {
        for (int i = 0; i < subBlocks.length; i++)
        {
            par3List.add(new ItemStack(item, 1, i));
        }
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta)
    {
        return null;
    }

    // Custom connected textures rendering

    public boolean isOpaqueCube()
    {
        return false;
    }

    public boolean renderAsNormalBlock()
    {
        return false;
    }

    @Override
    public int getRenderType()
    {
        return CommonProxy.connectedTexturesRenderID;
    }

    @Override
    public boolean drawOutline(DrawBlockHighlightEvent event)
    {
        BlockCoord target = new BlockCoord(event.target.blockX, event.target.blockY, event.target.blockZ);
        World world = event.player.worldObj;

        TileEntity tile = world.getTileEntity(target.x, target.y, target.z);
        if (tile instanceof TileTankPart)
        {
            return ((TileTankPart) tile).drawOutline(event);
        }

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
        return BlockTankPart.canDismantleTankBlock(player, world, x, y, z);
    }

    public boolean canCreatureSpawn(EnumCreatureType type, IBlockAccess world, int x, int y, int z)
    {
        return false;
    }

    @Override
    public boolean canPlaceBlockAt(World world, int x, int y, int z)
    {
        return BlockTankPart.canPlaceTankPartAt(world, x, y, z);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int faceHit, float par7, float par8, float par9)
    {
        boolean overridden = super.onBlockActivated(world, x, y, z, player, faceHit, par7, par8, par9);

        if (!overridden)
            overridden = BlockTankPart.onTankBlockActivated(world, x, y, z, player, faceHit, par7, par8, par9);

        return overridden;
    }
}
