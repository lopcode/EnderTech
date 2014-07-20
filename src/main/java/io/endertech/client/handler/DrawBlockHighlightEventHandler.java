package io.endertech.client.handler;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import io.endertech.util.IOutlineDrawer;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;

public class DrawBlockHighlightEventHandler
{
    @SubscribeEvent
    public void onDrawBlockHighlightEvent(DrawBlockHighlightEvent event)
    {
        if (event.target.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK) return;
        Block block = event.player.worldObj.getBlock(event.target.blockX, event.target.blockY, event.target.blockZ);

        boolean cancelEvent = false;
        boolean drewItem = false;

        if (event.currentItem != null)
        {
            Item item = event.currentItem.getItem();
            if (item instanceof IOutlineDrawer)
            {
                cancelEvent = ((IOutlineDrawer) item).drawOutline(event);
                drewItem = !cancelEvent;
            }
        }

        if (!drewItem && block instanceof IOutlineDrawer)
        {
            cancelEvent = ((IOutlineDrawer) block).drawOutline(event);
        }

        event.setCanceled(cancelEvent);
    }
}
