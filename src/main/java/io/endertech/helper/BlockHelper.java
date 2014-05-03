package io.endertech.helper;

import net.minecraft.block.Block;
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
        for (Object o : Block.blocksList)
        {
            Block block = (Block) o;

            if (block instanceof BlockFluidBase || block instanceof IPlantable)
            {
                softBlocks.add(block);
            }
        }

        softBlocks.add(Block.snow);
        softBlocks.add(Block.vine);
        softBlocks.add(Block.fire);
    }

    public static boolean isSoftBlock(World world, int x, int y, int z)
    {
        int blockId = world.getBlockId(x, y, z);
        if (blockId <= 0)
            return true;

        return isSoftBlock(Block.blocksList[blockId], world, x, y, z);
    }

    public static boolean isSoftBlock(Block block, World world, int x, int y, int z)
    {
        return block == null || softBlocks.contains(block) || block.isAirBlock(world, x, y, z);
    }

    public static boolean isBlockExposed(World world, int x, int y, int z)
    {
        if (isSoftBlock(world, x + 1, y, z) || isSoftBlock(world, x - 1, y, z)
                || isSoftBlock(world, x, y + 1, z) || isSoftBlock(world, x, y - 1, z)
                || isSoftBlock(world, x, y, z + 1) || isSoftBlock(world, x, y, z - 1))
            return true;
        else
            return false;
    }
}
