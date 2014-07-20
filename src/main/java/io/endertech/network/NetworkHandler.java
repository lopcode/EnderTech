package io.endertech.network;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import io.endertech.network.message.MessageKeyPressed;
import io.endertech.reference.Reference;

public class NetworkHandler
{
    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(Reference.MOD_ID.toLowerCase());
    private static int discriminator = -1;

    public static int getDiscriminator()
    {
        discriminator++;
        return discriminator;
    }

    public static void init()
    {
        INSTANCE.registerMessage(MessageKeyPressed.class, MessageKeyPressed.class, getDiscriminator(), Side.SERVER);
    }
}
