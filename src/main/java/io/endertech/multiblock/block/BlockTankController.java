package io.endertech.multiblock.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.endertech.EnderTech;
import io.endertech.multiblock.tile.TileTankController;
import io.endertech.multiblock.tile.TileTankPart;
import io.endertech.reference.Strings;
import io.endertech.util.BlockCoord;
import io.endertech.util.IOutlineDrawer;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import java.util.List;

public class BlockTankController extends BlockContainer implements IOutlineDrawer
{
    public static final int CONTROLLER_METADATA_BASE = 0; // Disabled, Idle, Active
    public static final int CONTROLLER_IDLE = 1;
    public static final int CONTROLLER_ACTIVE = 2;

    public static ItemStack itemBlockTankController;

    private static final String TEXTURE_BASE = "endertech:enderTankController";

    private static String[] _subBlocks = new String[] {"controllerBase", "controllerIdle", "controllerActive"};

    private IIcon[] _icons = new IIcon[_subBlocks.length];

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
    public IIcon getIcon(int side, int metadata)
    {
        return _icons[metadata];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister)
    {
        this.blockIcon = iconRegister.registerIcon(TEXTURE_BASE);

        for (int i = 0; i < _subBlocks.length; ++i)
        {
            _icons[i] = iconRegister.registerIcon(TEXTURE_BASE + "." + _subBlocks[i]);
        }
    }
}
