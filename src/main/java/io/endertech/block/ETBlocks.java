package io.endertech.block;

import cpw.mods.fml.common.registry.GameRegistry;
import io.endertech.multiblock.block.BlockTankPart;
import io.endertech.multiblock.item.ItemBlockTankPart;
import io.endertech.reference.Strings;
import net.minecraft.block.Block;

public class ETBlocks
{
    public static Block blockTank;
    public static Block blockSpinningCube;
    public static Block blockChargedPlane;
    public static Block blockTankPart;

    public static void init()
    {
        blockTank = new BlockTank();
        blockSpinningCube = new BlockSpinningCube();
        blockChargedPlane = new BlockChargedPlane();
        blockTankPart = new BlockTankPart();

        GameRegistry.registerBlock(blockTank, ItemBlockBasic.class, Strings.TANK_NAME);
        GameRegistry.registerBlock(blockSpinningCube, ItemBlockBasic.class, Strings.Blocks.SPINNING_CUBE_NAME);
        GameRegistry.registerBlock(blockChargedPlane, ItemBlockBasic.class, Strings.Blocks.CHARGED_PLANE_NAME);
        GameRegistry.registerBlock(blockTankPart, ItemBlockTankPart.class, Strings.Blocks.TANK_PART_NAME);

        ((BlockTank) blockTank).init();
        ((BlockSpinningCube) blockSpinningCube).init();
        ((BlockChargedPlane) blockChargedPlane).init();
        ((BlockTankPart) blockTankPart).init();
    }
}
