package io.endertech.items;

import io.endertech.util.Key;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface IKeyHandler
{
    public abstract void handleKey(EntityPlayer player, ItemStack itemStack, Key.KeyCode key);
}
