package io.endertech.util;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

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

    public Exchange(BlockCoord origin, int radius, Block source, int sourceMeta, ItemStack target, EntityPlayer p, int hotbar_id)
    {
        this.origin = origin;
        this.radius = radius;
        this.source = source;
        this.sourceMeta = sourceMeta;
        this.target = target;
        this.player = p;
        this.hotbar_id = hotbar_id;
    }
}
