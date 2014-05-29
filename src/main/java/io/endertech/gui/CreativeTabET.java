package io.endertech.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.endertech.lib.Strings;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class CreativeTabET extends CreativeTabs
{
    public CreativeTabET()
    {
        super(Strings.CREATIVE_TAB_ET);
    }

    @SideOnly(Side.CLIENT)
    public int getTabIconItemIndex()
    {
        return Item.enderPearl.itemID;
    }
}
