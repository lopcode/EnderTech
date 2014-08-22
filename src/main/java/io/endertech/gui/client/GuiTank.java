package io.endertech.gui.client;

import cofh.lib.gui.element.ElementEnergyStored;
import cofh.lib.gui.element.ElementFluidTank;
import io.endertech.gui.container.ContainerTank;
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
    }

    @Override
    public void initGui()
    {
        super.initGui();

        ElementFluidTank elementFluidTank = new ElementFluidTank(this, 42, 8, this.tileTankPart.getTankController().tank);
        elementFluidTank.setSize(127, ElementFluidTank.DEFAULT_SCALE);
        this.addElement(elementFluidTank);

        ElementEnergyStored elementEnergyStored = new ElementEnergyStored(this, 8, 8, this.tileTankPart.getTankController());
        this.addElement(elementEnergyStored);
    }
}
