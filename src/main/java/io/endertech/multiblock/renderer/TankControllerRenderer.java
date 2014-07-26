package io.endertech.multiblock.renderer;

import io.endertech.multiblock.block.BlockTankController;
import io.endertech.multiblock.controller.ControllerTank;
import io.endertech.multiblock.tile.TileTankController;
import io.endertech.util.BlockCoord;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import org.lwjgl.opengl.GL11;

public class TankControllerRenderer extends TileEntitySpecialRenderer
{
    RenderBlocks renderer = new RenderBlocks();

    public static ResourceLocation getFluidSheet(FluidStack liquid)
    {
        if (liquid == null) return TextureMap.locationBlocksTexture;
        return getFluidSheet(liquid.getFluid());
    }

    /**
     * @param liquid
     */
    public static ResourceLocation getFluidSheet(Fluid liquid)
    {
        return TextureMap.locationBlocksTexture;
    }

    public void renderSideFace(RenderBlocks renderer, Block block, IIcon icon, double x, double y, double z, int side)
    {
        switch (side)
        {
            case 0:
                renderer.renderFaceYNeg(block, x, y, z, icon);
                break;
            case 1:
                renderer.renderFaceYPos(block, x, y, z, icon);
                break;
            case 2:
                renderer.renderFaceZNeg(block, x, y, z, icon);
                break;
            case 3:
                renderer.renderFaceZPos(block, x, y, z, icon);
                break;
            case 4:
                renderer.renderFaceXNeg(block, x, y, z, icon);
                break;
            case 5:
                renderer.renderFaceXPos(block, x, y, z, icon);
        }

    }

    @Override
    public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float f)
    {
        if (!(tile instanceof TileTankController)) return;

        BlockTankController block = (BlockTankController) tile.getBlockType();
        int meta = tile.getBlockMetadata();

        renderer.blockAccess = tile.getWorldObj();
        renderer.setRenderBounds(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
        renderer.renderAllFaces = true;
        renderer.renderStandardBlock(block, tile.xCoord, tile.yCoord, tile.zCoord);


        //        for (int i = 0; i < 6; i++)
        //        {
        //            IIcon sideIcon = block.getIcon(i, meta);
        //            this.renderSideFace(renderer, block, sideIcon, (double)tile.xCoord, (double)tile.yCoord, (double)tile.zCoord, i);
        //        }

        TileTankController tank = (TileTankController) tile;

        ControllerTank controller = tank.getTankController();
        BlockCoord min = controller.getMinimumCoord();
        BlockCoord max = controller.getMaximumCoord();

        if (controller != null && controller.isAssembled() && min != null)
        {
            BlockCoord rMin = new BlockCoord(tile.xCoord - min.x, tile.yCoord - min.y, tile.zCoord - min.z);
            GL11.glPushMatrix();
            GL11.glTranslated(x + 0.5 - rMin.x + 1, y + 0.5 - rMin.y + 1, z + 0.5 - rMin.z + 1);


            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glColor3f(1, 1, 1);

            final Fluid fluid = FluidRegistry.WATER;
            IIcon texture = fluid.getStillIcon();
            final int color = fluid.getColor(new FluidStack(fluid, 1));

            bindTexture(getFluidSheet(fluid));

            Tessellator t = Tessellator.instance;

            double fillPercent = (double) controller.tank.getFluidAmount() / controller.tank.getCapacity();
            double fillHeight = max.y - min.y - 1;
            final double ySouthEast = fillPercent;
            final double yNorthEast = fillPercent;
            final double ySouthWest = fillPercent;
            final double yNorthWest = fillPercent;

            final double uMin = texture.getInterpolatedU(0.0);
            final double uMax = texture.getInterpolatedU(16.0);
            final double vMin = texture.getInterpolatedV(0.0);
            final double vMax = texture.getInterpolatedV(16.0);

            final double vHeight = vMax - vMin;

            final float r = (color >> 16 & 0xFF) / 255.0F;
            final float g = (color >> 8 & 0xFF) / 255.0F;
            final float b = (color & 0xFF) / 255.0F;

            // north side
            t.startDrawingQuads();
            t.setColorOpaque_F(r, g, b);
            t.addVertexWithUV(0.5, -0.5, -0.5, uMax, vMin); // bottom
            t.addVertexWithUV(-0.5, -0.5, -0.5, uMin, vMin); // bottom
            // top north/west
            t.addVertexWithUV(-0.5, -0.5 + yNorthWest, -0.5, uMin, vMin + (vHeight * yNorthWest));
            // top north/east
            t.addVertexWithUV(0.5, -0.5 + yNorthEast, -0.5, uMax, vMin + (vHeight * yNorthEast));

            // south side
            t.addVertexWithUV(0.5, -0.5, 0.5, uMin, vMin);
            // top south east
            t.addVertexWithUV(0.5, -0.5 + ySouthEast, 0.5, uMin, vMin + (vHeight * ySouthEast));
            // top south west
            t.addVertexWithUV(-0.5, -0.5 + ySouthWest, 0.5, uMax, vMin + (vHeight * ySouthWest));
            t.addVertexWithUV(-0.5, -0.5, 0.5, uMax, vMin);

            // east side
            t.addVertexWithUV(0.5, -0.5, -0.5, uMin, vMin);
            // top north/east
            t.addVertexWithUV(0.5, -0.5 + yNorthEast, -0.5, uMin, vMin + (vHeight * yNorthEast));
            // top south/east
            t.addVertexWithUV(0.5, -0.5 + ySouthEast, 0.5, uMax, vMin + (vHeight * ySouthEast));
            t.addVertexWithUV(0.5, -0.5, 0.5, uMax, vMin);

            // west side
            t.addVertexWithUV(-0.5, -0.5, 0.5, uMin, vMin);
            // top south/west
            t.addVertexWithUV(-0.5, -0.5 + ySouthWest, 0.5, uMin, vMin + (vHeight * ySouthWest));
            // top north/west
            t.addVertexWithUV(-0.5, -0.5 + yNorthWest, -0.5, uMax, vMin + (vHeight * yNorthWest));
            t.addVertexWithUV(-0.5, -0.5, -0.5, uMax, vMin);

            // top
            // south east

            t.addVertexWithUV(0.5, -0.5 + ySouthEast, 0.5, uMax, vMin);
            // north east
            t.addVertexWithUV(0.5, -0.5 + yNorthEast, -0.5, uMin, vMin);
            // north west
            t.addVertexWithUV(-0.5, -0.5 + yNorthWest, -0.5, uMin, vMax);
            // south west
            t.addVertexWithUV(-0.5, -0.5 + ySouthWest, 0.5, uMax, vMax);

            // bottom
            t.addVertexWithUV(0.5, -0.5, -0.5, uMax, vMin);
            t.addVertexWithUV(0.5, -0.5, 0.5, uMin, vMin);
            t.addVertexWithUV(-0.5, -0.5, 0.5, uMin, vMax);
            t.addVertexWithUV(-0.5, -0.5, -0.5, uMax, vMax);
            t.draw();

            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_BLEND);

            //}

            GL11.glPopMatrix();
        }
    }
}
