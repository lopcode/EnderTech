package io.endertech.gui.client;

import cofh.lib.gui.GuiBase;
import io.endertech.tile.TileET;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;

public class GuiETBase extends GuiBase
{
    public TileET tileET;

    public GuiETBase(Container container, ResourceLocation texture, TileET tileET)
    {
        super(container, texture);

        this.tileET = tileET;
    }

    @Override
    public void initGui()
    {
        super.initGui();
    }

    @Override
    public void updateScreen()
    {
        super.updateScreen();

        if (!tileET.hasGui())
        {
            this.mc.thePlayer.closeScreen();
        }
    }
}
