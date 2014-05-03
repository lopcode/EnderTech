package io.endertech.items;

import io.endertech.config.ItemConfig;
import io.endertech.lib.Strings;
import net.minecraft.item.ItemStack;

public class ETItems
{
    public static ItemExchanger itemExchanger;
    public static ItemStack toolExchangerCreative;
    public static ItemStack toolExchangerBasic;
    public static ItemStack toolExchangerAdvanced;

    public static void init()
    {
        itemExchanger = (ItemExchanger) new ItemExchanger(ItemConfig.itemExchangerID).setUnlocalizedName(Strings.EXCHANGER_BASE);

        loadItems();
    }

    public static void loadItems()
    {
        toolExchangerCreative = itemExchanger.addItem(ItemExchanger.Types.CREATIVE.ordinal(), Strings.EXCHANGER_CREATIVE);
        toolExchangerBasic = itemExchanger.addItem(ItemExchanger.Types.BASIC.ordinal(), Strings.EXCHANGER_BASIC);
        toolExchangerAdvanced = itemExchanger.addItem(ItemExchanger.Types.ADVANCED.ordinal(), Strings.EXCHANGER_ADVANCED);
    }
}
