package io.endertech.gui.element;

import cofh.lib.gui.GuiBase;
import cofh.lib.gui.element.ElementFluidTank;
import net.minecraftforge.fluids.IFluidTank;

public class ElementFluidTankSizeable extends ElementFluidTank
{
    public ElementFluidTankSizeable(GuiBase gui, int posX, int posY, int sizeX, int sizeY, IFluidTank tank)
    {
        super(gui, posX, posY, tank);
        this.sizeX = sizeX;
        this.sizeY = sizeY;
    }

    @Override
    public void drawBackground(int mouseX, int mouseY, float gameTicks)
    {

        int amount = this.getScaled();

        gui.drawFluid(posX, posY + sizeY - amount, tank.getFluid(), sizeX, amount);
        //RenderHelper.bindTexture(texture);
        //drawTexturedModalRect(posX, posY, 32 + gaugeType * 16, 1, sizeX, sizeY);
    }

    int getScaled()
    {

        return tank.getFluidAmount() * sizeY / tank.getCapacity();
    }
}
