package io.endertech.gui.container;

import cofh.lib.gui.slot.SlotEnergy;
import io.endertech.tile.TileHealthPad;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;

public class ContainerHealthPad extends ContainerETBase
{
    TileHealthPad tileHealthPad;

    public ContainerHealthPad(InventoryPlayer inventoryPlayer, TileEntity tileEntity)
    {
        super(inventoryPlayer, tileEntity);

        this.tileHealthPad = ((TileHealthPad) tileEntity);
        this.addSlotToContainer(new SlotEnergy(this.tileHealthPad, this.tileHealthPad.getChargeSlot(), 8, 53));
    }
}
