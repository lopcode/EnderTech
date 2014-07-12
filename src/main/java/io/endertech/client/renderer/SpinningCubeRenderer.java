package io.endertech.client.renderer;

import io.endertech.tile.TileSpinningCube;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;

public class SpinningCubeRenderer extends TileEntitySpecialRenderer
{
    private ResourceLocation emerald_block_texture = new ResourceLocation("textures/blocks/emerald_block.png");

    public void addVertexWithUVFromVector(Tessellator tessellator, Vec3 vector, float size, double U, double V)
    {
        tessellator.addVertexWithUV(vector.xCoord * size, vector.yCoord * size, vector.zCoord * size, U, V);
    }

    @Override
    public void renderTileEntityAt(TileEntity tTile, double x, double y, double z, float partialTick)
    {
        //LogHelper.info(partialTick);
        TileSpinningCube tile = (TileSpinningCube) tTile;

        this.bindTexture(emerald_block_texture);

        Tessellator tessellator = Tessellator.instance;
        GL11.glPushMatrix();

        GL11.glDisable(GL11.GL_LIGHTING);

        GL11.glScalef(1.0f, 1.0f, 1.0f);
        //GL11.glScaled(size, size, size);
        GL11.glTranslated(x + 0.5, y + 1.0 + ((Math.sin(tile.yAddition) + 1) / 3.0), z + 0.5);
        //float rotationAngle = (float) (720.0 * (System.currentTimeMillis() & 0x3FFFL) / 0x3FFFL);
        //GL11.glRotated(tile.speed, tile.directionVector.xCoord, tile.directionVector.yCoord, tile.directionVector.zCoord);

        //GL11.glRotated(tile.directionVector.xCoord * (180.0 / Math.PI), 1, 0, 0);
        //GL11.glRotated(tile.directionVector.yCoord * (180.0 / Math.PI), 0, 1, 0);
        //GL11.glRotated(tile.directionVector.zCoord * (180.0 / Math.PI), 0, 0, 1);

        tessellator.startDrawingQuads();

        //Front
        this.addVertexWithUVFromVector(tessellator, tile.cubeVertices[0], tile.size, 1, 0);
        this.addVertexWithUVFromVector(tessellator, tile.cubeVertices[1], tile.size, 1, 1);
        this.addVertexWithUVFromVector(tessellator, tile.cubeVertices[2], tile.size, 0, 1);
        this.addVertexWithUVFromVector(tessellator, tile.cubeVertices[3], tile.size, 0, 0);

        //Back
        this.addVertexWithUVFromVector(tessellator, tile.cubeVertices[5], tile.size, 1, 0);
        this.addVertexWithUVFromVector(tessellator, tile.cubeVertices[4], tile.size, 1, 1);
        this.addVertexWithUVFromVector(tessellator, tile.cubeVertices[7], tile.size, 0, 1);
        this.addVertexWithUVFromVector(tessellator, tile.cubeVertices[6], tile.size, 0, 0);

        //Bottom
        this.addVertexWithUVFromVector(tessellator, tile.cubeVertices[6], tile.size, 1, 0);
        this.addVertexWithUVFromVector(tessellator, tile.cubeVertices[7], tile.size, 1, 1);
        this.addVertexWithUVFromVector(tessellator, tile.cubeVertices[3], tile.size, 0, 1);
        this.addVertexWithUVFromVector(tessellator, tile.cubeVertices[2], tile.size, 0, 0);

        //Top
        this.addVertexWithUVFromVector(tessellator, tile.cubeVertices[4], tile.size, 1, 0);
        this.addVertexWithUVFromVector(tessellator, tile.cubeVertices[5], tile.size, 1, 1);
        this.addVertexWithUVFromVector(tessellator, tile.cubeVertices[1], tile.size, 0, 1);
        this.addVertexWithUVFromVector(tessellator, tile.cubeVertices[0], tile.size, 0, 0);

        //Left
        this.addVertexWithUVFromVector(tessellator, tile.cubeVertices[4], tile.size, 1, 0);
        this.addVertexWithUVFromVector(tessellator, tile.cubeVertices[0], tile.size, 1, 1);
        this.addVertexWithUVFromVector(tessellator, tile.cubeVertices[3], tile.size, 0, 1);
        this.addVertexWithUVFromVector(tessellator, tile.cubeVertices[7], tile.size, 0, 0);

        //Right
        this.addVertexWithUVFromVector(tessellator, tile.cubeVertices[1], tile.size, 1, 0);
        this.addVertexWithUVFromVector(tessellator, tile.cubeVertices[5], tile.size, 1, 1);
        this.addVertexWithUVFromVector(tessellator, tile.cubeVertices[6], tile.size, 0, 1);
        this.addVertexWithUVFromVector(tessellator, tile.cubeVertices[2], tile.size, 0, 0);

        tessellator.draw();

        GL11.glEnable(GL11.GL_LIGHTING);

        GL11.glPopMatrix();
    }
}
