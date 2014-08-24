package io.endertech.tile;

import cofh.api.energy.IEnergyStorage;

public interface IChargeableFromSlot extends IEnergyStorage
{
    public int getChargeSlot();

    public boolean hasChargeSlot();

    public void chargeFromGUISlot();
}
