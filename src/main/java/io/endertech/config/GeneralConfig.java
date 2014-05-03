package io.endertech.config;

import io.endertech.helper.LogHelper;
import net.minecraftforge.common.Configuration;

import java.io.File;

public class GeneralConfig
{
    private static Configuration generalConfig;

    protected static void init(File configFile)
    {
        generalConfig = new Configuration(configFile);
        try
        {
            generalConfig.load();
        } catch (Exception e)
        {
            LogHelper.error("Failed to load general config");
        } finally
        {
            generalConfig.save();
        }
    }
}
