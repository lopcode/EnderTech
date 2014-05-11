package io.endertech.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.endertech.lib.Strings;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class CreativeTabItems extends CreativeTabs
{
    public CreativeTabItems()
    {
        super(Strings.CREATIVE_TAB_ITEMS);
    }

    @SideOnly(Side.CLIENT)
    public int getTabIconItemIndex()
    {
        return Item.enderPearl.itemID;
    }
}
