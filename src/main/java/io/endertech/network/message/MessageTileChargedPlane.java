package io.endertech.network.message;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.endertech.tile.TileChargedPlane;
import io.endertech.util.LogHelper;
import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;

public class MessageTileChargedPlane implements IMessage, IMessageHandler<MessageTileChargedPlane, IMessage>
{
    public int x, y, z;

    public MessageTileChargedPlane()
    {
    }

    public MessageTileChargedPlane(TileChargedPlane chargedPlane)
    {
        this.x = chargedPlane.xCoord;
        this.y = chargedPlane.yCoord;
        this.z = chargedPlane.zCoord;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.x = buf.readInt();
        this.y = buf.readInt();
        this.z = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(this.x);
        buf.writeInt(this.y);
        buf.writeInt(this.z);
    }

    @Override
    public IMessage onMessage(MessageTileChargedPlane message, MessageContext ctx)
    {
        TileEntity tileEntity = FMLClientHandler.instance().getClient().theWorld.getTileEntity(message.x, message.y, message.z);

        if (tileEntity instanceof TileChargedPlane)
        {
            LogHelper.info("Setting charged plane data from message: " + tileEntity.toString());
        }

        return null;
    }
}
