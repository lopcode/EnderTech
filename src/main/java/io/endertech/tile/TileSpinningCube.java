package io.endertech.tile;

import cpw.mods.fml.common.registry.GameRegistry;
import io.endertech.lib.Strings;

public class TileSpinningCube extends TileET
{
    public static void init()
    {
        GameRegistry.registerTileEntity(TileSpinningCube.class, "tile." + Strings.Blocks.SPINNING_CUBE_NAME);
    }
}
