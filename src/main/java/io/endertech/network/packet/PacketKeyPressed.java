package io.endertech.network.packet;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import cpw.mods.fml.common.network.Player;
import io.endertech.items.IKeyHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;

public class PacketKeyPressed extends PacketET
{
    private String keyDescription;

    public PacketKeyPressed(String keyDescription)
    {
        this.keyDescription = keyDescription;
    }

    public PacketKeyPressed()
    {
    } // Do not optimise away, needed for reflection

    @Override
    public void write(ByteArrayDataOutput out)
    {
        out.writeUTF(this.keyDescription);
    }

    @Override
    public void read(ByteArrayDataInput in) throws ProtocolException
    {
        keyDescription = in.readUTF();
    }

    @Override
    public void execute(INetworkManager networkManager, Player player) throws ProtocolException
    {
        EntityPlayer thePlayer = (EntityPlayer) player;

        if (thePlayer.getCurrentEquippedItem() != null && thePlayer.getCurrentEquippedItem().getItem() instanceof IKeyHandler)
        {
            ((IKeyHandler) thePlayer.getCurrentEquippedItem().getItem()).handleKey(thePlayer, thePlayer.getCurrentEquippedItem(), keyDescription);
        }
    }
}
