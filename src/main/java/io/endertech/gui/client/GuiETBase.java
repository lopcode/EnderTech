package io.endertech.gui.client;

import cofh.lib.gui.GuiBase;
import cofh.lib.render.RenderHelper;
import io.endertech.tile.TileET;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.inventory.Container;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import org.lwjgl.opengl.GL11;

// Contains overridden CoFHLib functions for more advanced fluid rendering in GUIs

public class GuiETBase extends GuiBase
{
    public TileET tileET;

    public GuiETBase(Container container, ResourceLocation texture, TileET tileET)
    {
        super(container, texture);

        this.tileET = tileET;
    }

    @Override
    public void initGui()
    {
        super.initGui();
    }

    @Override
    public void updateScreen()
    {
        super.updateScreen();

        if (!tileET.hasGui())
        {
            this.mc.thePlayer.closeScreen();
        }
    }

    public void drawFluidWithOpacity(int x, int y, FluidStack fluid, int width, int height, float opacity)
    {
        if (fluid == null || fluid.getFluid() == null)
        {
            return;
        }

        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        RenderHelper.setBlockTextureSheet();

        this.drawTiledTextureWithColour(x, y, fluid.getFluid().getIcon(fluid), width, height, fluid.getFluid().getColor(fluid), opacity);
        GL11.glPopMatrix();
    }

    public void drawTiledTextureWithColour(int x, int y, IIcon icon, int width, int height, int colour, float opacity)
    {
        int i = 0;
        int j = 0;

        int drawHeight = 0;
        int drawWidth = 0;

        for (i = 0; i < width; i += 16)
        {
            for (j = 0; j < height; j += 16)
            {
                drawWidth = Math.min(width - i, 16);
                drawHeight = Math.min(height - j, 16);
                this.drawScaledTexturedModelRectFromIconWithColour(x + i, y + j, icon, drawWidth, drawHeight, colour, opacity);
            }
        }
    }

    public void drawScaledTexturedModelRectFromIconWithColour(int x, int y, IIcon icon, int width, int height, int colour, float opacity)
    {
        if (icon == null)
        {
            return;
        }
        double minU = icon.getMinU();
        double maxU = icon.getMaxU();
        double minV = icon.getMinV();
        double maxV = icon.getMaxV();

        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();

        final float r = (colour >> 16 & 0xFF) / 255.0F;
        final float g = (colour >> 8 & 0xFF) / 255.0F;
        final float b = (colour & 0xFF) / 255.0F;

        tessellator.setColorRGBA_F(r, g, b, opacity);

        tessellator.addVertexWithUV(x + 0, y + height, this.zLevel, minU, minV + (maxV - minV) * height / 16F);
        tessellator.addVertexWithUV(x + width, y + height, this.zLevel, minU + (maxU - minU) * width / 16F, minV + (maxV - minV) * height / 16F);
        tessellator.addVertexWithUV(x + width, y + 0, this.zLevel, minU + (maxU - minU) * width / 16F, minV);
        tessellator.addVertexWithUV(x + 0, y + 0, this.zLevel, minU, minV);
        tessellator.draw();
    }
}
