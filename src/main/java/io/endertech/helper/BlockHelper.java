package io.endertech.helper;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.fluids.BlockFluidBase;
import java.util.HashSet;
import java.util.Set;

public class BlockHelper
{
    public static final Set<Block> softBlocks = new HashSet<Block>();

    public static void initSoftBlocks()
    {
        for (Object o : Block.blockRegistry)
        {
            Block block = (Block) o;

            if (block instanceof BlockFluidBase || block instanceof BlockLiquid || block instanceof IPlantable)
            {
                softBlocks.add(block);
            }
        }

        softBlocks.add(Blocks.snow);
        softBlocks.add(Blocks.vine);
        softBlocks.add(Blocks.fire);
    }

    public static boolean isSoftBlock(World world, int x, int y, int z)
    {
        Block block = world.getBlock(x, y, z);
        if (block == Blocks.air)
        {
            return true;
        }

        return isSoftBlock(block, world, x, y, z);
    }

    public static boolean isSoftBlock(Block block, World world, int x, int y, int z)
    {
        return block == null || softBlocks.contains(block) || world.isAirBlock(x, y, z);
    }

    public static boolean isBlockExposed(World world, int x, int y, int z)
    {
        return isSoftBlock(world, x + 1, y, z) || isSoftBlock(world, x - 1, y, z) || isSoftBlock(world, x, y + 1, z) || isSoftBlock(world, x, y - 1, z) || isSoftBlock(world, x, y, z + 1) || isSoftBlock(world, x, y, z - 1);
    }
}
