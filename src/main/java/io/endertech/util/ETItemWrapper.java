package io.endertech.util;

import cofh.lib.util.helpers.ItemHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

// Derived from CoFHLib's ItemWrapper

public final class ETItemWrapper
{

    public Item item;
    public int metadata;

    public static ETItemWrapper fromItemStack(ItemStack stack)
    {

        return new ETItemWrapper(stack);
    }

    public ETItemWrapper(Item item, int metadata)
    {

        this.item = item;
        this.metadata = metadata;
    }

    public ETItemWrapper(ItemStack stack)
    {

        this.item = stack.getItem();
        this.metadata = ItemHelper.getItemDamage(stack);
    }

    public ETItemWrapper set(ItemStack stack)
    {

        if (stack != null)
        {
            this.item = stack.getItem();
            this.metadata = ItemHelper.getItemDamage(stack);
        } else
        {
            this.item = null;
            this.metadata = 0;
        }
        return this;
    }

    public boolean isEqual(ETItemWrapper other)
    {

        return other != null && item == other.item && metadata == other.metadata;
    }

    @Override
    public boolean equals(Object o)
    {

        if (!(o instanceof ETItemWrapper))
        {
            return false;
        }
        return isEqual((ETItemWrapper) o);
    }

    @Override
    public int hashCode()
    {
        return metadata | item.getUnlocalizedName().hashCode() << 16;
    }

}