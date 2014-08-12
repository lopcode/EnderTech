package io.endertech.modules.dev.item;

import io.endertech.block.ItemBlockBasic;
import io.endertech.modules.dev.block.BlockChargePad;
import net.minecraft.block.Block;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;

public class ItemBlockChargePad extends ItemBlockBasic
{
    public ItemBlockChargePad(Block block)
    {
        super(block);
    }

    @Override
    public EnumRarity getRarity(ItemStack par1ItemStack)
    {
        int type = par1ItemStack.getItemDamage();

        if (type == BlockChargePad.Types.CREATIVE.ordinal())
        {
            return EnumRarity.epic;
        } else if (type == BlockChargePad.Types.REDSTONE.ordinal())
        {
            return EnumRarity.uncommon;
        } else if (type == BlockChargePad.Types.RESONANT.ordinal())
        {
            return EnumRarity.rare;
        } else
        {
            return EnumRarity.common;
        }
    }

}
