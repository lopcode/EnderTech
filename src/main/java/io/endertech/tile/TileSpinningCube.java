package io.endertech.tile;

import cpw.mods.fml.common.registry.GameRegistry;
import io.endertech.network.PacketETBase;
import io.endertech.reference.Strings;
import io.endertech.util.Geometry;
import io.endertech.util.helper.LogHelper;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Vec3;
import java.util.Random;

public class TileSpinningCube extends TileET
{
    public Random random = new Random();
    public float size = 0.5f;
    public double speed;
    public Vec3 randomAddition;
    public Vec3[] cubeVertices;
    public double yAddition = 0.0;
    public Vec3 timedRandomAddition;
    public Vec3 directionVector = Vec3.createVectorHelper(1, 0, 0);

    public TileSpinningCube()
    {
        cubeVertices = new Vec3[Geometry.cubeVertices.length];
        for (int i = 0; i < Geometry.cubeVertices.length; i++)
        {
            cubeVertices[i] = Vec3.createVectorHelper(Geometry.cubeVertices[i].xCoord, Geometry.cubeVertices[i].yCoord, Geometry.cubeVertices[i].zCoord);
        }

        this.speed = random.nextDouble();
        this.randomAddition = Vec3.createVectorHelper(0, 0, 0);
    }

    public static void init()
    {
        GameRegistry.registerTileEntity(TileSpinningCube.class, "tile." + Strings.Blocks.SPINNING_CUBE_NAME);
    }

    public void createRandomAddition()
    {
        this.randomAddition = Vec3.createVectorHelper(((random.nextDouble() * 2) - 1) / 10.00, ((random.nextDouble() * 2) - 1) / 10.00, ((random.nextDouble() * 2) - 1) / 10.00);
    }

    @Override
    public void updateEntity()
    {
        super.updateEntity();

        //this.speed = 1;

        //directionVector.rotateAroundX((float) (this.speed * this.randomAddition.xCoord));
        //directionVector.rotateAroundY((float) (this.speed * this.randomAddition.xCoord));
        //directionVector.rotateAroundZ((float) (this.speed * this.randomAddition.zCoord));

        /*float xAngleRadians = (float) directionVector.xCoord + (float) (this.speed * this.randomAddition.xCoord);
        //float yAngleRadians = (float) directionVector.yCoord + (float) (this.speed * this.randomAddition.yCoord);
        float zAngleRadians = (float) directionVector.zCoord + (float) (this.speed * this.randomAddition.zCoord);

        if(xAngleRadians >= (2.0 * Math.PI)) xAngleRadians = xAngleRadians - (2.0f * (float)Math.PI);
        if(xAngleRadians <= -(2.0 * Math.PI)) xAngleRadians = xAngleRadians + (2.0f * (float)Math.PI);
        //if(yAngleRadians >= (2.0 * Math.PI)) yAngleRadians = yAngleRadians - (2.0f * (float)Math.PI);
        //if(yAngleRadians <= -(2.0 * Math.PI)) yAngleRadians = yAngleRadians + (2.0f * (float)Math.PI);
        if(zAngleRadians >= (2.0 * Math.PI)) zAngleRadians = zAngleRadians - (2.0f * (float)Math.PI);
        if(zAngleRadians <= -(2.0 * Math.PI)) zAngleRadians = zAngleRadians + (2.0f * (float)Math.PI);

        directionVector = Vec3.createVectorHelper(xAngleRadians, 0, zAngleRadians);*/

        for (Vec3 vector : this.cubeVertices)
        {
            vector.rotateAroundX((float) (this.speed * this.randomAddition.xCoord));
            vector.rotateAroundY((float) (this.speed * this.randomAddition.xCoord));
            vector.rotateAroundZ((float) (this.speed * this.randomAddition.zCoord));
        }

        this.yAddition += 0.05;
        if (this.yAddition >= (2.0 * Math.PI)) this.yAddition = this.yAddition - (2.0 * Math.PI);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbtTagCompound)
    {
        super.readFromNBT(nbtTagCompound);
        double x = 0;
        double y = 0;
        double z = 0;
        if (nbtTagCompound.hasKey("randomAddition.x")) x = nbtTagCompound.getDouble("randomAddition.x");
        if (nbtTagCompound.hasKey("randomAddition.y")) y = nbtTagCompound.getDouble("randomAddition.z");
        if (nbtTagCompound.hasKey("randomAddition.z")) z = nbtTagCompound.getDouble("randomAddition.y");
        this.randomAddition = Vec3.createVectorHelper(x, y, z);

        if (nbtTagCompound.hasKey("speed")) this.speed = nbtTagCompound.getDouble("speed");

        LogHelper.info("Reading cube NBT: " + this.toString());
    }

    @Override
    public void writeToNBT(NBTTagCompound nbtTagCompound)
    {
        super.writeToNBT(nbtTagCompound);
        nbtTagCompound.setDouble("randomAddition.x", this.randomAddition.xCoord);
        nbtTagCompound.setDouble("randomAddition.y", this.randomAddition.yCoord);
        nbtTagCompound.setDouble("randomAddition.z", this.randomAddition.zCoord);
        nbtTagCompound.setDouble("speed", this.speed);

        LogHelper.info("Writing cube NBT: " + this.toString());
    }

    @Override
    public PacketETBase getPacket()
    {
        PacketETBase tilePacket = super.getPacket();
        tilePacket.addVec3(this.randomAddition);
        tilePacket.addDouble(this.speed);
        return tilePacket;
    }

    @Override
    public void handleTilePacket(PacketETBase packetETBase, boolean isServer)
    {
        super.handleTilePacket(packetETBase, isServer);

        if (!isServer)
        {
            this.randomAddition = packetETBase.getVec3();
            this.speed = packetETBase.getDouble();
        }
    }

    public void applySpeedUp()
    {
        this.speed += 0.2;
    }

    @Override
    public String toString()
    {
        return "Spinning cube: position " + this.xCoord + ", " + this.yCoord + ", " + this.zCoord + " S: " + this.speed + " R: " + this.randomAddition.xCoord + ", " + this.randomAddition.yCoord + ", " + this.randomAddition.zCoord;
    }
}
