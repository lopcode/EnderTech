package io.endertech.modules.dev.fluid;

import net.minecraft.block.material.Material;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;

public class BlockFluidETBase extends BlockFluidClassic
{
    public String name;

    public BlockFluidETBase(Fluid fluid, Material material, String name)
    {
        super(fluid, material);

        this.name = name;
    }
}
