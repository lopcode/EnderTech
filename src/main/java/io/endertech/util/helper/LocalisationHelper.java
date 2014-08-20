package io.endertech.util.helper;

import net.minecraft.util.StatCollector;

public class LocalisationHelper
{
    public static String localiseString(String format, Object... data)
    {
        return StatCollector.translateToLocalFormatted(format, data);
    }
}