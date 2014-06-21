package io.endertech.network.packet;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import cpw.mods.fml.common.network.Player;
import io.endertech.items.IKeyHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;

public class PacketKeyPressed extends PacketET
{
    private byte keyCode;

    public PacketKeyPressed(byte keyCode)
    {
        this.keyCode = keyCode;
    }

    public PacketKeyPressed()
    {
    } // Do not optimise away, needed for reflection

    @Override
    public void write(ByteArrayDataOutput out)
    {
        out.writeByte(this.keyCode);
    }

    @Override
    public void read(ByteArrayDataInput in) throws ProtocolException
    {
        keyCode = in.readByte();
    }

    @Override
    public void execute(INetworkManager networkManager, Player player) throws ProtocolException
    {
        EntityPlayer thePlayer = (EntityPlayer) player;

        if (thePlayer.getCurrentEquippedItem() != null && thePlayer.getCurrentEquippedItem().getItem() instanceof IKeyHandler)
        {
            ((IKeyHandler) thePlayer.getCurrentEquippedItem().getItem()).handleKey(thePlayer, thePlayer.getCurrentEquippedItem(), keyCode);
        }
    }
}
