package io.endertech.gui.element;

import cofh.lib.gui.element.ElementFluidTank;
import io.endertech.config.GeneralConfig;
import io.endertech.gui.client.GuiETBase;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;

public class ElementFluidTankSizeable extends ElementFluidTank
{
    public ElementFluidTankSizeable(GuiETBase gui, int posX, int posY, int sizeX, int sizeY, IFluidTank tank)
    {
        super(gui, posX, posY, tank);
        this.sizeX = sizeX;
        this.sizeY = sizeY;
    }

    @Override
    public void drawBackground(int mouseX, int mouseY, float gameTicks)
    {
        int amount = this.getScaled();

        FluidStack fluidStack = tank.getFluid();
        if (fluidStack == null) return;

        Fluid fluid = fluidStack.getFluid();
        if (fluid == null) return;

        if (fluid.isGaseous(fluidStack) && !GeneralConfig.gasTopToBottom)
        {
            float opacity = ((tank.getFluidAmount() / (float) tank.getCapacity()));
            if (opacity < 0.10F) opacity = 0.10F;

            ((GuiETBase) gui).drawFluidWithOpacity(posX, posY, tank.getFluid(), sizeX, sizeY, opacity);
        } else
        {
            gui.drawFluid(posX, posY + sizeY - amount, tank.getFluid(), sizeX, amount);
        }
    }

    int getScaled()
    {

        return tank.getFluidAmount() * sizeY / tank.getCapacity();
    }
}
