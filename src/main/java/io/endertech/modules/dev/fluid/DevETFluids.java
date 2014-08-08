package io.endertech.modules.dev.fluid;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialLiquid;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

public class DevETFluids
{
    public static Fluid fluidChargedEnder;
    public static Fluid fluidCoFHEnder;
    public static Block blockFluidChargedEnder;
    public static Block blockFluidCoFHEnder;
    public static final Material materialFluidChargedEnder = new MaterialLiquid(MapColor.greenColor);

    public static void init()
    {
        fluidCoFHEnder = FluidRegistry.getFluid("ender");
        fluidChargedEnder = new Fluid("chargedEnder").setLuminosity(15).setDensity(fluidCoFHEnder.getDensity() - 10).setViscosity(fluidCoFHEnder.getViscosity() - 10).setTemperature(fluidCoFHEnder.getTemperature() + 100).setRarity(fluidCoFHEnder.getRarity()).setFlowingIcon(fluidCoFHEnder.getFlowingIcon()).setStillIcon(fluidCoFHEnder.getStillIcon());

        FluidRegistry.registerFluid(fluidChargedEnder);

        blockFluidChargedEnder = new BlockFluidChargedEnder();
        blockFluidCoFHEnder = GameRegistry.findBlock("ThermalFoundation", "FluidEnder");

        GameRegistry.registerBlock(blockFluidChargedEnder, "FluidChargedEnder");
    }
}
