package io.endertech.multiblock.item;

import io.endertech.block.ETBlocks;
import io.endertech.block.ItemBlockBasic;
import io.endertech.multiblock.block.BlockTankPart;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

public class ItemBlockTankPart extends ItemBlockBasic
{
    public ItemBlockTankPart(Block block)
    {
        super(block);
        this.setMaxDamage(0);
    }

    @Override
    public int getMetadata(int meta)
    {
        return meta;
    }

    @Override
    public String getUnlocalizedName(ItemStack itemstack)
    {
        int meta = 0;
        int damage = itemstack.getItemDamage();

        if (BlockTankPart.isFrame(damage)) meta = 0;
        else if (BlockTankPart.isValve(damage)) meta = 1;

        return ETBlocks.blockTankPart.getUnlocalizedName() + "." + meta;
    }
}
