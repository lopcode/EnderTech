package io.endertech.block;

import cpw.mods.fml.common.registry.GameRegistry;
import io.endertech.config.BlockConfig;
import net.minecraft.block.Block;

public class ETBlocks
{
    public static Block blockTank;

    public static void init()
    {
        blockTank = new BlockTank(BlockConfig.blockTankID);

        GameRegistry.registerBlock(blockTank, ItemBlockTank.class, "Tank");

        ((BlockTank) blockTank).init();
    }
}
