package io.endertech.tile;

import cofh.api.energy.IEnergyStorage;
import net.minecraft.inventory.IInventory;

public interface IChargeableFromSlot extends IEnergyStorage
{
    public int getChargeSlot();
    public boolean hasChargeSlot();
    public void chargeFromGUISlot();
}
