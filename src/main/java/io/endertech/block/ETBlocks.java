package io.endertech.block;

import cpw.mods.fml.common.registry.GameRegistry;
import io.endertech.config.BlockConfig;
import io.endertech.lib.Strings;
import net.minecraft.block.Block;

public class ETBlocks
{
    public static Block blockTank;

    public static void init()
    {
        blockTank = new BlockTank(BlockConfig.blockTankID);

        GameRegistry.registerBlock(blockTank, ItemBlockTank.class, Strings.TANK_NAME);

        ((BlockTank) blockTank).init();
    }
}
