package io.endertech.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface IKeyHandler
{
    public abstract void handleKey(EntityPlayer player, ItemStack itemStack, byte keyCode);
}
