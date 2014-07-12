package io.endertech.util;

import io.endertech.util.BlockCoord;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import java.util.Set;

public class Exchange
{
    public int remainingTicks = 0;
    public BlockCoord coord = null;
    public Block source;
    public int sourceMeta;
    public ItemStack target;
    public int hotbar_id = 0;
    public EntityPlayer player = null;
    public Set<BlockCoord> visits;

    Exchange(BlockCoord coord, Block source, int sourceMeta, ItemStack target, int remainingTicks, EntityPlayer p, int hotbar_id, Set<BlockCoord> visits)
    {
        this.coord = coord;
        this.source = source;
        this.sourceMeta = sourceMeta;
        this.target = target;
        this.remainingTicks = remainingTicks;
        this.player = p;
        this.hotbar_id = hotbar_id;
        this.visits = visits;
    }
}
