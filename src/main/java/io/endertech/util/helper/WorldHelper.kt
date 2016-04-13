package io.endertech.util.helper

import net.minecraft.entity.item.EntityItem
import net.minecraft.item.ItemStack
import net.minecraft.world.World

object WorldHelper {
    fun spawnItemInWorldWithRandomness(item: ItemStack, world: World, blockOffset: Float, x: Int, y: Int, z: Int, pickupDelay: Int) {
        val d1 = world.rand.nextFloat() * blockOffset + (1.0f - blockOffset) * 0.5
        val d2 = world.rand.nextFloat() * blockOffset + (1.0f - blockOffset) * 0.5
        val d3 = world.rand.nextFloat() * blockOffset + (1.0f - blockOffset) * 0.5
        val localEntityItem = EntityItem(world, x + d1, y + d2, z + d3, item)
        localEntityItem.delayBeforeCanPickup = pickupDelay
        world.spawnEntityInWorld(localEntityItem)
    }
}
