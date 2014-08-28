package io.endertech.gui.element;

import cofh.api.energy.IEnergyStorage;
import cofh.lib.gui.GuiBase;
import cofh.lib.gui.element.ElementEnergyStored;
import cofh.lib.render.RenderHelper;
import io.endertech.util.helper.StringHelper;
import java.util.List;

public class ElementETEnergyStored extends ElementEnergyStored
{
    public boolean isCreative;

    public ElementETEnergyStored(GuiBase gui, int posX, int posY, IEnergyStorage storage, boolean isCreative)
    {
        super(gui, posX, posY, storage);

        this.isCreative = isCreative;
    }

    @Override
    public void drawBackground(int mouseX, int mouseY, float gameTicks)
    {
        // TODO: Override getScaled when it is made public in CoFHLib instead
        if (this.isCreative)
        {
            RenderHelper.bindTexture(texture);
            drawTexturedModalRect(posX, posY, 0, 0, sizeX, sizeY);
            drawTexturedModalRect(posX, posY, 16, 0, sizeX, DEFAULT_SCALE);

            return;
        }

        super.drawBackground(mouseX, mouseY, gameTicks);
    }

    @Override
    public void addTooltip(List<String> list)
    {
        if (this.isCreative)
        {
            list.add("Infinite RF");
            return;
        }


        list.add(StringHelper.getEnergyString(storage.getEnergyStored()) + " / " + StringHelper.getEnergyString(storage.getMaxEnergyStored()) + " RF");
    }
}
