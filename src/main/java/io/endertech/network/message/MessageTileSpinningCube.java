package io.endertech.network.message;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.endertech.helper.LogHelper;
import io.endertech.tile.TileSpinningCube;
import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Vec3;

public class MessageTileSpinningCube implements IMessage, IMessageHandler<MessageTileSpinningCube, IMessage>
{
    public int x, y, z;
    public double rx, ry, rz;
    public double speed;

    public MessageTileSpinningCube()
    {
    }

    public MessageTileSpinningCube(TileSpinningCube tileSpinningCube)
    {
        this.x = tileSpinningCube.xCoord;
        this.y = tileSpinningCube.yCoord;
        this.z = tileSpinningCube.zCoord;
        this.rx = tileSpinningCube.randomAddition.xCoord;
        this.ry = tileSpinningCube.randomAddition.yCoord;
        this.rz = tileSpinningCube.randomAddition.zCoord;
        this.speed = tileSpinningCube.speed;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.x = buf.readInt();
        this.y = buf.readInt();
        this.z = buf.readInt();
        this.rx = buf.readDouble();
        this.ry = buf.readDouble();
        this.rz = buf.readDouble();
        this.speed = buf.readDouble();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(this.x);
        buf.writeInt(this.y);
        buf.writeInt(this.z);
        buf.writeDouble(this.rx);
        buf.writeDouble(this.ry);
        buf.writeDouble(this.rz);
        buf.writeDouble(this.speed);
    }

    @Override
    public IMessage onMessage(MessageTileSpinningCube message, MessageContext ctx)
    {
        TileEntity tileEntity = FMLClientHandler.instance().getClient().theWorld.getTileEntity(message.x, message.y, message.z);

        if (tileEntity instanceof TileSpinningCube)
        {
            ((TileSpinningCube) tileEntity).speed = message.speed;
            ((TileSpinningCube) tileEntity).randomAddition = Vec3.createVectorHelper(message.rx, message.ry, message.rz);

            LogHelper.info("Setting cube data from message: " + tileEntity.toString());
        }

        return null;
    }
}
