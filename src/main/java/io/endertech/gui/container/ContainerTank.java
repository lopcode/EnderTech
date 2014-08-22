package io.endertech.gui.container;

import io.endertech.multiblock.tile.TileTankPart;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;

public class ContainerTank extends ContainerETBase
{
    TileTankPart tileTankPart;

    public ContainerTank(InventoryPlayer inventoryPlayer, TileEntity tileEntity)
    {
        super(inventoryPlayer, tileEntity);

        this.tileTankPart = ((TileTankPart) tileEntity);
    }
}
