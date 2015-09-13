package io.endertech.item;

import cofh.api.energy.IEnergyContainerItem;
import io.endertech.block.BlockConduit;
import io.endertech.block.ItemBlockBasic;
import io.endertech.tile.TileConduit;
import io.endertech.util.helper.KeyHelper;
import io.endertech.util.helper.LocalisationHelper;
import io.endertech.util.helper.StringHelper;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import java.util.List;

public class ItemBlockConduit extends ItemBlockBasic implements IEnergyContainerItem
{
    public ItemBlockConduit(Block block)
    {
        super(block);

        this.setMaxStackSize(1);
    }

    @Override
    public EnumRarity getRarity(ItemStack par1ItemStack)
    {
        int type = par1ItemStack.getItemDamage();

        if (type == BlockConduit.Types.CREATIVE.ordinal())
        {
            return EnumRarity.epic;
        } else if (type == BlockConduit.Types.REDSTONE.ordinal())
        {
            return EnumRarity.uncommon;
        } else if (type == BlockConduit.Types.RESONANT.ordinal())
        {
            return EnumRarity.rare;
        } else
        {
            return EnumRarity.common;
        }
    }

    public void checkAndSetDefaultTag(ItemStack stack)
    {
        if (stack.stackTagCompound == null)
        {
            stack.setTagCompound(new NBTTagCompound());
            TileConduit.writeDefaultTag(stack.stackTagCompound);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean check)
    {
        super.addInformation(stack, player, list, check);

        if (KeyHelper.isShiftDown())
        {
            this.checkAndSetDefaultTag(stack);

            if (isCreative(stack))
            {
                list.add(LocalisationHelper.localiseString("info.charge", "Infinite"));
            } else
            {
                list.add(LocalisationHelper.localiseString("info.charge", StringHelper.getEnergyString(this.getEnergyStored(stack)) + " / " + StringHelper.getEnergyString(this.getMaxEnergyStored(stack)) + " RF"));
            }

            if (!isCreative(stack))
            {
                list.add(LocalisationHelper.localiseString("info.charge.receive", StringHelper.getEnergyString(TileConduit.RECEIVE[stack.getItemDamage()]) + " RF/t"));
            }

            list.add("Item groups per tick: " + TileConduit.GROUPS_PER_TICK[stack.getItemDamage()]);
        } else
        {
            list.add(StringHelper.holdShiftForDetails);
        }
    }

    private boolean isCreative(ItemStack stack)
    {
        return stack.getItemDamage() == 0;
    }

    @Override
    public int receiveEnergy(ItemStack container, int maxReceive, boolean simulate)
    {
        this.checkAndSetDefaultTag(container);

        int energy = container.stackTagCompound.getInteger("Energy");
        int energyReceived = Math.min(this.getMaxEnergyStored(container) - energy, Math.min(TileConduit.RECEIVE[container.getItemDamage()], maxReceive));

        if (!simulate)
        {
            energy += energyReceived;
            container.stackTagCompound.setInteger("Energy", energy);
        }

        return energyReceived;
    }


    @Override
    public int extractEnergy(ItemStack container, int maxExtract, boolean simulate)
    {
        return 0;
    }

    @Override
    public int getEnergyStored(ItemStack container)
    {
        if (container.stackTagCompound == null || !container.stackTagCompound.hasKey("Energy"))
        {
            return 0;
        }
        return container.stackTagCompound.getInteger("Energy");
    }

    @Override
    public int getMaxEnergyStored(ItemStack container)
    {
        return TileConduit.CAPACITY[container.getItemDamage()];
    }
}
