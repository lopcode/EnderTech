package io.endertech.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class TileInventory extends TileET implements IInventory
{
    public ItemStack[] inventory;

    @Override
    public int getSizeInventory()
    {
        return this.inventory.length;
    }

    @Override
    public ItemStack getStackInSlot(int slot)
    {
        return this.inventory[slot];
    }

    @Override
    public ItemStack decrStackSize(int slot, int amount)
    {
        if (this.inventory[slot] == null) return null;

        if (this.inventory[slot].stackSize <= amount) amount = this.inventory[slot].stackSize;

        ItemStack itemStack = this.inventory[slot].splitStack(amount);

        if (this.inventory[slot].stackSize <= 0) this.inventory[slot] = null;

        return itemStack;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot)
    {
        if (this.inventory[slot] == null) return null;

        ItemStack itemStack = this.inventory[slot];
        this.inventory[slot] = null;

        return itemStack;
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack itemStack)
    {
        this.inventory[slot] = itemStack;

        int inventoryStackLimit = this.getInventoryStackLimit();
        if (itemStack != null && itemStack.stackSize > inventoryStackLimit) itemStack.stackSize = inventoryStackLimit;

        this.worldObj.markTileEntityChunkModified(this.xCoord, this.yCoord, this.zCoord, this);
    }

    @Override
    public String getInventoryName()
    {
        return this.tileName;
    }

    @Override
    public boolean hasCustomInventoryName()
    {
        return !this.tileName.isEmpty();
    }

    @Override
    public int getInventoryStackLimit()
    {
        return 64;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player)
    {
        return true;
    }

    @Override
    public void openInventory()
    {

    }

    @Override
    public void closeInventory()
    {

    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack itemStack)
    {
        return true;
    }
}
