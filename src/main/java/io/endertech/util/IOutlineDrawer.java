package io.endertech.util;

import net.minecraftforge.client.event.DrawBlockHighlightEvent;

public interface IOutlineDrawer
{
    public void drawOutline(DrawBlockHighlightEvent event);
}
