package io.endertech.util.helper;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class NBTHelper
{
    public static ItemStack[] readInventoryFromNBT(NBTTagCompound nbtTagCompound, int numberOfItems)
    {
        NBTTagList nbtTagList = nbtTagCompound.getTagList("Inventory", 10);
        ItemStack[] inventory = new ItemStack[numberOfItems];
        for (int i = 0; i < nbtTagList.tagCount(); i++)
        {
            NBTTagCompound nbtTagCompoundSlot = nbtTagList.getCompoundTagAt(i);
            int j = nbtTagCompoundSlot.getInteger("Slot");

            if ((j >= 0) && (j < numberOfItems)) inventory[j] = ItemStack.loadItemStackFromNBT(nbtTagCompoundSlot);
        }
        return inventory;
    }

    public static void writeInventoryToNBT(NBTTagCompound nbtTagCompound, ItemStack[] inventory)
    {
        NBTTagList nbtTagList = new NBTTagList();
        for (int i = 0; i < inventory.length; i++)
        {
            if (inventory[i] != null)
            {
                NBTTagCompound nbtTagCompoundSlot = new NBTTagCompound();
                nbtTagCompoundSlot.setInteger("Slot", i);
                inventory[i].writeToNBT(nbtTagCompoundSlot);
                nbtTagList.appendTag(nbtTagCompoundSlot);
            }
        }
        nbtTagCompound.setTag("Inventory", nbtTagList);
    }

}
