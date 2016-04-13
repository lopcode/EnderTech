package io.endertech.util.helper

import net.minecraft.client.gui.FontRenderer
import org.lwjgl.opengl.GL11

object FontHelper {
    fun drawItemQuantity(fontRenderer: FontRenderer, x: Int, y: Int, quantity: String) {
        val scale = if (quantity.length > 2) 0.5 else 1
        val sheight = 8 * scale
        val swidth = fontRenderer.getStringWidth(quantity) * scale

        renderText(fontRenderer, (x + 16 - swidth).toInt(), (y + 16 - sheight).toInt(), scale, quantity)
    }

    fun renderText(fontRenderer: FontRenderer, x: Int, y: Int, scale: Double, text: String) {
        GL11.glDisable(GL11.GL_LIGHTING)
        GL11.glDisable(GL11.GL_DEPTH_TEST)
        GL11.glPushMatrix()
        GL11.glTranslated(x.toDouble(), y.toDouble(), 0.0)
        GL11.glScaled(scale, scale, 1.0)
        fontRenderer.drawStringWithShadow(text, 0f, 0f, 0xFFFFFF)
        GL11.glPopMatrix()
        GL11.glEnable(GL11.GL_LIGHTING)
        GL11.glEnable(GL11.GL_DEPTH_TEST)
    }
}
