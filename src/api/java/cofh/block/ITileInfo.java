package cofh.block;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.ForgeDirection;

import java.util.List;

// Included so the TE Multimeter can be used to debug blocks
// TODO: better way to do this?

public abstract interface ITileInfo
{
    public abstract void getTileInfo(List<String> info, ForgeDirection side, EntityPlayer player, boolean debug);
}