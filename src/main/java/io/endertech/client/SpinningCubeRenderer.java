package io.endertech.client;

import io.endertech.util.Geometry;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;

public class SpinningCubeRenderer extends TileEntitySpecialRenderer
{
    private ResourceLocation emerald_block_texture = new ResourceLocation("textures/blocks/emerald_block.png");
    private float size = 0.5f;
    private double rx = 10.0;
    private double ry = 10.0;
    private double rz = 10.0;
    private Vec3[] cubeVertices = Geometry.cubeVertices.clone();
    private float x = 0.5f;
    private float y = 0.5f;
    private float z = 0.5f;

    @Override
    public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float f)
    {
        for (Vec3 cubeVertex : cubeVertices)
        {
            cubeVertex.rotateAroundX((float) (Math.PI * rx));
            cubeVertex.rotateAroundY((float) (Math.PI * ry));
            cubeVertex.rotateAroundZ((float) (Math.PI * rz));
        }

        this.bindTexture(emerald_block_texture);

        Tessellator tessellator = Tessellator.instance;
        GL11.glPushMatrix();
        GL11.glTranslated(x, y, z);

        tessellator.startDrawingQuads();

        //Front
        tessellator.addVertexWithUV(cubeVertices[0].xCoord * size + this.x, cubeVertices[0].yCoord * size + this.y, cubeVertices[0].zCoord * size + this.z, 1, 0);
        tessellator.addVertexWithUV(cubeVertices[1].xCoord * size + this.x, cubeVertices[1].yCoord * size + this.y, cubeVertices[1].zCoord * size + this.z, 1, 1);
        tessellator.addVertexWithUV(cubeVertices[2].xCoord * size + this.x, cubeVertices[2].yCoord * size + this.y, cubeVertices[2].zCoord * size + this.z, 0, 1);
        tessellator.addVertexWithUV(cubeVertices[3].xCoord * size + this.x, cubeVertices[3].yCoord * size + this.y, cubeVertices[3].zCoord * size + this.z, 0, 0);

        //Back
        tessellator.addVertexWithUV(cubeVertices[5].xCoord * size + this.x, cubeVertices[5].yCoord * size + this.y, cubeVertices[5].zCoord * size + this.z, 1, 0);
        tessellator.addVertexWithUV(cubeVertices[4].xCoord * size + this.x, cubeVertices[4].yCoord * size + this.y, cubeVertices[4].zCoord * size + this.z, 1, 1);
        tessellator.addVertexWithUV(cubeVertices[7].xCoord * size + this.x, cubeVertices[7].yCoord * size + this.y, cubeVertices[7].zCoord * size + this.z, 0, 1);
        tessellator.addVertexWithUV(cubeVertices[6].xCoord * size + this.x, cubeVertices[6].yCoord * size + this.y, cubeVertices[6].zCoord * size + this.z, 0, 0);

        //Bottom
        tessellator.addVertexWithUV(cubeVertices[6].xCoord * size + this.x, cubeVertices[6].yCoord * size + this.y, cubeVertices[6].zCoord * size + this.z, 1, 0);
        tessellator.addVertexWithUV(cubeVertices[7].xCoord * size + this.x, cubeVertices[7].yCoord * size + this.y, cubeVertices[7].zCoord * size + this.z, 1, 1);
        tessellator.addVertexWithUV(cubeVertices[3].xCoord * size + this.x, cubeVertices[3].yCoord * size + this.y, cubeVertices[3].zCoord * size + this.z, 0, 1);
        tessellator.addVertexWithUV(cubeVertices[2].xCoord * size + this.x, cubeVertices[2].yCoord * size + this.y, cubeVertices[2].zCoord * size + this.z, 0, 0);

        //Top
        tessellator.addVertexWithUV(cubeVertices[4].xCoord * size + this.x, cubeVertices[4].yCoord * size + this.y, cubeVertices[4].zCoord * size + this.z, 1, 0);
        tessellator.addVertexWithUV(cubeVertices[5].xCoord * size + this.x, cubeVertices[5].yCoord * size + this.y, cubeVertices[5].zCoord * size + this.z, 1, 1);
        tessellator.addVertexWithUV(cubeVertices[1].xCoord * size + this.x, cubeVertices[1].yCoord * size + this.y, cubeVertices[1].zCoord * size + this.z, 0, 1);
        tessellator.addVertexWithUV(cubeVertices[0].xCoord * size + this.x, cubeVertices[0].yCoord * size + this.y, cubeVertices[0].zCoord * size + this.z, 0, 0);

        //Left
        tessellator.addVertexWithUV(cubeVertices[4].xCoord * size + this.x, cubeVertices[4].yCoord * size + this.y, cubeVertices[4].zCoord * size + this.z, 1, 0);
        tessellator.addVertexWithUV(cubeVertices[0].xCoord * size + this.x, cubeVertices[0].yCoord * size + this.y, cubeVertices[0].zCoord * size + this.z, 1, 1);
        tessellator.addVertexWithUV(cubeVertices[3].xCoord * size + this.x, cubeVertices[3].yCoord * size + this.y, cubeVertices[3].zCoord * size + this.z, 0, 1);
        tessellator.addVertexWithUV(cubeVertices[7].xCoord * size + this.x, cubeVertices[7].yCoord * size + this.y, cubeVertices[7].zCoord * size + this.z, 0, 0);

        //Right
        tessellator.addVertexWithUV(cubeVertices[1].xCoord * size + this.x, cubeVertices[1].yCoord * size + this.y, cubeVertices[1].zCoord * size + this.z, 1, 0);
        tessellator.addVertexWithUV(cubeVertices[5].xCoord * size + this.x, cubeVertices[5].yCoord * size + this.y, cubeVertices[5].zCoord * size + this.z, 1, 1);
        tessellator.addVertexWithUV(cubeVertices[6].xCoord * size + this.x, cubeVertices[6].yCoord * size + this.y, cubeVertices[6].zCoord * size + this.z, 0, 1);
        tessellator.addVertexWithUV(cubeVertices[2].xCoord * size + this.x, cubeVertices[2].yCoord * size + this.y, cubeVertices[2].zCoord * size + this.z, 0, 0);

        tessellator.draw();
        GL11.glPopMatrix();
    }
}
