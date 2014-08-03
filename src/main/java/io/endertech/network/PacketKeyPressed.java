package io.endertech.network;

import io.endertech.item.IKeyHandler;
import io.endertech.util.Key;
import net.minecraft.entity.player.EntityPlayer;

public class PacketKeyPressed extends PacketETBase
{
    public static void init()
    {
        PacketHandler.instance.registerPacket(PacketKeyPressed.class);
    }

    public void handlePacket(EntityPlayer entityPlayer, boolean isServer)
    {
        if (entityPlayer.getCurrentEquippedItem() != null && entityPlayer.getCurrentEquippedItem().getItem() instanceof IKeyHandler)
        {
            ((IKeyHandler) entityPlayer.getCurrentEquippedItem().getItem()).handleKey(entityPlayer, entityPlayer.getCurrentEquippedItem(), Key.fromByte(this.getByte()));
        }
    }

    public void sendKeyPressedPacket(Key.KeyCode keyCode)
    {
        addByte(Key.toByte(keyCode));
        PacketHandler.sendToServer(this);
    }
}
