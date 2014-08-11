package io.endertech.util;

import net.minecraft.item.ItemStack;
import java.util.List;

public interface IETWailaProvider
{
    public ItemStack getWailaStack();

    public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip);

    public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip);

    public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip);
}
