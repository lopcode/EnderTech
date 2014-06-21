package io.endertech.helper.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;

public class InventoryAbstracted
{
    public int[] slots;
    public IInventory inventory;
    public int side;

    public InventoryAbstracted(IInventory inventory)
    {
        this(inventory, 0);
    }

    public InventoryAbstracted(IInventory inventory, int side)
    {
        this.inventory = inventory;
        this.side = side;

        if (inventory instanceof ISidedInventory)
        {
            this.slots = ((ISidedInventory) this.inventory).getAccessibleSlotsFromSide(this.side);
        }
        else
        {
            this.slots = new int[inventory.getSizeInventory()];

            for (int i = 0; i < slots.length; i++)
            {
                this.slots[i] = i;
            }
        }
    }

    public ItemStack getStackInSlot(int slot)
    {
        return inventory.getStackInSlot(slot);
    }

    public boolean canInsertItem(int slot, ItemStack item)
    {
        if (inventory instanceof ISidedInventory)
        {
            return ((ISidedInventory) inventory).canInsertItem(slot, item, this.side);
        }
        else
        {
            return inventory.isItemValidForSlot(slot, item);
        }
    }

    public boolean canExtractItem(int slot, ItemStack item)
    {
        if (inventory instanceof ISidedInventory)
        {
            return ((ISidedInventory) inventory).canExtractItem(slot, item, this.side);
        }
        else
        {
            return inventory.isItemValidForSlot(slot, item);
        }
    }
}
