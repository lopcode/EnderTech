package io.endertech.gui.element;

import cofh.api.energy.IEnergyStorage;
import cofh.lib.gui.GuiBase;
import cofh.lib.gui.element.ElementEnergyStored;
import io.endertech.util.helper.StringHelper;
import java.util.List;

public class ElementETEnergyStored extends ElementEnergyStored
{
    public ElementETEnergyStored(GuiBase gui, int posX, int posY, IEnergyStorage storage)
    {
        super(gui, posX, posY, storage);
    }

    @Override
    public void addTooltip(List<String> list)
    {

        if (storage.getMaxEnergyStored() < 0)
        {
            list.add("Infinite RF");
        } else
        {
            list.add(StringHelper.getEnergyString(storage.getEnergyStored()) + " / " + StringHelper.getEnergyString(storage.getMaxEnergyStored()) + " RF");
        }
    }
}
