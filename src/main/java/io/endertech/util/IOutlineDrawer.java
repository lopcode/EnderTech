package io.endertech.util;

import net.minecraftforge.client.event.DrawBlockHighlightEvent;

public interface IOutlineDrawer
{
    public boolean drawOutline(DrawBlockHighlightEvent event);
}
