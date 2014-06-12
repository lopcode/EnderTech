package io.endertech.tile;

import cofh.block.ITileInfo;
import cpw.mods.fml.common.registry.GameRegistry;
import io.endertech.helper.LogHelper;
import io.endertech.helper.StringHelper;
import io.endertech.lib.Strings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.*;

import java.util.List;

public class TileTank extends TileET implements IFluidHandler, ITileInfo
{
    private FluidTank tank = new FluidTank(FluidContainerRegistry.BUCKET_VOLUME * 32);
    private static final String TANK_NAME = "MainTank";

    @Override
    public final void writeToNBT(NBTTagCompound nbt)
    {
        LogHelper.debug("Writing tank at " + this.xCoord + " " + this.yCoord + " " + this.zCoord);
        super.writeToNBT(nbt);

        NBTTagCompound tankNBT = new NBTTagCompound();
        tank.writeToNBT(tankNBT);
        nbt.setTag(TANK_NAME, tankNBT);
    }

    @Override
    public final void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        LogHelper.debug("Reading tank at " + this.xCoord + " " + this.yCoord + " " + this.zCoord);

        if (nbt.hasKey(TANK_NAME)) {
            LogHelper.debug("Got tank NBT data");
            NBTTagCompound tankNBT = nbt.getCompoundTag(TANK_NAME);
            tank.readFromNBT(tankNBT);
        } else {
            LogHelper.debug("Didn't have tank NBT data - new tank");
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
        if (this.canDrain(from, null)) {
            return this.drain(from, resource.amount, doDrain);
        } else {
            return null;
        }
    }

    @Override
    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain)
    {
        if (this.canDrain(from, null)) {
            return tank.drain(maxDrain, doDrain);
        } else {
            return null;
        }
    }

    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid)
    {
        return true;
    }

    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid)
    {
        return this.tank.getFluidAmount() > 0;
    }

    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection from)
    {
        return new FluidTankInfo[] {tank.getInfo()};
    }

    public static void init()
    {
        GameRegistry.registerTileEntity(TileTank.class, "tile." + Strings.TANK_NAME);
    }

    @Override
    public void getTileInfo(List<String> info, ForgeDirection side, EntityPlayer player, boolean debug)
    {
        if (debug) {
            return;
        }

        info.add("EnderTech Tank");

        if (tank.getFluidAmount() <= 0) {
            info.add(" Fluid: none");
        } else {
            info.add(" Fluid: " + StringHelper.getFluidString(tank.getFluid().getFluid()));
            info.add(" Contents: " + tank.getFluidAmount() + " / " + tank.getCapacity() + " mB");
        }
    }
}
