package io.endertech.util.helper

import io.endertech.util.BlockCoord
import io.endertech.util.RGBA
import net.minecraft.block.Block
import net.minecraft.client.renderer.OpenGlHelper
import net.minecraft.client.renderer.RenderGlobal
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.AxisAlignedBB
import org.lwjgl.opengl.GL11

object RenderHelper {
    // Similar to vanilla's "drawSelectionBox" with some customisability and without block checks
    fun renderBlockOutline(context: RenderGlobal, entityPlayer: EntityPlayer, blockCoord: BlockCoord, colour: RGBA, lineWidth: Float, partialTicks: Float) {
        val block = entityPlayer.worldObj.getBlock(blockCoord.x, blockCoord.y, blockCoord.z)
        block.setBlockBoundsBasedOnState(entityPlayer.worldObj, blockCoord.x, blockCoord.y, blockCoord.z)
        renderAABBOutline(context, entityPlayer, block.getSelectedBoundingBoxFromPool(entityPlayer.worldObj, blockCoord.x, blockCoord.y, blockCoord.z), colour, lineWidth, partialTicks)
    }

    fun renderAABBOutline(context: RenderGlobal, entityPlayer: EntityPlayer, aabb: AxisAlignedBB, colour: RGBA, lineWidth: Float, partialTicks: Float) {
        GL11.glEnable(GL11.GL_BLEND)
        OpenGlHelper.glBlendFunc(770, 771, 1, 0)
        GL11.glColor4f(colour.red, colour.green, colour.blue, colour.alpha)
        GL11.glLineWidth(lineWidth)
        GL11.glDisable(GL11.GL_TEXTURE_2D)
        GL11.glDepthMask(false)
        val f1 = 0.002f

        val d0 = entityPlayer.lastTickPosX + (entityPlayer.posX - entityPlayer.lastTickPosX) * partialTicks.toDouble()
        val d1 = entityPlayer.lastTickPosY + (entityPlayer.posY - entityPlayer.lastTickPosY) * partialTicks.toDouble()
        val d2 = entityPlayer.lastTickPosZ + (entityPlayer.posZ - entityPlayer.lastTickPosZ) * partialTicks.toDouble()
        context.drawOutlinedBoundingBox(aabb.expand(f1.toDouble(), f1.toDouble(), f1.toDouble()).getOffsetBoundingBox(-d0, -d1, -d2), -1)

        GL11.glDepthMask(true)
        GL11.glEnable(GL11.GL_TEXTURE_2D)
        GL11.glDisable(GL11.GL_BLEND)
    }
}
