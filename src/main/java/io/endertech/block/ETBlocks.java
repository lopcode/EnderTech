package io.endertech.block;

import cpw.mods.fml.common.registry.GameRegistry;
import io.endertech.reference.Strings;
import net.minecraft.block.Block;

public class ETBlocks
{
    public static Block blockTank;
    public static Block blockSpinningCube;
    public static Block blockChargedPlane;

    public static void init()
    {
        blockTank = new BlockTank();
        blockSpinningCube = new BlockSpinningCube();
        blockChargedPlane = new BlockChargedPlane();

        GameRegistry.registerBlock(blockTank, ItemBlockTank.class, Strings.TANK_NAME);
        GameRegistry.registerBlock(blockSpinningCube, ItemSpinningCube.class, Strings.Blocks.SPINNING_CUBE_NAME);
        GameRegistry.registerBlock(blockChargedPlane, ItemChargedPlane.class, Strings.Blocks.CHARGED_PLANE_NAME);

        ((BlockTank) blockTank).init();
        ((BlockSpinningCube) blockSpinningCube).init();
        ((BlockChargedPlane) blockChargedPlane).init();
    }
}
