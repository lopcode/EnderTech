package io.endertech.multiblock.block;

import io.endertech.EnderTech;
import io.endertech.multiblock.texture.ConnectedTextureIcon;
import io.endertech.multiblock.tile.TileTankGlass;
import io.endertech.proxy.ClientProxy;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import java.util.List;

public class BlockMultiblockGlass extends BlockContainer
{
    public static final int METADATA_TANK = 0;
    private static String[] subBlocks = new String[] {"tank"};
    public static ItemStack itemBlockMultiblockGlass;
    private static final String TEXTURE_BASE = "endertech:multiblockGlass";
    private ConnectedTextureIcon[] icons;

    public BlockMultiblockGlass()
    {
        super(Material.glass);

        setStepSound(soundTypeGlass);
        setHardness(2.0f);
        setBlockName("multiblockGlass");
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
        return ClientProxy.connectedTexturesRenderID;
    }
}
