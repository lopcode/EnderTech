package io.endertech.fluid;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialLiquid;
import net.minecraftforge.fluids.Fluid;

public class ETFluids
{
    public static Fluid fluidChargedEnder;
    public static Block blockFluidChargedEnder;
    public static final Material materialFluidChargedEnder = new MaterialLiquid(MapColor.foliageColor);

    public static void init()
    {
        //        fluidChargedEnder = new Fluid("chargedEnder").setLuminosity(5).setDensity(5000).setViscosity(4000).setTemperature(300).setRarity(EnumRarity.epic);
        //
        //        FluidRegistry.registerFluid(fluidChargedEnder);
        //
        //        blockFluidChargedEnder = new BlockFluidChargedEnder();
        //
        //        GameRegistry.registerBlock(blockFluidChargedEnder, "FluidChargedEnder");
    }
}
