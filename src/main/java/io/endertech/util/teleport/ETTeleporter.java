package io.endertech.util.teleport;

import net.minecraft.entity.Entity;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;

public class ETTeleporter extends Teleporter
{
    private double x;
    private double y;
    private double z;

    public ETTeleporter(WorldServer worldServer, double x, double y, double z)
    {
        super(worldServer);
        this.setTarget(x, y, z);
    }

    public void setTarget(double x, double y, double z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public void placeInPortal(Entity entity, double x, double y, double z, float rotationYaw)
    {
        entity.setLocationAndAngles(this.x, this.y, this.z, rotationYaw, 0);
    }

    // Below overriden to make sure nothing gets created in the world

    @Override
    public boolean placeInExistingPortal(Entity entity, double x, double y, double z, float p_77184_8_)
    {
        return true;
    }

    @Override
    public boolean makePortal(Entity entity)
    {
        return true;
    }

    @Override
    public void removeStalePortalLocations(long p_85189_1_)
    {

    }
}
