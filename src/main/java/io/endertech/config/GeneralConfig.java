package io.endertech.config;

import io.endertech.util.helper.LocalisationHelper;
import io.endertech.util.helper.LogHelper;
import net.minecraftforge.common.config.Configuration;
import java.io.File;

public class GeneralConfig
{
    private static Configuration generalConfig;

    public static int tankStorageMultiplier;
    public static boolean debugRender;
    public static boolean gasTopToBottom;
    public static boolean forceLoadDevContent;

    protected static void init(File configFile)
    {
        generalConfig = new Configuration(configFile);
        try
        {
            generalConfig.load();

            tankStorageMultiplier = generalConfig.get("tank", "Tank.StorageMultiplier", 128).getInt(128);
            if (tankStorageMultiplier < 1)
            {
                tankStorageMultiplier = 1;
            }
            if (tankStorageMultiplier > 1024)
            {
                tankStorageMultiplier = 1024;
            }

            debugRender = generalConfig.get("rendering", "Rendering.Debug", false).getBoolean(false);
            gasTopToBottom = generalConfig.get("rendering", "Rendering.Tank.GaseousTopToBottom", false).getBoolean(false);
            forceLoadDevContent = generalConfig.get("development", "Development.ForceLoadDevContent", false).getBoolean(false);
        } catch (Exception e)
        {
            LogHelper.error(LocalisationHelper.localiseString("error.config.general.load"));
        } finally
        {
            generalConfig.save();
        }
    }
}
