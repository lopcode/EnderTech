package io.endertech.gui.container;

import io.endertech.tile.TileChargePad;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;

public class ContainerChargePad extends ContainerETBase
{
    TileChargePad tileChargePad;

    public ContainerChargePad(InventoryPlayer inventoryPlayer, TileEntity tileEntity)
    {
        super(inventoryPlayer, tileEntity);

        this.tileChargePad = ((TileChargePad) tileEntity);
    }
}
