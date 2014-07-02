package io.endertech.tile;

import cpw.mods.fml.common.registry.GameRegistry;
import io.endertech.lib.Strings;
import io.endertech.util.Geometry;
import net.minecraft.util.Vec3;
import java.util.Random;

public class TileSpinningCube extends TileET
{
    public Random random = new Random();
    public double speed;
    public Vec3 randomAddition;
    public Vec3[] cubeVertices;
    public double yAddition = 0.0;

    public TileSpinningCube()
    {
        cubeVertices = new Vec3[Geometry.cubeVertices.length];
        for (int i = 0; i < Geometry.cubeVertices.length; i++)
        {
            cubeVertices[i] = Vec3.createVectorHelper(Geometry.cubeVertices[i].xCoord, Geometry.cubeVertices[i].yCoord, Geometry.cubeVertices[i].zCoord);
        }

        this.speed = random.nextDouble() / 100.0;
        this.randomAddition = Vec3.createVectorHelper(((random.nextDouble() * 2) - 1) / 10.00, ((random.nextDouble() * 2) - 1) / 10.00, ((random.nextDouble() * 2) - 1) / 10.00);
    }

    public static void init()
    {
        GameRegistry.registerTileEntity(TileSpinningCube.class, "tile." + Strings.Blocks.SPINNING_CUBE_NAME);
    }
}
