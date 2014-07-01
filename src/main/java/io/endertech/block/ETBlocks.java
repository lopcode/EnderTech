package io.endertech.block;

import cpw.mods.fml.common.registry.GameRegistry;
import io.endertech.lib.Strings;
import net.minecraft.block.Block;

public class ETBlocks
{
    public static Block blockTank;
    public static Block blockSpinningCube;

    public static void init()
    {
        blockTank = new BlockTank();
        blockSpinningCube = new BlockSpinningCube();

        GameRegistry.registerBlock(blockTank, ItemBlockTank.class, Strings.TANK_NAME);
        GameRegistry.registerBlock(blockSpinningCube, ItemSpinningCube.class, Strings.Blocks.SPINNING_CUBE_NAME);

        ((BlockTank) blockTank).init();
        ((BlockSpinningCube) blockSpinningCube).init();
    }
}
