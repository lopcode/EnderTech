package io.endertech.client;

import cpw.mods.fml.common.FMLCommonHandler;
import io.endertech.common.CommonProxy;

public class ClientProxy extends CommonProxy
{
    @Override
    public void registerTickerHandlers()
    {
        super.registerTickerHandlers();

        FMLCommonHandler.instance().bus().register(new KeyBindingHandler());
    }
}
