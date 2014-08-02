package io.endertech.util;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class WorldHelper
{
    public static void spawnItemInWorldWithRandomness(ItemStack item, World world, float blockOffset, int x, int y, int z, int pickupDelay)
    {
        double d1 = world.rand.nextFloat() * blockOffset + (1.0F - blockOffset) * 0.5D;
        double d2 = world.rand.nextFloat() * blockOffset + (1.0F - blockOffset) * 0.5D;
        double d3 = world.rand.nextFloat() * blockOffset + (1.0F - blockOffset) * 0.5D;
        EntityItem localEntityItem = new EntityItem(world, x + d1, y + d2, z + d3, item);
        localEntityItem.delayBeforeCanPickup = pickupDelay;
        world.spawnEntityInWorld(localEntityItem);
    }
}
