package io.endertech.util.helper

import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList

object NBTHelper {
    fun readInventoryFromNBT(nbtTagCompound: NBTTagCompound, numberOfItems: Int): Array<ItemStack?> {
        val nbtTagList = nbtTagCompound.getTagList("Inventory", 10)
        val inventory = arrayOfNulls<ItemStack>(numberOfItems)
        for (i in 0..nbtTagList.tagCount() - 1) {
            val nbtTagCompoundSlot = nbtTagList.getCompoundTagAt(i)
            val j = nbtTagCompoundSlot.getInteger("Slot")

            if (j >= 0 && j < numberOfItems) inventory[j] = ItemStack.loadItemStackFromNBT(nbtTagCompoundSlot)
        }
        return inventory
    }

    fun writeInventoryToNBT(nbtTagCompound: NBTTagCompound, inventory: Array<ItemStack?>) {
        val nbtTagList = NBTTagList()
        for (i in inventory.indices) {
            var currItem = inventory[i]
            if (currItem != null) {
                val nbtTagCompoundSlot = NBTTagCompound()
                nbtTagCompoundSlot.setInteger("Slot", i)
                currItem.writeToNBT(nbtTagCompoundSlot)
                nbtTagList.appendTag(nbtTagCompoundSlot)
            }
        }
        nbtTagCompound.setTag("Inventory", nbtTagList)
    }

}
