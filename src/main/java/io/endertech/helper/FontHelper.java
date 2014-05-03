package io.endertech.helper;

import net.minecraft.client.gui.FontRenderer;
import org.lwjgl.opengl.GL11;

public class FontHelper
{
    public static void drawItemQuantity(FontRenderer fontRenderer, int x, int y, String quantity)
    {
        double scale = quantity.length() > 2 ? 0.5 : 1;
        double sheight = 8 * scale;
        double swidth = fontRenderer.getStringWidth(quantity) * scale;

        renderText(fontRenderer, (int) (x + 16 - swidth), (int) (y + 16 - sheight), scale, quantity);
    }

    public static void renderText(FontRenderer fontRenderer, int x, int y, double scale, String text)
    {
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glPushMatrix();
        GL11.glTranslated(x, y, 0);
        GL11.glScaled(scale, scale, 1);
        fontRenderer.drawStringWithShadow(text, 0, 0, 0xFFFFFF);
        GL11.glPopMatrix();
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
    }
}
