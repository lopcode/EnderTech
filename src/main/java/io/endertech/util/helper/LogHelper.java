package io.endertech.util.helper;

import io.endertech.reference.Reference;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LogHelper
{
    private static final Logger logger = LogManager.getLogger(Reference.MOD_ID);

    public static void log(Level logLevel, String format, Object... data) { logger.log(logLevel, format, data); }

    public static void debug(String format, Object... data)
    {
        log(Level.DEBUG, format, data);
    }

    public static void error(String format, Object... data)
    {
        log(Level.ERROR, format, data);
    }

    public static void fatal(String format, Object... data)
    {
        log(Level.FATAL, format, data);
    }

    public static void info(String format, Object... data)
    {
        log(Level.INFO, format, data);
    }

    public static void warn(String format, Object... data)
    {
        log(Level.WARN, format, data);
    }
}
