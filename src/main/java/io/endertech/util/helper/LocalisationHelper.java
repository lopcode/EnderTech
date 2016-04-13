package io.endertech.util.helper;

import net.minecraft.util.StatCollector;

public class LocalisationHelper
{
    /**
     * @deprecated Replace with the String extension methods in io/endertech/ext/string.kt (i18n/applyAsi18nFormat)
     */
    @Deprecated
    public static String localiseString(String format, Object... data)
    {
        return StatCollector.translateToLocalFormatted(format, data);
    }
}