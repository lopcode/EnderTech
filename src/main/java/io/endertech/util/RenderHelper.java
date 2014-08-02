package io.endertech.util;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import org.lwjgl.opengl.GL11;

public class RenderHelper
{
    // Similar to vanilla's "drawSelectionBox" with some customisability and without block checks
    public static void renderBlockOutline(RenderGlobal context, EntityPlayer entityPlayer, BlockCoord blockCoord, RGBA colour, float lineWidth, float partialTicks)
    {
        Block block = entityPlayer.worldObj.getBlock(blockCoord.x, blockCoord.y, blockCoord.z);
        block.setBlockBoundsBasedOnState(entityPlayer.worldObj, blockCoord.x, blockCoord.y, blockCoord.z);
        renderAABBOutline(context, entityPlayer, block.getSelectedBoundingBoxFromPool(entityPlayer.worldObj, blockCoord.x, blockCoord.y, blockCoord.z), colour, lineWidth, partialTicks);
    }

    public static void renderAABBOutline(RenderGlobal context, EntityPlayer entityPlayer, AxisAlignedBB aabb, RGBA colour, float lineWidth, float partialTicks)
    {
        GL11.glEnable(GL11.GL_BLEND);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glColor4f(colour.red, colour.green, colour.blue, colour.alpha);
        GL11.glLineWidth(lineWidth);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDepthMask(false);
        float f1 = 0.002F;

        double d0 = entityPlayer.lastTickPosX + (entityPlayer.posX - entityPlayer.lastTickPosX) * (double) partialTicks;
        double d1 = entityPlayer.lastTickPosY + (entityPlayer.posY - entityPlayer.lastTickPosY) * (double) partialTicks;
        double d2 = entityPlayer.lastTickPosZ + (entityPlayer.posZ - entityPlayer.lastTickPosZ) * (double) partialTicks;
        context.drawOutlinedBoundingBox(aabb.expand((double) f1, (double) f1, (double) f1).getOffsetBoundingBox(-d0, -d1, -d2), -1);

        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
    }
}
