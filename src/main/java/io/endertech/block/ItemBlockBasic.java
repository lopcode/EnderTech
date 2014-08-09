package io.endertech.block;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockBasic extends ItemBlock
{
    public ItemBlockBasic(Block block)
    {
        super(block);

        setHasSubtypes(true);
    }

    @Override
    public int getMetadata(int i)
    {
        return i;
    }

    @Override
    public ItemBlock setUnlocalizedName(String name)
    {
        name = "endertech." + name;
        return super.setUnlocalizedName(name);
    }

    @Override
    public String getUnlocalizedName(ItemStack itemStack)
    {
        int metadata = itemStack.getItemDamage();
        return super.getUnlocalizedName(itemStack) + "." + Integer.toString(metadata);
    }

    @Override
    public String getUnlocalizedName()
    {
        return super.getUnlocalizedName() + ".0";
    }
}
