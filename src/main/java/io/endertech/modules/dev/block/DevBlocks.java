package io.endertech.modules.dev.block;

import cpw.mods.fml.common.registry.GameRegistry;
import io.endertech.modules.dev.item.ItemBlockChargePad;
import io.endertech.reference.Strings;
import net.minecraft.block.Block;

public class DevBlocks
{
    //    public static Block blockSpinningCube;
    public static Block blockChargePad;

    public static void init()
    {
        //        blockSpinningCube = new BlockSpinningCube();
        blockChargePad = new BlockChargePad();

        //        GameRegistry.registerBlock(blockSpinningCube, ItemBlockBasic.class, Strings.Blocks.SPINNING_CUBE_NAME);
        GameRegistry.registerBlock(blockChargePad, ItemBlockChargePad.class, Strings.Blocks.CHARGE_PAD);

        //        ((BlockSpinningCube) blockSpinningCube).init();
        ((BlockChargePad) blockChargePad).init();
    }
}
