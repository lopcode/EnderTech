package io.endertech.modules.dev.block;

import cpw.mods.fml.common.registry.GameRegistry;
import io.endertech.block.ItemBlockBasic;
import io.endertech.reference.Strings;
import net.minecraft.block.Block;

public class DevBlocks
{
    //    public static Block blockSpinningCube;
    public static Block blockChargedPlane;

    public static void init()
    {
        //        blockSpinningCube = new BlockSpinningCube();
        blockChargedPlane = new BlockChargedPlane();

        //        GameRegistry.registerBlock(blockSpinningCube, ItemBlockBasic.class, Strings.Blocks.SPINNING_CUBE_NAME);
        GameRegistry.registerBlock(blockChargedPlane, ItemBlockBasic.class, "endertech." + Strings.Blocks.CHARGED_PLANE_NAME);

        //        ((BlockSpinningCube) blockSpinningCube).init();
        ((BlockChargedPlane) blockChargedPlane).init();
    }
}
