package io.endertech.fx;

import io.endertech.client.render.IconRegistry;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityHealthPadFX extends EntityFX
{
    protected double vx;
    protected double vy;
    protected double vz;

    public EntityHealthPadFX(World world, double x, double y, double z, int maxAge, double[] velocity, float[] colour, float sizeModifier)
    {
        super(world, x, y, z, velocity[0], velocity[1], velocity[2]);
        vx = velocity[0];
        vy = velocity[1];
        vz = velocity[2];

        this.customSetup(vx, vy, vz);

        this.particleRed = colour[0];
        this.particleGreen = colour[1];
        this.particleBlue = colour[2];

        setSize(sizeModifier, sizeModifier);
        this.particleScale = 1.0F;
        this.multipleParticleScaleBy(sizeModifier);
        this.particleScale += (this.rand.nextFloat() * 0.2F);
        this.particleMaxAge = ((int) (maxAge / (Math.random() * 0.8D + 0.2D)));
        this.noClip = true;

        this.setParticleIcon(IconRegistry.getIcon("Particle_Health"));
    }

    public void customSetup(double vx, double vy, double vz)
    {
        this.motionX = vx;
        this.motionY = vy;
        this.motionZ = vz;
        float f = (float) (Math.random() + Math.random() + 1.0D) * 0.15F;
        float f1 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
        this.motionX = this.motionX / (double) f1 * (double) f * 0.4D;
        this.motionY = this.motionY / (double) f1 * (double) f * 0.4D;
        this.motionZ = this.motionZ / (double) f1 * (double) f * 0.4D;
    }

    @Override
    public void onUpdate()
    {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        this.posX += this.motionX;
        this.posY += this.motionY;
        this.posZ += this.motionZ;
        this.motionX *= 0.90D;
        this.motionY *= 0.90D;
        this.motionZ *= 0.90D;

        this.particleAlpha *= 0.99D;

        if (this.particleMaxAge-- <= 0)
        {
            setDead();
        }
    }

    @Override
    public int getFXLayer()
    {
        return 1;
    }
}
