package io.endertech.helper;

import cpw.mods.fml.common.FMLLog;
import io.endertech.lib.Reference;
import org.apache.logging.log4j.Level;

public class LogHelper
{
    public static void log(Level logLevel, Object object)
    {
        FMLLog.log(Reference.MOD_NAME, logLevel, String.valueOf(object));
    }

    public static void debug(Object object)
    {
        log(Level.DEBUG, object);
    }

    public static void error(Object object)
    {
        log(Level.ERROR, object);
    }

    public static void fatal(Object object)
    {
        log(Level.FATAL, object);
    }

    public static void info(Object object)
    {
        log(Level.INFO, object);
    }

    public static void warn(Object object)
    {
        log(Level.WARN, object);
    }
}
