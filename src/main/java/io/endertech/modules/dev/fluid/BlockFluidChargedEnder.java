package io.endertech.modules.dev.fluid;

import cofh.lib.util.helpers.ServerHelper;
import io.endertech.reference.Strings;
import io.endertech.util.teleport.TeleportHelper;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

// Derived from CoFH's BlockFluidEnder

public class BlockFluidChargedEnder extends BlockFluidETBase
{
    public static final int LEVELS = 5;

    public BlockFluidChargedEnder()
    {
        super(DevETFluids.fluidChargedEnder, DevETFluids.materialFluidChargedEnder, "chargedEnder");
        setQuantaPerBlock(LEVELS);
        setTickRate(30);
        setHardness(2000F);
        setLightOpacity(7);
        //setCreativeTab(EnderTech.tabET);
        setBlockName(Strings.Blocks.FLUID_CHARGED_ENDER_NAME);
    }

    @Override
    public int getLightValue(IBlockAccess world, int x, int y, int z)
    {
        return DevETFluids.fluidChargedEnder.getLuminosity();
    }

    @Override
    public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity)
    {
        if (world.getTotalWorldTime() % 4 == 0)
        {
            int x2 = x - 8 + world.rand.nextInt(24);
            int y2 = y + world.rand.nextInt(24);
            int z2 = z - 8 + world.rand.nextInt(24);
            if (!world.getBlock(x2, y2, z2).getMaterial().isSolid())
            {
                if (entity instanceof EntityLivingBase)
                {
                    TeleportHelper.teleportEntityWithinCurrentDimension((EntityLivingBase) entity, x2, y2, z2);
                } else
                {
                    entity.setPosition(x2, y2, z2);
                    entity.worldObj.playSoundEffect(x2, y2, z2, "mob.endermen.portal", 1.0F, 1.0F);
                    entity.playSound("mob.endermen.portal", 1.0F, 1.0F);
                }
            }
        }
    }

    @Override
    public IIcon getIcon(int side, int meta)
    {
        return DevETFluids.blockFluidCoFHEnder.getIcon(side, meta);
    }
}