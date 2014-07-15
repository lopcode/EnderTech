package io.endertech.fluid;

import io.endertech.EnderTech;
import net.minecraft.world.IBlockAccess;

public class BlockFluidChargedEnder extends BlockFluidETBase
{
    public BlockFluidChargedEnder()
    {
        super(ETFluids.fluidChargedEnder, ETFluids.materialFluidChargedEnder, "chargedEnder");
        this.setQuantaPerBlock(4);
        this.setTickRate(30);
        this.setCreativeTab(EnderTech.tabET);
    }

    @Override
    public int getLightValue(IBlockAccess world, int x, int y, int z)
    {
        return ETFluids.fluidChargedEnder.getLuminosity();
    }
}