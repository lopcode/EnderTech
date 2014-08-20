package io.endertech.config;

import io.endertech.util.helper.LocalisationHelper;
import io.endertech.util.helper.LogHelper;
import net.minecraftforge.common.config.Configuration;
import java.io.File;

public class BlockConfig
{
    private static Configuration blockConfig;

    protected static void init(File configFile)
    {
        blockConfig = new Configuration(configFile);
        try
        {
            blockConfig.load();

        } catch (Exception e)
        {
            LogHelper.error(LocalisationHelper.localiseString("error.config.block.load"));
        } finally
        {
            blockConfig.save();
        }
    }
}