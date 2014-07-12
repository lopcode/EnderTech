package io.endertech.creativetab;

import io.endertech.reference.Strings;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;

public class CreativeTabET extends CreativeTabs
{
    public CreativeTabET()
    {
        super(Strings.CREATIVE_TAB_ET);
    }

    @Override
    public Item getTabIconItem()
    {
        return Items.ender_pearl;
    }
}
