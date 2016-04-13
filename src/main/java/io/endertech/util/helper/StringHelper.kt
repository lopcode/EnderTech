package io.endertech.util.helper

import net.minecraft.util.EnumChatFormatting
import net.minecraftforge.fluids.FluidStack
import java.text.DecimalFormat

object StringHelper {
    val holdShiftForDetails = EnumChatFormatting.GRAY + LocalisationHelper.localiseString("info.hold_for_details.hold") + " " + EnumChatFormatting.YELLOW + EnumChatFormatting.ITALIC + LocalisationHelper.localiseString("info.hold_for_details.shift") + EnumChatFormatting.RESET + " " + EnumChatFormatting.GRAY + LocalisationHelper.localiseString("info.hold_for_details.for_details") + EnumChatFormatting.RESET
    var twoDP = DecimalFormat("#.##")

    fun getEnergyString(energy: Int): String {
        if (energy == Integer.MAX_VALUE) return LocalisationHelper.localiseString("info.infinite")

        if (energy >= 1000000) {
            return twoDP.format(energy / 1000000.0).toString() + "M"
        } else if (energy >= 1000) {
            return (energy / 1000).toString() + "k"
        } else {
            return energy.toString()
        }
    }

    fun getFluidName(fluidStack: FluidStack): String {
        return cofh.lib.util.helpers.StringHelper.getFluidName(fluidStack)
    }
}
