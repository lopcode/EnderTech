package io.endertech.util.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import java.util.ArrayList;

public class InventoryHelper
{
    public static int getExtractableQuantity(InventoryAbstracted inventory, ItemStack stack)
    {
        int quantity = 0;
        for (int slot : inventory.slots)
        {
            ItemStack is = inventory.getStackInSlot(slot);
            if (is != null && is.isItemEqual(stack))
            {
                quantity += is.stackSize;
            }
        }

        return quantity;
    }

    public static int getExtractableQuantity(IInventory inv, ItemStack stack)
    {
        return getExtractableQuantity(new InventoryAbstracted(inv), stack);
    }

    public static int findFirstItemStack(InventoryAbstracted inventory, ItemStack stack)
    {
        for (int slot : inventory.slots)
        {
            ItemStack is = inventory.getStackInSlot(slot);
            if (is != null && is.isItemEqual(stack) && is.stackSize > 0)
            {
                return slot;
            }
        }

        return -1;
    }

    public static int findFirstItemStack(IInventory inv, ItemStack stack)
    {
        return findFirstItemStack(new InventoryAbstracted(inv), stack);
    }

    public static ItemStack insertItem(InventoryAbstracted inventory, ItemStack tStack, boolean simulate)
    {
        if (tStack == null)
        {
            return null;
        }

        if (inventory.slots == null)
        {
            return tStack;
        }

        ItemStack stack = tStack.copy();

        // Loop through the inventory:
        //  Check each slot in the abstracted inventory
        //   If the items can stack and there's space, add to it
        //  If we have items remaining, loop through again
        //   If the slot is empty, put as much as possible in it
        // Return whatever's left

        for (int pass = 1; pass <= 2; pass++)
        {
            for (int slot : inventory.slots)
            {
                ItemStack slotStack = inventory.getStackInSlot(slot);
                if ((slotStack == null && pass == 1) || !inventory.canInsertItem(slot, stack))
                {
                    continue;
                }

                boolean canStack = (slotStack == null) || (slotStack.getItem().equals(stack.getItem()) && ItemStack.areItemStackTagsEqual(slotStack, stack) && slotStack.isStackable() && stack.isStackable() && (!slotStack.getHasSubtypes() || (slotStack.getItemDamage() == stack.getItemDamage())));
                if (!canStack)
                {
                    continue;
                }

                int fittable = 0;
                if (slotStack == null)
                {
                    fittable = stack.getMaxStackSize();
                } else
                {
                    fittable = slotStack.getMaxStackSize() - slotStack.stackSize;
                }

                //LogHelper.info("Fittable is " + fittable);
                if (fittable <= 0)
                {
                    continue;
                }

                int fit = Math.min(fittable, stack.stackSize);
                stack.stackSize -= fit;

                if (slotStack == null)
                {
                    slotStack = stack.copy();
                    slotStack.stackSize = 0;
                }

                if (!simulate)
                {
                    slotStack.stackSize += fit;

                    //LogHelper.info("Setting inventory contents " + slotStack.stackSize);
                    inventory.inventory.setInventorySlotContents(slot, slotStack);
                }

                if (stack.stackSize <= 0)
                {
                    return null;
                }
            }
        }

        return stack;
    }

    public static void consumeItem(IInventory inventory, int slot)
    {
        inventory.decrStackSize(slot, 1);
    }

    public static ItemStack insertItem(IInventory inventory, ItemStack stack, boolean simulate)
    {
        return insertItem(new InventoryAbstracted(inventory), stack, simulate);
    }

    public static boolean checkAndPutItemStacksInToInventory(IInventory inventory, ArrayList<ItemStack> itemStacks)
    {
        return checkAndPutItemStacksInToInventory(new InventoryAbstracted(inventory), itemStacks);
    }

    public static boolean checkAndPutItemStacksInToInventory(InventoryAbstracted inventory, ArrayList<ItemStack> itemStacks)
    {
        for (ItemStack droppedItem : itemStacks)
        {
            if (InventoryHelper.insertItem(inventory, droppedItem, true) != null) return false;
        }

        for (ItemStack droppedItem : itemStacks)
        {
            InventoryHelper.insertItem(inventory, droppedItem, false);
        }

        return true;
    }
}
