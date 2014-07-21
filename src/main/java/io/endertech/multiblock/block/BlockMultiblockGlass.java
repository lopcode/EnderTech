package io.endertech.multiblock.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.endertech.EnderTech;
import io.endertech.multiblock.tile.TileTankGlass;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import java.util.List;

public class BlockMultiblockGlass extends BlockContainer
{
    public static final int METADATA_TANK = 0;
    private static String[] subBlocks = new String[] {"tank"};
    private IIcon[][] icons = new IIcon[subBlocks.length][16];
    private IIcon transparentIcon;
    public static ItemStack itemBlockMultiblockGlass;

    public BlockMultiblockGlass()
    {
        super(Material.glass);

        setStepSound(soundTypeGlass);
        setHardness(2.0f);
        setBlockName("multiblockGlass");
        this.setBlockTextureName("endertech:multiblockGlass");
        setCreativeTab(EnderTech.tabET);
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
    public void registerBlockIcons(IIconRegister iconRegister)
    {
        this.transparentIcon = iconRegister.registerIcon("endertech:" + getUnlocalizedName() + ".transparent");

        for (int metadata = 0; metadata < subBlocks.length; metadata++)
        {
            for (int i = 0; i < 16; ++i)
            {
                icons[metadata][i] = iconRegister.registerIcon("endertech:" + getUnlocalizedName() + "." + subBlocks[metadata] + "." + Integer.toString(i));
            }
        }
    }

    public static final ForgeDirection neighborsBySide[][] = new ForgeDirection[][] {{ForgeDirection.NORTH, ForgeDirection.SOUTH, ForgeDirection.WEST, ForgeDirection.EAST}, {ForgeDirection.NORTH, ForgeDirection.SOUTH, ForgeDirection.WEST, ForgeDirection.EAST}, {ForgeDirection.UP, ForgeDirection.DOWN, ForgeDirection.EAST, ForgeDirection.WEST}, {ForgeDirection.UP, ForgeDirection.DOWN, ForgeDirection.WEST, ForgeDirection.EAST}, {ForgeDirection.UP, ForgeDirection.DOWN, ForgeDirection.NORTH, ForgeDirection.SOUTH}, {ForgeDirection.UP, ForgeDirection.DOWN, ForgeDirection.SOUTH, ForgeDirection.NORTH}};

    @Override
    public IIcon getIcon(IBlockAccess blockAccess, int x, int y, int z, int side)
    {
        ForgeDirection[] dirsToCheck = neighborsBySide[side];
        ForgeDirection dir;
        Block myBlock = blockAccess.getBlock(x, y, z);
        int myBlockMetadata = blockAccess.getBlockMetadata(x, y, z);

        // First check if we have a block in front of us of the same type - if so, just be completely transparent on this side
        ForgeDirection out = ForgeDirection.getOrientation(side);
        if (blockAccess.getBlock(x + out.offsetX, y + out.offsetY, z + out.offsetZ) == myBlock && blockAccess.getBlockMetadata(x + out.offsetX, y + out.offsetY, z + out.offsetZ) == myBlockMetadata)
        {
            return transparentIcon;
        }

        // Calculate icon index based on whether the blocks around this block match it
        // Icons use a naming pattern so that the bits correspond to:
        // 1 = Connected on top, 2 = connected on bottom, 4 = connected on left, 8 = connected on right
        int iconIdx = 0;
        for (int i = 0; i < dirsToCheck.length; i++)
        {
            dir = dirsToCheck[i];
            // Same blockID and metadata on this side?
            if (blockAccess.getBlock(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ) == myBlock && blockAccess.getBlockMetadata(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ) == myBlockMetadata)
            {
                // Connected!
                iconIdx |= 1 << i;
            }
        }

        return icons[myBlockMetadata][iconIdx];
    }

    @Override
    public IIcon getIcon(int side, int metadata)
    {
        return icons[metadata][0];
    }

    @Override
    public int damageDropped(int metadata)
    {
        return metadata;
    }

    @Override
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

    // Same rendering as glass
    @SideOnly(Side.CLIENT)
    public int getRenderBlockPass()
    {
        return 0;
    }

    public boolean isOpaqueCube()
    {
        return false;
    }

    public boolean renderAsNormalBlock()
    {
        return false;
    }
}
