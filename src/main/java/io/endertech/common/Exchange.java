package io.endertech.common;

import io.endertech.util.BlockCoord;
import net.minecraft.entity.player.EntityPlayer;
import java.util.Set;

public class Exchange
{
    public int remainingTicks = 0;
    public BlockCoord coord = null;
    public int sourceId = 0;
    public int sourceMetadata = 0;
    public int targetId = 0;
    public int targetMetadata = 0;
    public int hotbar_id = 0;
    public EntityPlayer player = null;
    public Set<BlockCoord> visits;

    Exchange(BlockCoord coord, int sourceId, int sourceMetadata, int targetId, int targetMetadata, int remainingTicks, EntityPlayer p, int hotbar_id, Set<BlockCoord> visits)
    {
        this.coord = coord;
        this.sourceId = sourceId;
        this.sourceMetadata = sourceMetadata;
        this.targetId = targetId;
        this.targetMetadata = targetMetadata;
        this.remainingTicks = remainingTicks;
        this.player = p;
        this.hotbar_id = hotbar_id;
        this.visits = visits;
    }
}
