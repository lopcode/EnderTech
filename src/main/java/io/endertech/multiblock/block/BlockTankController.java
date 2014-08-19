package io.endertech.multiblock.block;

import cofh.api.block.IDismantleable;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.endertech.EnderTech;
import io.endertech.block.BlockET;
import io.endertech.multiblock.tile.TileTankController;
import io.endertech.multiblock.tile.TileTankPart;
import io.endertech.reference.Strings;
import io.endertech.reference.Textures;
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

public class BlockTankController extends BlockET implements ITileEntityProvider, IOutlineDrawer, IDismantleable
{
    public static final int CONTROLLER_METADATA_BASE = 0; // Disabled, Idle, Active
    public static final int CONTROLLER_IDLE = 1;
    public static final int CONTROLLER_ACTIVE = 2;

    public static ItemStack itemBlockTankController;

    public static final String TEXTURE_BASE = "endertech:enderTankController";

    private static String[] _subBlocks = new String[] {"controllerBase", "controllerIdle", "controllerActive"};

    private IIcon[] _icons = new IIcon[_subBlocks.length];
    public IIcon sideIcon;
    public IIcon topIcon;
    public IIcon bottomIcon;

    public static boolean isController(int metadata) { return metadata >= CONTROLLER_METADATA_BASE && metadata <= CONTROLLER_ACTIVE; }

    public BlockTankController()
    {
        super(Material.iron);
        setHardness(10.0f);
        setResistance(20.0f);
        this.setCreativeTab(EnderTech.tabET);
        this.setBlockName(Strings.Blocks.TANK_CONTROLLER_NAME);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata)
    {
        if (metadata >= CONTROLLER_METADATA_BASE && metadata <= CONTROLLER_ACTIVE) return new TileTankController();

        throw new IllegalArgumentException("Unrecognized metadata");
    }

    public void init()
    {
        TileTankController.init();

        itemBlockTankController = new ItemStack(this, 1, CONTROLLER_METADATA_BASE);
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

    public ItemStack getTankControllerItemStack()
    {
        return new ItemStack(this, 1, CONTROLLER_METADATA_BASE);
    }

    @Override
    public void getSubBlocks(Item item, CreativeTabs par2CreativeTabs, List par3List)
    {
        par3List.add(getTankControllerItemStack());
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

        if (tile instanceof TileTankPart)
        {
            return ((TileTankPart) tile).drawOutline(event);
        }

        return false;
    }

    @Override
    public int damageDropped(int meta)
    {
        return CONTROLLER_METADATA_BASE;
    }

    @Override
    public IIcon getIcon(int side, int meta)
    {
        return _icons[meta];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister)
    {
        for (int i = 0; i < _subBlocks.length; ++i)
        {
            _icons[i] = iconRegister.registerIcon(TEXTURE_BASE + "." + _subBlocks[i]);
        }

        this.sideIcon = iconRegister.registerIcon(Textures.ENDER_TEXTURE_BASE + "Side");
        this.topIcon = iconRegister.registerIcon(Textures.ENDER_TEXTURE_BASE + "Top");
        this.bottomIcon = iconRegister.registerIcon(Textures.ENDER_TEXTURE_BASE + "Bottom");
    }

    @Override
    public ArrayList<ItemStack> dismantleBlock(EntityPlayer player, World world, int x, int y, int z, boolean returnDrops)
    {
        return dismantleBlockInWorld(player, world, x, y, z, returnDrops);
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
}
