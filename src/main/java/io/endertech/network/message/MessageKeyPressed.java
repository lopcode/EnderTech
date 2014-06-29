package io.endertech.network.message;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.endertech.items.IKeyHandler;
import io.endertech.util.Key;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;

public class MessageKeyPressed implements IMessage, IMessageHandler<MessageKeyPressed, IMessage>
{
    private byte keyCode;

    public MessageKeyPressed() {} // Do not optimise away

    public MessageKeyPressed(Key.KeyCode key)
    {
        this.keyCode = Key.toByte(key);
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.keyCode = buf.readByte();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeByte(this.keyCode);
    }

    @Override
    public IMessage onMessage(MessageKeyPressed message, MessageContext ctx)
    {
        EntityPlayer player = ctx.getServerHandler().playerEntity;

        if (player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() instanceof IKeyHandler)
        {
            ((IKeyHandler) player.getCurrentEquippedItem().getItem()).handleKey(player, player.getCurrentEquippedItem(), Key.fromByte(message.keyCode));
        }

        return null;
    }
}
