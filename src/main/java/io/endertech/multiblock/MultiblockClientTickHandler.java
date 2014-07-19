package io.endertech.multiblock;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.client.Minecraft;

public class MultiblockClientTickHandler
{
    @SubscribeEvent
    public void tickStart(TickEvent.ClientTickEvent event)
    {
        if (event.phase == TickEvent.Phase.START) MultiblockRegistry.tickStart(Minecraft.getMinecraft().theWorld);
        else if (event.phase == TickEvent.Phase.END) MultiblockRegistry.tickEnd(Minecraft.getMinecraft().theWorld);
    }
}
