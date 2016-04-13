package io.endertech.modules.dev;

import com.google.common.eventbus.Subscribe;
import io.drakon.pulsar.pulse.Pulse;
import io.endertech.modules.dev.block.DevBlocks;
import io.endertech.modules.dev.fluid.DevETFluids;
import io.endertech.util.helper.LogHelper;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Pulse(id = "DevEnvironmentPulse", description = "Loads in-dev content", forced = true)
public class DevEnvironmentPulse
{
    @Subscribe
    public void preInit(FMLPreInitializationEvent fmlPreInitializationEvent)
    {
        LogHelper.INSTANCE.info("Dev environment pulse preInit");

        DevBlocks.init();
        DevETFluids.init();
    }

    @Subscribe
    public void init(FMLInitializationEvent fmlInitializationEvent)
    {
        LogHelper.INSTANCE.info("Dev environment pulse init");
    }

    @Subscribe
    public void postInit(FMLPostInitializationEvent fmlPostInitializationEvent)
    {
        LogHelper.INSTANCE.info("Dev environment pulse postInit");
    }
}
