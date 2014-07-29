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


        for (int i = 0; i < 6; i++)
        {
            IIcon sideIcon = block.getIcon(i, meta);
            this.renderSideFace(renderer, block, sideIcon, (double) tile.xCoord, (double) tile.yCoord, (double) tile.zCoord, i);
        }

        TileTankController tank = (TileTankController) tile;

        ControllerTank controller = tank.getTankController();
        if (controller == null) return;

        BlockCoord min = controller.getMinimumCoord();
        BlockCoord max = controller.getMaximumCoord();

        if (controller != null && controller.isAssembled() && min != null && controller.tank.getFluid() != null)
        {
            GL11.glPushMatrix();

            BlockCoord rMin = new BlockCoord(tile.xCoord - min.x, tile.yCoord - min.y, tile.zCoord - min.z);
            GL11.glTranslated(x + 0.5 - rMin.x + 1, y + 0.5 - rMin.y + 1, z + 0.5 - rMin.z + 1);

            double diff = controller.tank.getFluidAmount() - controller.lastTank.getFluidAmount();
            double ratio = (diff - controller.renderAddition) / diff;

            if (ratio <= 0.3) ratio = 0.3;
            if (ratio >= 0.8) ratio = 0.8;
            controller.renderAddition += diff * f * ratio * (1.0 / 10);

            if (diff < 0)
            {
                if (controller.renderAddition < diff) controller.renderAddition = (int) diff;
            } else
            {
                if (controller.renderAddition > diff) controller.renderAddition = (int) diff;
            }
            double amount = controller.lastTank.getFluidAmount() + controller.renderAddition;
            double capacity = controller.tank.getCapacity();

            int unitHeights = (max.y - min.y - 1);
            double capacityPerUnitHeight = capacity / unitHeights;
            double[] levelAmounts = new double[unitHeights];
            int stopLevel = 0;
            for (int level = 0; (level < unitHeights) && (stopLevel == 0); level++)
            {
                double levelAmount = capacityPerUnitHeight;
                amount -= capacityPerUnitHeight;

                if (amount <= 0)
                {
                    levelAmount += amount;
                    stopLevel = level + 1;
                }

                levelAmounts[level] = levelAmount;
            }

            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glColor3f(1, 1, 1);

            final Fluid fluid = controller.tank.getFluid().getFluid();
            final IIcon texture = fluid.getStillIcon();
            final int colour = fluid.getColor(new FluidStack(fluid, 1));

            bindTexture(getFluidSheet(fluid));

            if (stopLevel != 0)
            {
                for (int level = 0; level < stopLevel; level++)
                {
                    if (levelAmounts[level] > 0)
                    {
                        double height = levelAmounts[level] / capacityPerUnitHeight;
                        if (height > 0 && height < 0.02) height = 0.02;

                        renderFluidBlocks(height, colour, texture, (max.x - min.x - 1), level, stopLevel, (max.z - min.z - 1));
                    }
                }
            }

            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_BLEND);

            GL11.glPopMatrix();
        }
    }

    public void renderFluidBlocks(double height, int colour, IIcon texture, int repx, int yoffset, int maxyoffset, int repz)
    {
        Tessellator t = Tessellator.instance;

        final double ySouthEast = height;
        final double yNorthEast = height;
        final double ySouthWest = height;
        final double yNorthWest = height;

        final double uMin = texture.getInterpolatedU(0.0);
        final double uMax = texture.getInterpolatedU(16.0);
        final double vMin = texture.getInterpolatedV(0.0);
        final double vMax = texture.getInterpolatedV(16.0);

        final double vHeight = vMax - vMin;

        final float r = (colour >> 16 & 0xFF) / 255.0F;
        final float g = (colour >> 8 & 0xFF) / 255.0F;
        final float b = (colour & 0xFF) / 255.0F;

        for (int rx = 0; rx < repx; rx++)
        {
            int ry = yoffset;
            for (int rz = 0; rz < repz; rz++)
            {

                t.startDrawingQuads();
                t.setColorOpaque_F(r, g, b);

                // north side
                if (rz == 0)
                {
                    t.addVertexWithUV(0.5 + rx, -0.5 + ry, -0.5, uMax, vMin); // bottom
                    t.addVertexWithUV(-0.5 + rx, -0.5 + ry, -0.5, uMin, vMin); // bottom
                    // top north/west
                    t.addVertexWithUV(-0.5 + rx, -0.5 + yNorthWest + ry, -0.5 + rz, uMin, vMin + (vHeight * yNorthWest));
                    // top north/east
                    t.addVertexWithUV(0.5 + rx, -0.5 + yNorthEast + ry, -0.5 + rz, uMax, vMin + (vHeight * yNorthEast));
                }

                // south side
                if (rz == repz - 1)
                {
                    t.addVertexWithUV(0.5 + rx, -0.5 + ry, 0.5 + rz, uMin, vMin);
                    // top south east
                    t.addVertexWithUV(0.5 + rx, -0.5 + ySouthEast + ry, 0.5 + rz, uMin, vMin + (vHeight * ySouthEast));
                    // top south west
                    t.addVertexWithUV(-0.5 + rx, -0.5 + ySouthWest + ry, 0.5 + rz, uMax, vMin + (vHeight * ySouthWest));
                    t.addVertexWithUV(-0.5 + rx, -0.5 + ry, 0.5 + rz, uMax, vMin);
                }

                // east side
                if (rx == repx - 1)
                {
                    t.addVertexWithUV(0.5 + rx, -0.5 + ry, -0.5 + rz, uMin, vMin);
                    // top north/east
                    t.addVertexWithUV(0.5 + rx, -0.5 + yNorthEast + ry, -0.5 + rz, uMin, vMin + (vHeight * yNorthEast));
                    // top south/east
                    t.addVertexWithUV(0.5 + rx, -0.5 + ySouthEast + ry, 0.5 + rz, uMax, vMin + (vHeight * ySouthEast));
                    t.addVertexWithUV(0.5 + rx, -0.5 + ry, 0.5 + rz, uMax, vMin);
                }

                // west side
                if (rx == 0)
                {
                    t.addVertexWithUV(-0.5 + rx, -0.5 + ry, 0.5 + rz, uMin, vMin);
                    // top south/west
                    t.addVertexWithUV(-0.5 + rx, -0.5 + ySouthWest + ry, 0.5 + rz, uMin, vMin + (vHeight * ySouthWest));
                    // top north/west
                    t.addVertexWithUV(-0.5 + rx, -0.5 + yNorthWest + ry, -0.5 + rz, uMax, vMin + (vHeight * yNorthWest));
                    t.addVertexWithUV(-0.5 + rx, -0.5 + ry, -0.5 + rz, uMax, vMin);
                }
                // top
                // south east
                if (ry == maxyoffset - 1)
                {
                    t.addVertexWithUV(0.5 + rx, -0.5 + ySouthEast + ry, 0.5 + rz, uMax, vMin);
                    // north east
                    t.addVertexWithUV(0.5 + rx, -0.5 + yNorthEast + ry, -0.5 + rz, uMin, vMin);
                    // north west
                    t.addVertexWithUV(-0.5 + rx, -0.5 + yNorthWest + ry, -0.5 + rz, uMin, vMax);
                    // south west
                    t.addVertexWithUV(-0.5 + rx, -0.5 + ySouthWest + ry, 0.5 + rz, uMax, vMax);
                }

                if (ry == 0)
                {
                    // bottom
                    t.addVertexWithUV(0.5 + rx, -0.5 + ry, -0.5 + rz, uMax, vMin);
                    t.addVertexWithUV(0.5 + rx, -0.5 + ry, 0.5 + rz, uMin, vMin);
                    t.addVertexWithUV(-0.5 + rx, -0.5 + ry, 0.5 + rz, uMin, vMax);
                    t.addVertexWithUV(-0.5 + rx, -0.5 + ry, -0.5 + rz, uMax, vMax);
                }

                t.draw();
            }

        }
    }
}
