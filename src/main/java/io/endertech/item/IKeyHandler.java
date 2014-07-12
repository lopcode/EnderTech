package io.endertech.item;

import io.endertech.util.Key;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import java.util.Set;

public interface IKeyHandler
{
    public abstract void handleKey(EntityPlayer player, ItemStack itemStack, Key.KeyCode key);

    public abstract Set<Key.KeyCode> getHandledKeys();
}
