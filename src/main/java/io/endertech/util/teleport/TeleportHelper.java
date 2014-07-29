package io.endertech.util.teleport;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;

public class TeleportHelper
{
    public static boolean teleportEntityWithinCurrentDimension(EntityLivingBase entity, double x, double y, double z)
    {
        EnderTeleportEvent event = new EnderTeleportEvent(entity, x, y, z, 0.0F);
        if (MinecraftForge.EVENT_BUS.post(event)) return false;

        entity.setPositionAndUpdate(event.targetX, event.targetY, event.targetZ);
        playTeleportSound(entity);

        return true;
    }

    public static void playTeleportSound(EntityLivingBase entity)
    {
        entity.worldObj.playSoundEffect(entity.posX, entity.posY, entity.posZ, "mob.endermen.portal", 1.0F, 1.0F);
        entity.playSound("mob.endermen.portal", 1.0F, 1.0F);
    }

    public static void teleportPlayerToDimensionWithCoords(EntityPlayerMP entity, int dimension, double x, double y, double z)
    {
        if (dimension == entity.dimension)
        {
            teleportEntityWithinCurrentDimension(entity, x, y, z);
            return;
        }

        playTeleportSound(entity);

        MinecraftServer server = MinecraftServer.getServer();
        WorldServer worldServer = server.worldServerForDimension(dimension);
        MinecraftServer.getServer().getConfigurationManager().transferPlayerToDimension(entity, dimension, new ETTeleporter(worldServer, x, y, z));
    }
}
