package io.endertech.config;

import io.endertech.helper.LogHelper;
import net.minecraftforge.common.Configuration;

import java.io.File;

public class BlockConfig
{
    private static Configuration blockConfig;

    public static int blockTankID;

    protected static void init(File configFile)
    {
        blockConfig = new Configuration(configFile);
        try {
            blockConfig.load();

            blockTankID = blockConfig.getBlock("block.id", "Tank", 3500).getInt(3500);
        } catch (Exception e) {
            LogHelper.error("Failed to load block config");
        } finally {
            blockConfig.save();
        }
    }
}