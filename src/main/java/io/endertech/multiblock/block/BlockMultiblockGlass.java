package io.endertech.multiblock.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.endertech.EnderTech;
import io.endertech.multiblock.texture.ConnectedTextureManager;
import io.endertech.multiblock.tile.TileTankGlass;
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
import java.util.List;

public class BlockMultiblockGlass extends BlockContainer
{
    public static final int METADATA_TANK = 0;
    private static String[] subBlocks = new String[] {"tank"};
    public static ItemStack itemBlockMultiblockGlass;
    private ConnectedTextureManager[] textureManagers;
    private static final String TEXTURE_BASE = "endertech:multiblockGlass";

    public BlockMultiblockGlass()
    {
        super(Material.glass);

        setStepSound(soundTypeGlass);
        setHardness(2.0f);
        setBlockName("multiblockGlass");
        this.setBlockTextureName(TEXTURE_BASE);
        setCreativeTab(EnderTech.tabET);

        this.initialiseTextureManagers();
    }

    public void initialiseTextureManagers()
    {
        textureManagers = new ConnectedTextureManager[subBlocks.length];
        textureManagers[0] = new ConnectedTextureManager(TEXTURE_BASE + "." + subBlocks[0], null);
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
        for (ConnectedTextureManager textureManager : textureManagers)
            textureManager.registerBlockIcons(iconRegister);
    }

    @Override
    public IIcon getIcon(IBlockAccess blockAccess, int x, int y, int z, int side)
    {
        int myBlockMetadata = blockAccess.getBlockMetadata(x, y, z);
        return textureManagers[myBlockMetadata].getIcon(blockAccess, x, y, z, side);
    }

    @Override
    public IIcon getIcon(int side, int metadata)
    {
        return textureManagers[metadata].getUnconnectedTexture();
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
