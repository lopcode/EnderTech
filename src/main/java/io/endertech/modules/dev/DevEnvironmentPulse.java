package io.endertech.modules.dev;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import io.drakon.pulsar.pulse.Handler;
import io.drakon.pulsar.pulse.Pulse;
import io.endertech.modules.dev.block.DevBlocks;
import io.endertech.modules.dev.fluid.DevETFluids;
import io.endertech.util.LogHelper;

@Pulse(id = "DevEnvironmentPulse", description = "Loads in-dev content", forced = true)
public class DevEnvironmentPulse
{
    @Handler
    public void preInit(FMLPreInitializationEvent fmlPreInitializationEvent)
    {
        LogHelper.info("Dev environment pulse preInit");

        DevBlocks.init();
        DevETFluids.init();
    }

    @Handler
    public void init(FMLInitializationEvent fmlInitializationEvent)
    {
        LogHelper.info("Dev environment pulse init");
    }

    @Handler
    public void postInit(FMLPostInitializationEvent fmlPostInitializationEvent)
    {
        LogHelper.info("Dev environment pulse postInit");
    }
}
