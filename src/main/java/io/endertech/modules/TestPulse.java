package io.endertech.modules;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import io.drakon.pulsar.pulse.IPulse;
import io.drakon.pulsar.pulse.Pulse;
import io.endertech.helper.LogHelper;

@Pulse(id = "TestPulse")
public class TestPulse implements IPulse
{
    @Override
    public void preInit(FMLPreInitializationEvent fmlPreInitializationEvent)
    {
        LogHelper.info("Test pulse preInit");
    }

    @Override
    public void init(FMLInitializationEvent fmlInitializationEvent)
    {
        LogHelper.info("Test pulse init");
    }

    @Override
    public void postInit(FMLPostInitializationEvent fmlPostInitializationEvent)
    {
        LogHelper.info("Test pulse postInit");
    }
}
