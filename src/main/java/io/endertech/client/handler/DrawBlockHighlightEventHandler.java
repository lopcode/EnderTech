package io.endertech.client.handler;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import io.endertech.util.IOutlineDrawer;
import net.minecraft.item.Item;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;

public class DrawBlockHighlightEventHandler
{
    @SubscribeEvent
    public void onDrawBlockHighlightEvent(DrawBlockHighlightEvent event)
    {
        if (event.currentItem == null) return;

        if (event.target.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK) return;

        Item item = event.currentItem.getItem();
        if (!(item instanceof IOutlineDrawer)) return;

        boolean cancelEvent = ((IOutlineDrawer) item).drawOutline(event);
        event.setCanceled(cancelEvent);

    }
}
