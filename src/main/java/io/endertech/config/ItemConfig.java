package io.endertech.config;

import io.endertech.helper.LogHelper;
import net.minecraftforge.common.Configuration;

import java.io.File;

public class ItemConfig
{
    private static Configuration itemConfig;

    public static int itemExchangerID;
    public static int itemExchangerBlockCost;
    public static int itemExchangerMaxRadius;

    protected static void init(File configFile)
    {
        itemConfig = new Configuration(configFile);
        try
        {
            itemConfig.load();

            itemExchangerID = itemConfig.getItem("item.id", "Tool.Exchanger", 15363).getInt(15363);
            itemExchangerBlockCost = itemConfig.get("item.general", "Exchanger.BlockCost", 8192).getInt(8192);
            itemExchangerMaxRadius = itemConfig.get("item.general", "Exchanger.MaxRadius", 8).getInt(8);
            if (itemExchangerMaxRadius < 1)
            {
                itemExchangerMaxRadius = 1;
            }
            if (itemExchangerMaxRadius > 64)
            {
                itemExchangerMaxRadius = 64;
            }
        }
        catch (Exception e)
        {
            LogHelper.error("Failed to load item config");
        }
        finally
        {
            itemConfig.save();
        }
    }
}