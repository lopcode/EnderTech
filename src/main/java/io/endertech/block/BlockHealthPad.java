package io.endertech.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.endertech.client.render.IconRegistry;
import io.endertech.reference.Strings;
import io.endertech.reference.Textures;
import io.endertech.tile.TileHealthPad;
import io.endertech.util.helper.TextureHelper;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import java.util.List;

public class BlockHealthPad extends BlockPad
{
    public static ItemStack itemHealthPadCreative;
    public static ItemStack itemHealthPadResonant;
    public static ItemStack itemHealthPadRedstone;

    public BlockHealthPad()
    {
        super();

        this.setBlockName(Strings.Blocks.HEALTH_PAD);
    }

    public void init()
    {
        TileHealthPad.init();

        itemHealthPadCreative = new ItemStack(this, 1, 0);
        itemHealthPadResonant = new ItemStack(this, 1, 2);
        itemHealthPadRedstone = new ItemStack(this, 1, 1);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta)
    {
        return new TileHealthPad();
    }

    @Override
    public void getSubBlocks(Item item, CreativeTabs creativeTabs, List blockList)
    {
        blockList.add(new ItemStack(this, 1, 0));
        blockList.add(new ItemStack(this, 1, 2));
        blockList.add(new ItemStack(this, 1, 1));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister)
    {
        String[] types = {"Creative", "Redstone", "Resonant"};
        for (String type : types)
        {
            IconRegistry.addAndRegisterIcon("HealthPad_" + type + "_Active", Textures.CHARGE_PAD_BASE + type + "_Active", iconRegister);
            IconRegistry.addAndRegisterIcon("HealthPad_" + type + "_Inactive", Textures.CHARGE_PAD_BASE + type + "_Inactive", iconRegister);
        }
    }

    @Override
    public IIcon getActiveIcon(int meta)
    {
        return IconRegistry.getIcon("HealthPad_" + TextureHelper.metaToType(meta) + "_Active");
    }

    @Override
    public IIcon getInactiveIcon(int meta)
    {
        return IconRegistry.getIcon("HealthPad_" + TextureHelper.metaToType(meta) + "_Inactive");
    }

    public static enum Types
    {
        CREATIVE, REDSTONE, RESONANT;
    }
}
