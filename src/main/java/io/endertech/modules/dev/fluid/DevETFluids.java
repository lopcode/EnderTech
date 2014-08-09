package io.endertech.modules.dev.fluid;

import cpw.mods.fml.common.registry.GameRegistry;
import io.endertech.item.ItemBucket;
import io.endertech.util.fluid.BucketHandler;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialLiquid;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;

public class DevETFluids
{
    public static Fluid fluidChargedEnder;
    public static Fluid fluidCoFHEnder;
    public static Block blockFluidChargedEnder;
    public static Block blockFluidCoFHEnder;

    public static final Material materialFluidChargedEnder = new MaterialLiquid(MapColor.greenColor);

    public static ItemBucket itemBucket;
    public static ItemStack bucketChargedEnder;

    public static void init()
    {
        fluidCoFHEnder = FluidRegistry.getFluid("ender");
        fluidChargedEnder = new Fluid("chargedEnder").setLuminosity(15).setDensity(fluidCoFHEnder.getDensity() - 10).setViscosity(fluidCoFHEnder.getViscosity() - 10).setTemperature(fluidCoFHEnder.getTemperature() + 100).setRarity(fluidCoFHEnder.getRarity()).setFlowingIcon(fluidCoFHEnder.getFlowingIcon()).setStillIcon(fluidCoFHEnder.getStillIcon());

        FluidRegistry.registerFluid(fluidChargedEnder);

        blockFluidChargedEnder = new BlockFluidChargedEnder();
        blockFluidCoFHEnder = GameRegistry.findBlock("ThermalFoundation", "FluidEnder");

        GameRegistry.registerBlock(blockFluidChargedEnder, "FluidChargedEnder");

        itemBucket = (ItemBucket) new ItemBucket().setUnlocalizedName("bucket");
        GameRegistry.registerItem(itemBucket, "bucket");

        bucketChargedEnder = itemBucket.addItem(0, "bucketChargedEnder", EnumRarity.rare.ordinal());
        BucketHandler.registerBucket(blockFluidChargedEnder, 0, bucketChargedEnder);
        FluidContainerRegistry.registerFluidContainer(fluidChargedEnder, bucketChargedEnder, FluidContainerRegistry.EMPTY_BUCKET);
    }
}
