package io.endertech.util;

import io.endertech.item.ItemExchanger;
import io.endertech.util.helper.BlockHelper;
import net.minecraft.block.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import java.util.HashSet;
import java.util.Set;

public class Exchange
{
    public static final int radiusTicksDefault = 10;
    public static final Set<Block> specialBlocks = new HashSet<Block>();
    public static final Set<Block> blacklistedBlocks = new HashSet<Block>();
    public BlockCoord origin = null;
    public int radius;
    public int currentRadius = 1;
    public int currentRadiusTicks = 0;
    public Block source;
    public int sourceMeta;
    public ItemStack target;
    public int hotbar_id = 0;
    public EntityPlayer player = null;
    public ForgeDirection orientation = null;

    public Exchange(BlockCoord origin, int radius, Block source, int sourceMeta, ItemStack target, EntityPlayer p, int hotbar_id, ForgeDirection orientation)
    {
        this.origin = origin;
        this.radius = radius;
        this.source = source;
        this.sourceMeta = sourceMeta;
        this.target = target;
        this.player = p;
        this.hotbar_id = hotbar_id;
        this.orientation = orientation;
    }

    public static void initSpecialBlocks()
    {
        for (Object o : Block.blockRegistry)
        {
            Block block = (Block) o;

            if (block instanceof BlockFence || block instanceof BlockFenceGate || block instanceof BlockTorch)
            {
                specialBlocks.add(block);
            }
        }

        for (Object o : Block.blockRegistry)
        {
            Block block = (Block) o;

            if (block instanceof BlockRedstoneLight)
            {
                blacklistedBlocks.add(block);
            }
        }
    }

    public static boolean blockSuitableForSelection(BlockCoord blockCoord, World world, Block block, int blockMeta, ItemStack itemStack)
    {
        if (world.getTileEntity(blockCoord.x, blockCoord.y, blockCoord.z) != null) return false;

        if (world.isAirBlock(blockCoord.x, blockCoord.y, blockCoord.z)) return false;
        if (blacklistedBlocks.contains(block)) return false;
        if (block.getBlockHardness(world, blockCoord.x, blockCoord.y, blockCoord.z) < 0) return false;
        if (BlockHelper.softBlocks.contains(block)) return false;

        if (!ItemExchanger.isCreative(itemStack) && ItemExchanger.creativeOverrideBlocks.contains(block)) return false;

        return true;
    }

    public static boolean blockSuitableForExchange(BlockCoord blockCoord, World world, Block source, int sourceMeta, ItemStack target, ItemStack itemStack, int radius)
    {
        Block worldBlock = world.getBlock(blockCoord.x, blockCoord.y, blockCoord.z);
        int worldMeta = world.getBlockMetadata(blockCoord.x, blockCoord.y, blockCoord.z);

        if (!blockSuitableForSelection(blockCoord, world, worldBlock, worldMeta, itemStack)) return false;

        if (!isBlockExposedWithExchangerExceptions(world, blockCoord.x, blockCoord.y, blockCoord.z) && radius > 0)
            return false;

        if (source != worldBlock || sourceMeta != worldMeta) return false;
        if (target.isItemEqual(new ItemStack(source, 1, sourceMeta))) return false;


        return true;
    }

    public static boolean isBlockExposedWithExchangerExceptions(World world, int x, int y, int z)
    {
        return BlockHelper.INSTANCE.isBlockExposed(world, x, y, z) || isBlockNextToException(world, x, y, z);
    }

    public static boolean isBlockNextToException(World world, int x, int y, int z)
    {
        return isBlockException(world, x + 1, y, z) || isBlockException(world, x - 1, y, z) || isBlockException(world, x, y + 1, z) || isBlockException(world, x, y - 1, z) || isBlockException(world, x, y, z + 1) || isBlockException(world, x, y, z - 1);
    }

    public static boolean isBlockException(World world, int x, int y, int z)
    {
        if (world.isAirBlock(x, y, z)) return false;

        Block block = world.getBlock(x, y, z);
        return isBlockException(block);
    }

    public static boolean isBlockException(Block block)
    {
        return block != null && specialBlocks.contains(block);
    }
}
