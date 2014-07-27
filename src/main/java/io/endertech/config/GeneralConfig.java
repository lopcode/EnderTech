package io.endertech.config;

import io.endertech.util.LogHelper;
import net.minecraftforge.common.config.Configuration;
import java.io.File;

public class GeneralConfig
{
    private static Configuration generalConfig;

    public static int tankStorageMultiplier;

    protected static void init(File configFile)
    {
        generalConfig = new Configuration(configFile);
        try
        {
            generalConfig.load();

            tankStorageMultiplier = generalConfig.get("tank", "Tank.StorageMultiplier", 64).getInt(64);
            if (tankStorageMultiplier < 1)
            {
                tankStorageMultiplier = 1;
            }
            if (tankStorageMultiplier > 128)
            {
                tankStorageMultiplier = 128;
            }
        } catch (Exception e)
        {
            LogHelper.error("Failed to load general config");
        } finally
        {
            generalConfig.save();
        }
    }
}
