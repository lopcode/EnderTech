package io.endertech.gui.client;

import cofh.lib.gui.element.ElementEnergyStored;
import io.endertech.gui.container.ContainerHealthPad;
import io.endertech.gui.element.ElementIcon;
import io.endertech.tile.TileET;
import io.endertech.tile.TileHealthPad;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

public class GuiHealthPad extends GuiETBase
{
    public static final String TEXTURE_PATH = "endertech:textures/gui/ChargePad.png";
    public static final ResourceLocation TEXTURE = new ResourceLocation(TEXTURE_PATH);
    public TileHealthPad tileHealthPad;
    private ElementIcon elementChargingIcon;

    public GuiHealthPad(InventoryPlayer inventoryPlayer, TileET tileEntity)
    {
        super(new ContainerHealthPad(inventoryPlayer, tileEntity), TEXTURE, tileEntity);

        this.tileHealthPad = (TileHealthPad) tileEntity;
        this.name = this.tileHealthPad.getName();
    }

    @Override
    public void initGui()
    {
        super.initGui();

        ElementEnergyStored elementEnergyStored = new ElementEnergyStored(this, 8, 8, this.tileHealthPad);
        this.addElement(elementEnergyStored);

        elementChargingIcon = new ElementIcon(this, 80, 30);
        this.addElement(elementChargingIcon);
    }

    @Override
    protected void updateElementInformation()
    {
        elementChargingIcon.setIconToDraw(tileHealthPad.getFrontIcon());
    }
}
