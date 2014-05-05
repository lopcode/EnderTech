package io.endertech.helper;

import codechicken.lib.inventory.InventoryRange;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class InventoryHelper
{
    public static int getExtractableQuantity(InventoryRange inv, ItemStack stack)
    {
        int quantity = 0;
        for (int slot : inv.slots)
        {
            ItemStack is = inv.inv.getStackInSlot(slot);
            if (is != null && is.isItemEqual(stack))
            {
                quantity += is.stackSize;
            }
        }

        return quantity;
    }

    public static int getExtractableQuantity(IInventory inv, ItemStack stack)
    {
        return getExtractableQuantity(new InventoryRange(inv), stack);
    }

    public static int findFirstItemStack(InventoryRange inv, ItemStack stack)
    {
        for (int slot : inv.slots)
        {
            ItemStack is = inv.inv.getStackInSlot(slot);
            if (is != null && is.isItemEqual(stack) && is.stackSize > 0) return slot;
        }

        return -1;
    }

    public static int findFirstItemStack(IInventory inv, ItemStack stack)
    {
        return findFirstItemStack(new InventoryRange(inv), stack);
    }
}
