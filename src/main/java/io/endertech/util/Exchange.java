package io.endertech.util;

import io.endertech.item.ItemExchanger;
import io.endertech.util.helper.BlockHelper;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class Exchange
{
    public BlockCoord origin = null;
    public int radius;
    public int currentRadius = 1;
    public int currentRadiusTicks = 0;
    public static final int radiusTicksDefault = 10;
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

    public static boolean blockSuitableForExchange(BlockCoord blockCoord, World world, Block source, int sourceMeta, ItemStack target, ItemStack itemStack)
    {
        if (world.getTileEntity(blockCoord.x, blockCoord.y, blockCoord.z) != null) return false;

        Block worldBlock = world.getBlock(blockCoord.x, blockCoord.y, blockCoord.z);
        int worldMeta = world.getBlockMetadata(blockCoord.x, blockCoord.y, blockCoord.z);

        if (world.isAirBlock(blockCoord.x, blockCoord.y, blockCoord.z)) return false;
        if (!BlockHelper.isBlockExposed(world, blockCoord.x, blockCoord.y, blockCoord.z)) return false;

        if (source != worldBlock || sourceMeta != worldMeta) return false;
        if (target.isItemEqual(new ItemStack(source, 1, sourceMeta))) return false;

        if (!ItemExchanger.isCreative(itemStack) && ItemExchanger.creativeOverrideBlocks.contains(worldBlock))
            return false;

        return true;
    }
}
