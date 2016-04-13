package io.endertech.client.handler;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import io.endertech.config.GeneralConfig;
import io.endertech.item.ItemExchanger;
import io.endertech.util.helper.FontHelper;
import io.endertech.util.helper.LocalisationHelper;
import io.endertech.util.inventory.InventoryHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import org.lwjgl.opengl.GL11;

public class GUIEventHandler extends Gui
{
    private Minecraft mc;
    private RenderItem ri = new RenderItem();
    private ItemStack lastExchangeSource = null;
    private int lastExchangeSourceCount = 0;

    public GUIEventHandler(Minecraft mc)
    {
        super();

        this.mc = mc;
    }

    @SubscribeEvent
    public void onGameOverlayRender(RenderGameOverlayEvent event)
    {
        if (event.isCancelable() || event.type != RenderGameOverlayEvent.ElementType.EXPERIENCE) return;

        if (!(mc.renderViewEntity instanceof EntityPlayer)) return;

        EntityPlayer player = (EntityPlayer) mc.renderViewEntity;

        if (player == null || !mc.inGameHasFocus || !Minecraft.isGuiEnabled()) return;

        if (player.inventory.getCurrentItem() == null || !(player.inventory.getCurrentItem().getItem() instanceof ItemExchanger))
            return;

        ItemExchanger exchanger = (ItemExchanger) player.inventory.getCurrentItem().getItem();
        ItemStack exchangerStack = player.inventory.getCurrentItem();
        ItemStack source = exchanger.getSourceItemStack(player.inventory.getCurrentItem());

        if (source == null) return;

        //LogHelper.info("Rendering item");

        if (player.inventory.inventoryChanged || this.lastExchangeSource == null || !source.isItemEqual(this.lastExchangeSource))
        {
            this.lastExchangeSourceCount = InventoryHelper.getExtractableQuantity(player.inventory, source);

            player.inventory.inventoryChanged = false;
            this.lastExchangeSource = source;
        }

        // I have no idea what I'm doing

        // Null check prevents NPEs in vanilla renderItemAndEffectIntoGUI for items which drop the wrong thing
        if (this.lastExchangeSource.getItem() != null) {
            net.minecraft.client.renderer.RenderHelper.enableGUIStandardItemLighting();

            GL11.glEnable(32826 /* GL_RESCALE_NORMAL_EXT */);
            GL11.glEnable(GL11.GL_COLOR_MATERIAL);
            GL11.glEnable(GL11.GL_LIGHTING);

            ri.renderItemAndEffectIntoGUI(mc.fontRenderer, mc.renderEngine, this.lastExchangeSource, 2 + GeneralConfig.GUITopLeftXOffset, 2 + GeneralConfig.GUITopLeftYOffset);

            net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();

            GL11.glDisable(32826);
            GL11.glDisable(GL11.GL_COLOR_MATERIAL);
        }

        String am = Integer.toString(this.lastExchangeSourceCount);
        if (exchanger.isCreative(exchangerStack))
        {
            am = LocalisationHelper.localiseString("gui.exchanger.radius.infinite");
        }

        FontHelper.INSTANCE.drawItemQuantity(mc.fontRenderer, 3 + GeneralConfig.GUITopLeftXOffset, 3 + GeneralConfig.GUITopLeftYOffset, am);
        FontHelper.INSTANCE.renderText(mc.fontRenderer, 2 + 16 + 2 + GeneralConfig.GUITopLeftXOffset, 3 + GeneralConfig.GUITopLeftYOffset, 1.0, LocalisationHelper.localiseString("gui.exchanger.radius", exchanger.getTargetRadius(exchangerStack)));

        net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();
    }
}
