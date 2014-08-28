package io.endertech.gui.client;

import io.endertech.gui.container.ContainerTank;
import io.endertech.gui.element.ElementETEnergyStored;
import io.endertech.gui.element.ElementFluidTankSizeable;
import io.endertech.multiblock.tile.TileTankPart;
import io.endertech.tile.TileET;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

public class GuiTank extends GuiETBase
{
    public static final String TEXTURE_PATH = "endertech:textures/gui/Tank.png";
    public static final ResourceLocation TEXTURE = new ResourceLocation(TEXTURE_PATH);
    public TileTankPart tileTankPart;

    public GuiTank(InventoryPlayer inventoryPlayer, TileET tileEntity)
    {
        super(new ContainerTank(inventoryPlayer, tileEntity), TEXTURE, tileEntity);

        this.tileTankPart = (TileTankPart) tileEntity;
        this.name = "Ender Tank";
    }

    @Override
    public void initGui()
    {
        super.initGui();

        ElementFluidTankSizeable elementFluidTank = new ElementFluidTankSizeable(this, 43, 27, 89, 42, this.tileTankPart.getTankController().tank);
        this.addElement(elementFluidTank);

        ElementETEnergyStored elementEnergyStored = new ElementETEnergyStored(this, 8, 8, this.tileTankPart.getTankController(), false);
        this.addElement(elementEnergyStored);
    }
}
