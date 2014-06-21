package io.endertech.helper;

import cpw.mods.fml.common.FMLLog;
import io.endertech.lib.Reference;

import java.util.logging.Level;
import java.util.logging.Logger;

public class LogHelper
{
    private static final Logger ET_LOGGER = Logger.getLogger(Reference.MOD_ID);

    static
    {
        ET_LOGGER.setParent(FMLLog.getLogger());
    }

    public static void log(Level logLevel, Object object)
    {
        ET_LOGGER.log(logLevel, String.valueOf(object));
    }

    public static void debug(Object object)
    {
        log(Level.FINE, object);
    }

    public static void error(Object object)
    {
        log(Level.SEVERE, object);
    }

    public static void fatal(Object object)
    {
        log(Level.SEVERE, object);
    }

    public static void info(Object object)
    {
        log(Level.INFO, object);
    }

    public static void warn(Object object)
    {
        log(Level.WARNING, object);
    }
}
