package io.endertech.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.endertech.client.render.IconRegistry;
import io.endertech.reference.Strings;
import io.endertech.reference.Textures;
import io.endertech.tile.TileChargePad;
import io.endertech.util.helper.TextureHelper;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import java.util.List;

public class BlockChargePad extends BlockPad
{
    public static ItemStack itemChargePadCreative;
    public static ItemStack itemChargePadResonant;
    public static ItemStack itemChargePadRedstone;

    public BlockChargePad()
    {
        super();

        this.setBlockName(Strings.Blocks.CHARGE_PAD);
    }

    public static boolean isCreative(int meta)
    {
        return meta == 0;
    }

    public void init()
    {
        TileChargePad.init();

        itemChargePadCreative = new ItemStack(this, 1, 0);
        itemChargePadResonant = new ItemStack(this, 1, 2);
        itemChargePadRedstone = new ItemStack(this, 1, 1);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata)
    {
        return new TileChargePad();
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
            IconRegistry.addAndRegisterIcon("ChargePad_" + type + "_Active", Textures.CHARGE_PAD_BASE + type + "_Active", iconRegister);
            IconRegistry.addAndRegisterIcon("ChargePad_" + type + "_Inactive", Textures.CHARGE_PAD_BASE + type + "_Inactive", iconRegister);
        }
    }

    @Override
    public IIcon getActiveIcon(int meta)
    {
        return IconRegistry.getIcon("ChargePad_" + TextureHelper.metaToType(meta) + "_Active");
    }

    @Override
    public IIcon getInactiveIcon(int meta)
    {
        return IconRegistry.getIcon("ChargePad_" + TextureHelper.metaToType(meta) + "_Inactive");
    }

    public static enum Types
    {
        CREATIVE, REDSTONE, RESONANT;
    }
}
