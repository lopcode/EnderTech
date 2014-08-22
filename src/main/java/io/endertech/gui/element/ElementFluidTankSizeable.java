package io.endertech.gui.element;

import cofh.lib.gui.GuiBase;
import cofh.lib.gui.element.ElementFluidTank;
import net.minecraftforge.fluids.IFluidTank;

public class ElementFluidTankSizeable extends ElementFluidTank
{
    public ElementFluidTankSizeable(GuiBase gui, int posX, int posY, IFluidTank tank)
    {
        super(gui, posX, posY, tank);
    }
}
