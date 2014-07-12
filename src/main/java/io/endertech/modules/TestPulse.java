package io.endertech.modules;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import io.drakon.pulsar.pulse.Handler;
import io.drakon.pulsar.pulse.Pulse;
import io.endertech.util.LogHelper;

@Pulse(id = "TestPulse", description = "This one's for testing. It goes up to 11.")
public class TestPulse
{
    @Handler
    public void preInit(FMLPreInitializationEvent fmlPreInitializationEvent)
    {
        LogHelper.info("Test pulse preInit");
    }

    @Handler
    public void init(FMLInitializationEvent fmlInitializationEvent)
    {
        LogHelper.info("Test pulse init");
    }

    @Handler
    public void postInit(FMLPostInitializationEvent fmlPostInitializationEvent)
    {
        LogHelper.info("Test pulse postInit");
    }
}
