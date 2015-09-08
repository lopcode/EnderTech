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
    public static boolean healthPadForceAlwaysSendHealth;
    public static int maxTankEnergyInputRate;

    private static Configuration generalConfig;

    private static int TANK_MULTIPLIER_MAX = Integer.MAX_VALUE;
    private static int TANK_ENERGY_INPUT_MAX = Integer.MAX_VALUE;

    protected static void init(File configFile)
    {
        generalConfig = new Configuration(configFile);
        try
        {
            generalConfig.load();

            tankStorageMultiplier = generalConfig.get("tank", "Tank.StorageMultiplier", 512).getInt(512);
            if (tankStorageMultiplier < 1)
            {
                tankStorageMultiplier = 1;
            }
            if (tankStorageMultiplier > TANK_MULTIPLIER_MAX)
            {
                tankStorageMultiplier = TANK_MULTIPLIER_MAX;
            }

            maxTankEnergyInputRate = generalConfig.get("tank", "Tank.MaxEnergyInputRate", 20000).getInt(20000);
            if (maxTankEnergyInputRate < 1)
            {
                maxTankEnergyInputRate = 1;
            }
            if (maxTankEnergyInputRate > TANK_ENERGY_INPUT_MAX)
            {
                maxTankEnergyInputRate = TANK_ENERGY_INPUT_MAX;
            }

            debugRender = generalConfig.get("rendering", "Rendering.Debug", false).getBoolean(false);
            gasTopToBottom = generalConfig.get("rendering", "Rendering.Tank.GaseousTopToBottom", false).getBoolean(false);
            forceLoadDevContent = generalConfig.get("development", "Development.ForceLoadDevContent", false).getBoolean(false);

            GUITopLeftXOffset = generalConfig.get("rendering", "Rendering.GUITopLeftXOffset", 0).getInt(0);
            GUITopLeftYOffset = generalConfig.get("rendering", "Rendering.GUITopLeftYOffset", 0).getInt(0);

            healthPadChargeCostPerHalfHeart = generalConfig.get("healthpad", "HealthPad.ChargePerHalfHeart", 192000).getInt(192000);
            healthPadForceAlwaysSendHealth = generalConfig.get("healthpad", "HealthPad.ForceAlwaysSendHealth", false).getBoolean(false);
        } catch (Exception e)
        {
            LogHelper.error(LocalisationHelper.localiseString("error.config.general.load"));
        } finally
        {
            generalConfig.save();
        }
    }
}
