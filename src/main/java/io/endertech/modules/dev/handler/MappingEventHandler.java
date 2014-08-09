package io.endertech.modules.dev.handler;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLModIdMappingEvent;
import io.endertech.util.fluid.BucketHandler;

public class MappingEventHandler
{
    @Mod.EventHandler
    public void handleIdMappingEvent(FMLModIdMappingEvent event)
    {
        BucketHandler.refreshMap();
    }
}
