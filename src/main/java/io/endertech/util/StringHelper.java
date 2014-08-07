package io.endertech.util;

import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.fluids.FluidStack;
import java.text.DecimalFormat;

public class StringHelper
{
    public static final String holdShiftForDetails = EnumChatFormatting.GRAY + "Hold " + EnumChatFormatting.YELLOW + EnumChatFormatting.ITALIC + "Shift " + EnumChatFormatting.RESET + EnumChatFormatting.GRAY + "for Details" + EnumChatFormatting.RESET;
    public static DecimalFormat twoDP = new DecimalFormat("#.##");

    public static String getEnergyString(int energy)
    {
        if (energy >= 1000000)
        {
            return String.valueOf(twoDP.format(energy / 1000000.0)) + "M";
        } else if (energy >= 1000)
        {
            return String.valueOf(energy / 1000) + "k";
        } else
        {
            return String.valueOf(energy);
        }
    }

    public static String getFluidName(FluidStack fluidStack)
    {
        return cofh.lib.util.helpers.StringHelper.getFluidName(fluidStack);
    }
}
