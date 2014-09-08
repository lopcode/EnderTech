package io.endertech.multiblock.renderer;

import io.endertech.block.ETBlocks;
import io.endertech.config.GeneralConfig;
import io.endertech.multiblock.block.BlockTankController;
import io.endertech.multiblock.controller.ControllerTank;
import io.endertech.multiblock.tile.TileTankController;
import io.endertech.util.BlockCoord;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import org.lwjgl.opengl.GL11;

public class TankControllerRenderer extends TileEntitySpecialRenderer implements IItemRenderer
{
    private RenderBlocks renderer = new RenderBlocks();

    public static ResourceLocation getFluidSheet(Fluid liquid)
    {
        return TextureMap.locationBlocksTexture;
    }

    public void renderControllerBlock(RenderBlocks renderer, BlockTankController block, int meta, ForgeDirection front, double translateX, double translateY, double translateZ)
    {
        Tessellator tessellator = Tessellator.instance;

        renderer.setRenderBoundsFromBlock(block);
        GL11.glTranslated(translateX, translateY, translateZ);
        tessellator.startDrawingQuads();

        IIcon texture = block.bottomIcon;
        tessellator.setNormal(0.0F, -1.0F, 0.0F);
        renderer.renderFaceYNeg(block, 0.0D, 0.0D, 0.0D, texture);

        texture = block.topIcon;
        tessellator.setNormal(0.0F, 1.0F, 0.0F);
        renderer.renderFaceYPos(block, 0.0D, 0.0D, 0.0D, texture);

        texture = block.sideIcon;
        tessellator.setNormal(0.0F, 0.0F, -1.0F);
        if (front == ForgeDirection.NORTH)
            renderer.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, block.getIcon(ForgeDirection.NORTH.ordinal(), meta));
        else renderer.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, texture);

        tessellator.setNormal(0.0F, 0.0F, 1.0F);
        if (front == ForgeDirection.SOUTH)
            renderer.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, block.getIcon(ForgeDirection.SOUTH.ordinal(), meta));
        else renderer.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, texture);

        tessellator.setNormal(-1.0F, 0.0F, 0.0F);
        if (front == ForgeDirection.WEST)
            renderer.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, block.getIcon(ForgeDirection.WEST.ordinal(), meta));
        else renderer.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, texture);

        tessellator.setNormal(1.0F, 0.0F, 0.0F);
        if (front == ForgeDirection.EAST)
            renderer.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, block.getIcon(ForgeDirection.EAST.ordinal(), meta));
        else renderer.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, texture);

        tessellator.draw();
    }

    @Override
    public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float f)
    {
        if (!(tile instanceof TileTankController)) return;

        TileTankController tank = (TileTankController) tile;
        BlockTankController block = (BlockTankController) tile.getBlockType();
        int meta = tile.getBlockMetadata();

        this.bindTexture(TextureMap.locationBlocksTexture);

        GL11.glPushMatrix();

        renderer.blockAccess = tile.getWorldObj();
        this.renderControllerBlock(renderer, block, meta, tank.getOrientation(), x, y, z);

        GL11.glPopMatrix();


        ControllerTank controller = tank.getTankController();
        if (controller == null) return;

        BlockCoord min = controller.getMinimumCoord();
        BlockCoord max = controller.getMaximumCoord();

        if (controller != null && controller.isAssembled() && min != null && controller.tank.getFluid() != null)
        {
            BlockCoord rMin = new BlockCoord(tile.xCoord - min.x, tile.yCoord - min.y, tile.zCoord - min.z);

            double diff = controller.tank.getFluidAmount() - controller.lastTank.getFluidAmount();
            double ratio = (diff - controller.renderAddition) / diff;

            if (ratio <= 0.3) ratio = 0.3;
            if (ratio >= 0.8) ratio = 0.8;

            if (controller.renderedOnce) controller.renderAddition += diff * f * ratio * (1.0 / 10);
            else
            {
                controller.renderAddition += diff;
                controller.renderedOnce = true;
            }

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

            boolean setOpacity = false;
            float opacity = 1;

            final Fluid fluid = controller.tank.getFluid().getFluid();
            final IIcon texture = fluid.getStillIcon();
            if (texture == null) return;

            GL11.glPushMatrix();

            GL11.glTranslated(x + 0.5 - rMin.x + 1, y + 0.5 - rMin.y + 1, z + 0.5 - rMin.z + 1);

            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);


            final int colour = fluid.getColor(new FluidStack(fluid, 1));
            bindTexture(getFluidSheet(fluid));

            if (fluid.getDensity() < 0)
            {
                if (GeneralConfig.gasTopToBottom)
                {
                    GL11.glTranslatef(0f, 1f, 0f);
                    GL11.glRotatef(180, 1.0f, 0.0f, 1.0f);
                } else
                {
                    for (int i = 0; i < unitHeights; i++)
                        levelAmounts[i] = capacityPerUnitHeight;

                    setOpacity = true;
                    stopLevel = unitHeights;
                    opacity = (float) ((controller.lastTank.getFluidAmount() + controller.renderAddition) / capacity);
                    if (opacity < 0.10F) opacity = 0.10F;
                }
            }

            if (stopLevel != 0)
            {
                for (int level = 0; level < stopLevel; level++)
                {
                    if (levelAmounts[level] > 0)
                    {
                        double height = levelAmounts[level] / capacityPerUnitHeight;
                        if (height > 0 && height < 0.02) height = 0.02;

                        renderFluidBlocks(height, colour, texture, (max.x - min.x - 1), level, stopLevel, (max.z - min.z - 1), setOpacity, opacity);
                    }
                }
            }

            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_BLEND);

            GL11.glPopMatrix();
        }
    }

    public void renderFluidBlocks(double height, int colour, IIcon texture, int repx, int yoffset, int maxyoffset, int repz, boolean setOpacity, float opacity)
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
                if (!setOpacity) t.setColorOpaque_F(r, g, b);
                else t.setColorRGBA_F(r, g, b, opacity);

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

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type)
    {
        return true;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper)
    {
        return true;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data)
    {
        float x = 0F, y = 0F, z = 0F;

        switch (type)
        {
            case ENTITY:
            {
                x = -0.5F;
                y = -0.25F;
                z = -0.5F;

                break;
            }
            case EQUIPPED:
            {
                x = 0F;
                y = 0F;
                z = 0F;

                break;
            }
            case EQUIPPED_FIRST_PERSON:
            {
                x = 0F;
                y = 0F;
                z = 0F;

                break;
            }
            case INVENTORY:
            {
                x = 0F;
                y = -0.1F;
                z = 0F;

                break;
            }
            default:
                return;
        }

        GL11.glPushMatrix();
        GL11.glTranslatef(x, y, z);
        renderer.setRenderBoundsFromBlock(ETBlocks.blockTankController);
        //renderer.blockAccess = RenderBlocks.getInstance().blockAccess;
        this.renderControllerBlock(renderer, (BlockTankController) ETBlocks.blockTankController, 0, ForgeDirection.EAST, 0, 0, 0);

        GL11.glPopMatrix();
    }
}
