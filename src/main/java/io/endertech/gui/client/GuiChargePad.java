package io.endertech.gui.client;

import io.endertech.gui.container.ContainerChargePad;
import io.endertech.gui.element.ElementETEnergyStored;
import io.endertech.gui.element.ElementIcon;
import io.endertech.tile.TileChargePad;
import io.endertech.tile.TileET;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

public class GuiChargePad extends GuiETBase
{
    public static final String TEXTURE_PATH = "endertech:textures/gui/ChargePad.png";
    public static final ResourceLocation TEXTURE = new ResourceLocation(TEXTURE_PATH);
    public TileChargePad tileChargePad;
    private ElementIcon elementChargingIcon;

    public GuiChargePad(InventoryPlayer inventoryPlayer, TileET tileEntity)
    {
        super(new ContainerChargePad(inventoryPlayer, tileEntity), TEXTURE, tileEntity);

        this.tileChargePad = (TileChargePad) tileEntity;
        this.name = this.tileChargePad.getName();
    }

    @Override
    public void initGui()
    {
        super.initGui();

        ElementETEnergyStored elementEnergyStored = new ElementETEnergyStored(this, 8, 8, this.tileChargePad, tileChargePad.isCreative);
        this.addElement(elementEnergyStored);

        elementChargingIcon = new ElementIcon(this, 80, 30);
        this.addElement(elementChargingIcon);
    }

    @Override
    protected void updateElementInformation()
    {
        elementChargingIcon.setIconToDraw(tileChargePad.getFrontIcon());
    }
}
