package io.endertech.util.helper;

import io.endertech.util.BlockCoord;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockTorch;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.BlockFluidBase;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BlockHelper
{
    public static final Set<Block> softBlocks = new HashSet<Block>();

    public static void initSoftBlocks()
    {
        for (Object o : Block.blockRegistry)
        {
            Block block = (Block) o;

            if (block instanceof BlockFluidBase || block instanceof BlockLiquid || block instanceof IPlantable || block instanceof BlockTorch)
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

    public static boolean areBlocksEqual(Block blockOne, int metaOne, Block blockTwo, int metaTwo)
    {
        return (blockOne == blockTwo) && (metaOne == metaTwo);
    }

    public static boolean areBlocksEqual(IBlockAccess blockAccess, BlockCoord one, BlockCoord two)
    {
        return areBlocksEqual(blockAccess.getBlock(one.x, one.y, one.z), blockAccess.getBlockMetadata(one.x, one.y, one.z), blockAccess.getBlock(two.x, two.y, two.z), blockAccess.getBlockMetadata(two.x, two.y, two.z));
    }

    public static boolean areBlocksEqual(IBlockAccess blockAccess, int x1, int y1, int z1, int x2, int y2, int z2)
    {
        return areBlocksEqual(blockAccess.getBlock(x1, y1, z1), blockAccess.getBlockMetadata(x1, y1, z1), blockAccess.getBlock(x2, y2, z2), blockAccess.getBlockMetadata(x2, y2, z2));
    }

    public static boolean areBlocksEqual(IBlockAccess blockAccess, int x, int y, int z, ForgeDirection direction)
    {
        return areBlocksEqual(blockAccess, x, y, z, x + direction.offsetX, y + direction.offsetY, z + direction.offsetZ);
    }

    public static boolean areBlocksEqual(IBlockAccess blockAccess, Block block, int meta, int x, int y, int z, ForgeDirection direction)
    {
        return (block == blockAccess.getBlock(x + direction.offsetX, y + direction.offsetY, z + direction.offsetZ) && meta == blockAccess.getBlockMetadata(x + direction.offsetX, y + direction.offsetY, z + direction.offsetZ));
    }

    // Silk Touch

    // NOTE: Reflection performance warning
    public static List<ItemStack> createSilkTouchStack(Block block, int meta) {
        ArrayList<ItemStack> items = new ArrayList<ItemStack>();
        ItemStack itemstack = null;

        try {
            Method createStackedBlock = Block.class.getDeclaredMethod("func_149644_j", int.class); // # createStackedBlock
            createStackedBlock.setAccessible(true);
            itemstack = (ItemStack) createStackedBlock.invoke(block, meta);
        } catch (NoSuchMethodException e) {
            LogHelper.warn("NoSuchMethod:");
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            LogHelper.warn("IllegalArgument");
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            LogHelper.warn("IllegalAccess");
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            LogHelper.warn("InvocationTargetException");
            e.printStackTrace();
        }

        if (itemstack == null) {
            return null;
        }

        items.add(itemstack);

        return items;
    }
}
