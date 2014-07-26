package io.endertech.multiblock.block;

import io.endertech.EnderTech;
import io.endertech.multiblock.tile.TileTankController;
import io.endertech.multiblock.tile.TileTankPartBase;
import io.endertech.reference.Strings;
import io.endertech.util.BlockCoord;
import io.endertech.util.IOutlineDrawer;
import io.endertech.util.RGBA;
import io.endertech.util.RenderHelper;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.fluids.FluidRegistry;
import java.util.List;

public class BlockTankController extends BlockContainer implements IOutlineDrawer
{
    public static final int CONTROLLER_METADATA_BASE = 0; // Disabled, Idle, Active
    public static final int CONTROLLER_IDLE = 1;
    public static final int CONTROLLER_ACTIVE = 2;

    public static ItemStack itemBlockTankController;

    public static boolean isController(int metadata) { return metadata >= CONTROLLER_METADATA_BASE && metadata <= CONTROLLER_ACTIVE; }

    public BlockTankController()
    {
        super(Material.iron);
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
            RenderHelper.renderBlockOutline(event.context, event.player, target, RGBA.Blue.setAlpha(0.6f), 2.0f, event.partialTicks);
            return true;
        }

        if (tile instanceof TileTankPartBase)
        {
            return ((TileTankPartBase) tile).drawOutline(event);
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
        return FluidRegistry.LAVA.getStillIcon();
    }
}
