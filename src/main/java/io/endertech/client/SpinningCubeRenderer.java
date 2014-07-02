package io.endertech.client;

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
    private float size = 0.5f;
    private float x = 0.5f;
    private float y = 0.5f;
    private float z = 0.5f;

    @Override
    public void renderTileEntityAt(TileEntity tTile, double x, double y, double z, float partialTick)
    {
        //LogHelper.info(partialTick);
        TileSpinningCube tile = (TileSpinningCube) tTile;

        float xAngleRadians = (float) (tile.speed + tile.randomAddition.xCoord) * partialTick;
        float yAngleRadians = (float) (tile.speed + tile.randomAddition.yCoord) * partialTick;
        float zAngleRadians = (float) (tile.speed + tile.randomAddition.zCoord) * partialTick;

        tile.yAddition += tile.speed * 3;
        if (tile.yAddition >= (2.0 * Math.PI)) tile.yAddition = tile.yAddition - (2.0 * Math.PI);

        for (Vec3 cubeVertex : tile.cubeVertices)
        {
            cubeVertex.rotateAroundX(xAngleRadians);
            cubeVertex.rotateAroundY(yAngleRadians);
            cubeVertex.rotateAroundZ(zAngleRadians);
        }

        this.bindTexture(emerald_block_texture);

        Tessellator tessellator = Tessellator.instance;
        GL11.glPushMatrix();

        GL11.glDisable(GL11.GL_LIGHTING);

        GL11.glTranslated(x, y + 0.5 + ((Math.sin(tile.yAddition) + 1) / 3.0), z);

        tessellator.startDrawingQuads();

        //Front
        tessellator.addVertexWithUV(tile.cubeVertices[0].xCoord * size + this.x, tile.cubeVertices[0].yCoord * size + this.y, tile.cubeVertices[0].zCoord * size + this.z, 1, 0);
        tessellator.addVertexWithUV(tile.cubeVertices[1].xCoord * size + this.x, tile.cubeVertices[1].yCoord * size + this.y, tile.cubeVertices[1].zCoord * size + this.z, 1, 1);
        tessellator.addVertexWithUV(tile.cubeVertices[2].xCoord * size + this.x, tile.cubeVertices[2].yCoord * size + this.y, tile.cubeVertices[2].zCoord * size + this.z, 0, 1);
        tessellator.addVertexWithUV(tile.cubeVertices[3].xCoord * size + this.x, tile.cubeVertices[3].yCoord * size + this.y, tile.cubeVertices[3].zCoord * size + this.z, 0, 0);

        //Back
        tessellator.addVertexWithUV(tile.cubeVertices[5].xCoord * size + this.x, tile.cubeVertices[5].yCoord * size + this.y, tile.cubeVertices[5].zCoord * size + this.z, 1, 0);
        tessellator.addVertexWithUV(tile.cubeVertices[4].xCoord * size + this.x, tile.cubeVertices[4].yCoord * size + this.y, tile.cubeVertices[4].zCoord * size + this.z, 1, 1);
        tessellator.addVertexWithUV(tile.cubeVertices[7].xCoord * size + this.x, tile.cubeVertices[7].yCoord * size + this.y, tile.cubeVertices[7].zCoord * size + this.z, 0, 1);
        tessellator.addVertexWithUV(tile.cubeVertices[6].xCoord * size + this.x, tile.cubeVertices[6].yCoord * size + this.y, tile.cubeVertices[6].zCoord * size + this.z, 0, 0);

        //Bottom
        tessellator.addVertexWithUV(tile.cubeVertices[6].xCoord * size + this.x, tile.cubeVertices[6].yCoord * size + this.y, tile.cubeVertices[6].zCoord * size + this.z, 1, 0);
        tessellator.addVertexWithUV(tile.cubeVertices[7].xCoord * size + this.x, tile.cubeVertices[7].yCoord * size + this.y, tile.cubeVertices[7].zCoord * size + this.z, 1, 1);
        tessellator.addVertexWithUV(tile.cubeVertices[3].xCoord * size + this.x, tile.cubeVertices[3].yCoord * size + this.y, tile.cubeVertices[3].zCoord * size + this.z, 0, 1);
        tessellator.addVertexWithUV(tile.cubeVertices[2].xCoord * size + this.x, tile.cubeVertices[2].yCoord * size + this.y, tile.cubeVertices[2].zCoord * size + this.z, 0, 0);

        //Top
        tessellator.addVertexWithUV(tile.cubeVertices[4].xCoord * size + this.x, tile.cubeVertices[4].yCoord * size + this.y, tile.cubeVertices[4].zCoord * size + this.z, 1, 0);
        tessellator.addVertexWithUV(tile.cubeVertices[5].xCoord * size + this.x, tile.cubeVertices[5].yCoord * size + this.y, tile.cubeVertices[5].zCoord * size + this.z, 1, 1);
        tessellator.addVertexWithUV(tile.cubeVertices[1].xCoord * size + this.x, tile.cubeVertices[1].yCoord * size + this.y, tile.cubeVertices[1].zCoord * size + this.z, 0, 1);
        tessellator.addVertexWithUV(tile.cubeVertices[0].xCoord * size + this.x, tile.cubeVertices[0].yCoord * size + this.y, tile.cubeVertices[0].zCoord * size + this.z, 0, 0);

        //Left
        tessellator.addVertexWithUV(tile.cubeVertices[4].xCoord * size + this.x, tile.cubeVertices[4].yCoord * size + this.y, tile.cubeVertices[4].zCoord * size + this.z, 1, 0);
        tessellator.addVertexWithUV(tile.cubeVertices[0].xCoord * size + this.x, tile.cubeVertices[0].yCoord * size + this.y, tile.cubeVertices[0].zCoord * size + this.z, 1, 1);
        tessellator.addVertexWithUV(tile.cubeVertices[3].xCoord * size + this.x, tile.cubeVertices[3].yCoord * size + this.y, tile.cubeVertices[3].zCoord * size + this.z, 0, 1);
        tessellator.addVertexWithUV(tile.cubeVertices[7].xCoord * size + this.x, tile.cubeVertices[7].yCoord * size + this.y, tile.cubeVertices[7].zCoord * size + this.z, 0, 0);

        //Right
        tessellator.addVertexWithUV(tile.cubeVertices[1].xCoord * size + this.x, tile.cubeVertices[1].yCoord * size + this.y, tile.cubeVertices[1].zCoord * size + this.z, 1, 0);
        tessellator.addVertexWithUV(tile.cubeVertices[5].xCoord * size + this.x, tile.cubeVertices[5].yCoord * size + this.y, tile.cubeVertices[5].zCoord * size + this.z, 1, 1);
        tessellator.addVertexWithUV(tile.cubeVertices[6].xCoord * size + this.x, tile.cubeVertices[6].yCoord * size + this.y, tile.cubeVertices[6].zCoord * size + this.z, 0, 1);
        tessellator.addVertexWithUV(tile.cubeVertices[2].xCoord * size + this.x, tile.cubeVertices[2].yCoord * size + this.y, tile.cubeVertices[2].zCoord * size + this.z, 0, 0);

        tessellator.draw();

        GL11.glEnable(GL11.GL_LIGHTING);

        GL11.glPopMatrix();
    }
}
