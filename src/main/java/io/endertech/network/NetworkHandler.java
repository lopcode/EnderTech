package io.endertech.network;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import io.endertech.lib.Reference;
import io.endertech.network.message.MessageKeyPressed;
import io.endertech.network.message.MessageTileSpinningCube;

public class NetworkHandler
{
    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(Reference.MOD_ID.toLowerCase());

    public static void init()
    {
        INSTANCE.registerMessage(MessageTileSpinningCube.class, MessageTileSpinningCube.class, 0, Side.CLIENT);
        INSTANCE.registerMessage(MessageKeyPressed.class, MessageKeyPressed.class, 1, Side.SERVER);
    }
}
