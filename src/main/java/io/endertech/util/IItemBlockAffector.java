package io.endertech.util;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import java.util.Set;

public interface IItemBlockAffector
{
    public Set<BlockCoord> blocksAffected(ItemStack item, World world, BlockCoord origin);
}
