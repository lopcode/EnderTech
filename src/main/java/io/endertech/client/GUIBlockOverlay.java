package io.endertech.client;

import io.endertech.helper.FontHelper;
import io.endertech.helper.InventoryHelper;
import io.endertech.items.ItemExchanger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.EventPriority;
import net.minecraftforge.event.ForgeSubscribe;
import org.lwjgl.opengl.GL11;

public class GUIBlockOverlay extends Gui
{
    private Minecraft mc;
    private RenderItem ri = new RenderItem();
    private ItemStack lastExchangeSource = null;
    private int lastExchangeSourceCount = 0;

    public GUIBlockOverlay(Minecraft mc)
    {
        super();

        this.mc = mc;
    }

    @ForgeSubscribe(priority = EventPriority.NORMAL)
    public void onRenderExperienceBar(RenderGameOverlayEvent event)
    {
        if (event.isCancelable() || event.type != RenderGameOverlayEvent.ElementType.EXPERIENCE)
        {
            return;
        }

        if ((mc.renderViewEntity instanceof EntityPlayer))
        {
            EntityPlayer player = (EntityPlayer) mc.renderViewEntity;

            if ((player != null) && (mc.inGameHasFocus) && (Minecraft.isGuiEnabled()))
            {
                if (player.inventory.getCurrentItem() != null)
                {
                    if ((player.inventory.getCurrentItem().getItem() instanceof ItemExchanger))
                    {
                        ItemExchanger exchanger = (ItemExchanger) player.inventory.getCurrentItem().getItem();
                        ItemStack exchangerStack = player.inventory.getCurrentItem();
                        ItemStack source = exchanger.getSourceBlock(player.inventory.getCurrentItem());
                        if (source != null)
                        {
                            //LogHelper.info("Rendering item");

                            if (player.inventory.inventoryChanged || !source.isItemEqual(this.lastExchangeSource))
                            {
                                this.lastExchangeSourceCount = InventoryHelper.getExtractableQuantity(player.inventory, source);

                                player.inventory.inventoryChanged = false;
                                this.lastExchangeSource = source;
                            }

                            // I have no idea what I'm doing

                            GL11.glPushMatrix();
                            GL11.glDisable(GL11.GL_LIGHTING);

                            GL11.glEnable(32826 /* GL_RESCALE_NORMAL_EXT */);
                            GL11.glEnable(GL11.GL_COLOR_MATERIAL);
                            GL11.glEnable(GL11.GL_LIGHTING);

                            net.minecraft.client.renderer.RenderHelper.enableGUIStandardItemLighting();
                            ri.renderItemAndEffectIntoGUI(mc.fontRenderer, mc.renderEngine, this.lastExchangeSource, 2, 2);

                            GL11.glDisable(GL11.GL_LIGHTING);
                            GL11.glPushMatrix();

                            String am = Integer.toString(this.lastExchangeSourceCount);
                            if (exchanger.isCreative(exchangerStack)) am = "Inf"; // infinity

                            FontHelper.drawItemQuantity(mc.fontRenderer, 3, 3, am);
                            FontHelper.renderText(mc.fontRenderer, 2 + 16 + 2, 3, 1.0, "Radius: " + exchanger.getTargetRadius(exchangerStack));

                            GL11.glPopMatrix();
                            GL11.glPopMatrix();
                        }
                    }
                }
            }
        }
    }
}
