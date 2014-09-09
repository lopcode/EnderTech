package io.endertech.tile;

import cofh.lib.util.helpers.ServerHelper;
import cpw.mods.fml.relauncher.Side;
import io.endertech.EnderTech;
import io.endertech.network.ITilePacketHandler;
import io.endertech.network.PacketETBase;
import io.endertech.network.PacketHandler;
import io.endertech.network.PacketTile;
import io.endertech.util.IETWailaProvider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import java.util.List;

public class TileET extends TileEntity implements ITilePacketHandler, IETWailaProvider
{
    protected String tileName;
    protected ForgeDirection orientation;
    // Network Communication

    public TileET()
    {
        super();

        this.tileName = "";
        this.orientation = ForgeDirection.SOUTH;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbtTagCompound)
    {
        super.readFromNBT(nbtTagCompound);

        if (nbtTagCompound.hasKey("orientation"))
        {
            this.orientation = ForgeDirection.getOrientation(nbtTagCompound.getByte("orientation"));
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbtTagCompound)
    {
        super.writeToNBT(nbtTagCompound);

        nbtTagCompound.setByte("orientation", (byte) this.orientation.ordinal());
    }

    @Override
    public Packet getDescriptionPacket()
    {
        return PacketHandler.toMCPacket(getPacket());
    }

    public PacketETBase getPacket()
    {
        PacketETBase packet = new PacketTile(this);
        packet.addString(this.tileName);
        packet.addByte(this.orientation.ordinal());
        return packet;
    }

    public void sendDescriptionPacket()
    {
        PacketHandler.sendToAllAround(this.getPacket(), this);
    }

    public void sendUpdatePacket(Side side)
    {
        if (this.worldObj == null) return;

        if (side == Side.CLIENT && ServerHelper.isServerWorld(this.worldObj))
        {
            this.sendDescriptionPacket();
            this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
        } else if (side == Side.SERVER && ServerHelper.isClientWorld(this.worldObj))
        {
            PacketHandler.sendToServer(this.getPacket());
        }
    }

    @Override
    public void handleTilePacket(PacketETBase tilePacket, boolean isServer)
    {
        String tileName = tilePacket.getString();
        byte orientation = tilePacket.getByte();
        if (ServerHelper.isClientWorld(this.worldObj))
        {
            this.tileName = tileName;
            this.orientation = ForgeDirection.getOrientation(orientation);
        }

        this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
    }

    public void setOrientation(int orientation)
    {
        this.orientation = ForgeDirection.getOrientation(orientation);
    }

    public ForgeDirection getOrientation()
    {
        return orientation;
    }

    public void setOrientation(ForgeDirection orientation)
    {
        this.orientation = orientation;
    }

    @Override
    public ItemStack getWailaStack()
    {
        return null;
    }

    @Override
    public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip)
    {
        return currenttip;
    }

    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip)
    {
        return currenttip;
    }

    @Override
    public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip)
    {
        return currenttip;
    }

    public Object getGuiClient(InventoryPlayer inventory)
    {
        return null;
    }

    public Object getGuiServer(InventoryPlayer inventory)
    {
        return null;
    }

    public boolean hasGui()
    {
        return false;
    }

    public boolean openGui(EntityPlayer player)
    {
        boolean hasGui = this.hasGui();
        if (hasGui)
        {
            player.openGui(EnderTech.instance, 0, this.worldObj, this.xCoord, this.yCoord, this.zCoord);
        }

        return hasGui;
    }

    public boolean canInteractWith(EntityPlayer player)
    {
        double distance = player.getDistanceSq(this.xCoord, this.yCoord, this.zCoord);
        TileEntity tile = this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord);
        return (distance <= 64D && tile == this);
    }

    public boolean hasItemState()
    {
        return false;
    }

    public void readStateFromNBT(NBTTagCompound nbtTagCompound)
    {
        return;
    }

    public void writeStateToNBT(NBTTagCompound nbtTagCompound)
    {
        return;
    }

    public String getName()
    {
        return this.tileName;
    }
}
