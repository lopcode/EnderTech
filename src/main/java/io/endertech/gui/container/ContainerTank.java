package io.endertech.gui.container;

import cofh.lib.gui.slot.SlotEnergy;
import io.endertech.multiblock.controller.ControllerTank;
import io.endertech.multiblock.tile.TileTankPart;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;

public class ContainerTank extends ContainerETBase
{
    public TileTankPart tileTankPart;

    public ContainerTank(InventoryPlayer inventoryPlayer, TileEntity tileEntity)
    {
        super(inventoryPlayer, tileEntity);

        this.tileTankPart = ((TileTankPart) tileEntity);

        ControllerTank controller = this.tileTankPart.getTankController();
        this.addSlotToContainer(new SlotEnergy(controller, controller.getChargeSlot(), 8, 53));
    }
}
