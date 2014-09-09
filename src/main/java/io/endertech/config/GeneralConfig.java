package io.endertech.config;

import io.endertech.util.helper.LocalisationHelper;
import io.endertech.util.helper.LogHelper;
import net.minecraftforge.common.config.Configuration;
import java.io.File;

public class GeneralConfig
{
    public static int tankStorageMultiplier;
    public static boolean debugRender;
    public static boolean gasTopToBottom;
    public static boolean forceLoadDevContent;
    public static int GUITopLeftYOffset;
    public static int GUITopLeftXOffset;
    public static int healthPadChargeCostPerHalfHeart;
    private static Configuration generalConfig;

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

            GUITopLeftXOffset = generalConfig.get("rendering", "Rendering.GUITopLeftXOffset", 0).getInt(0);
            GUITopLeftYOffset = generalConfig.get("rendering", "Rendering.GUITopLeftYOffset", 0).getInt(0);

            healthPadChargeCostPerHalfHeart = generalConfig.get("healthpad", "HealthPad.ChargePerHalfHeart", 192000).getInt(192000);
        } catch (Exception e)
        {
            LogHelper.error(LocalisationHelper.localiseString("error.config.general.load"));
        } finally
        {
            generalConfig.save();
        }
    }
}
