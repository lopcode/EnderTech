package io.endertech.tile;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.*;

public class TileTank extends TileET implements IFluidHandler
{
    private FluidTank tank = new FluidTank(FluidContainerRegistry.BUCKET_VOLUME * 32);
    private static final String TANK_NAME = "MainTank";

    @Override
    public final void writeToNBT(NBTTagCompound nbt)
    {
        NBTTagCompound tankNBT = new NBTTagCompound();
        tank.writeToNBT(tankNBT);
        nbt.setTag(TANK_NAME, tankNBT);
    }

    @Override
    public final void readFromNBT(NBTTagCompound nbt)
    {
        if (nbt.hasKey(TANK_NAME))
        {
            NBTTagCompound tankNBT = nbt.getCompoundTag(TANK_NAME);
            super.readFromNBT(tankNBT);
            tank.readFromNBT(tankNBT);
        }
    }

    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill)
    {
        return tank.fill(resource, doFill);
    }

    @Override
    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain)
    {
        return this.drain(from, resource.amount, doDrain);
    }

    @Override
    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain)
    {
        return tank.drain(maxDrain, doDrain);
    }

    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid)
    {
        return true;
    }

    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid)
    {
        return true;
    }

    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection from)
    {
        return new FluidTankInfo[] {tank.getInfo()};
    }

    public static void init()
    {
        GameRegistry.registerTileEntity(TileTank.class, "endertech.Tank");
    }
}
