package io.endertech.items;

import io.endertech.common.WorldTickHandler;
import io.endertech.config.ItemConfig;
import io.endertech.helper.BlockHelper;
import io.endertech.helper.KeyHelper;
import io.endertech.helper.LogHelper;
import io.endertech.helper.StringHelper;
import io.endertech.util.BlockCoord;
import io.endertech.util.Key;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ItemExchanger extends ItemETEnergyContainer implements IKeyHandler
{
    private static Set<Key.KeyCode> handledKeys;

    static
    {
        handledKeys = new HashSet<Key.KeyCode>();
        handledKeys.add(Key.KeyCode.TOOL_INCREASE);
        handledKeys.add(Key.KeyCode.TOOL_DECREASE);
    }

    public ItemExchanger()
    {
        super();
    }

    public boolean isCreative(ItemStack stack)
    {
        return stack.getItemDamage() == Types.CREATIVE.ordinal();
    }

    @Override
    public int getMaxEnergyStored(ItemStack stack)
    {
        return CAPACITY[stack.getItemDamage()];
    }

    @Override
    public int getMaxReceiveRate(ItemStack stack)
    {
        return RECEIVE[stack.getItemDamage()];
    }

    @Override
    public int getMaxExtractRate(ItemStack stack)
    {
        return SEND[stack.getItemDamage()];
    }

    @Override
    public boolean isDamaged(ItemStack stack)
    {
        return stack.getItemDamage() != 0;
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean check)
    {
        super.addInformation(stack, player, list, check);

        if (KeyHelper.isShiftDown())
        {
            if (stack.stackTagCompound == null)
            {
                setDefaultTag(stack, 0);
            }

            if (this.isCreative(stack))
            {
                list.add("Charge: Infinite");
            } else
            {
                list.add("Charge: " + StringHelper.getEnergyString(this.getEnergyStored(stack)) + " / " + StringHelper.getEnergyString(this.getMaxEnergyStored(stack)) + " RF");
            }

            //if (this.getMaxExtractRate(stack) > 0)
            //    list.add("Send: " + this.getMaxExtractRate(stack) + " RF/t");

            if (this.getMaxReceiveRate(stack) > 0)
            {
                list.add("Receive: " + StringHelper.getEnergyString(this.getMaxReceiveRate(stack)) + " RF/t");
            }

            ItemStack pb = getSourceItemStack(stack);
            if (pb == null)
            {
                list.add("Source block: None");
            } else
            {
                list.add(EnumChatFormatting.GREEN + "Source block: " + pb.getDisplayName());
            }

            list.add(EnumChatFormatting.GREEN + "Radius: " + this.getTargetRadius(stack));

            list.add(EnumChatFormatting.AQUA + "" + EnumChatFormatting.ITALIC + "Use while sneaking to choose source." + EnumChatFormatting.RESET);
        } else
        {
            list.add(StringHelper.holdShiftForDetails);
        }
    }

    @Override
    public boolean onItemUse(ItemStack itemstack, EntityPlayer player, World world, int x, int y, int z, int par7, float xFloat, float yFloat, float zFloat)
    {
        LogHelper.debug("Exchanger use on " + x + " " + y + " " + z);

        if (player.isSneaking())
        {
            LogHelper.debug("Shift right click");

            Block source = player.worldObj.getBlock(x, y, z);
            int sourceMeta = player.worldObj.getBlockMetadata(x, y, z);

            if (!world.isAirBlock(x, y, z) && world.getTileEntity(x, y, z) == null && !BlockHelper.softBlocks.contains(source))
            {
                LogHelper.debug("Setting source block to " + source.getLocalizedName());
                setSourceBlock(itemstack, new ItemStack(source, 1, sourceMeta));
            } else
            {
                LogHelper.debug("Failed to set source block");
            }

            return false;
        }

        ItemStack pb = getSourceItemStack(itemstack);

        if ((pb != null) && (player.worldObj.getTileEntity(x, y, z) == null) && !player.worldObj.isRemote)
        {
            WorldTickHandler.queueExchangeRequest(player.worldObj, new BlockCoord(x, y, z), player.worldObj.getBlock(x, y, z), player.worldObj.getBlockMetadata(x, y, z), pb, this.getTargetRadius(itemstack) - 1, player, player.inventory.currentItem, new HashSet<BlockCoord>());
        }

        return true;
    }

    @Override
    public int extractEnergy(ItemStack container, int maxExtract, boolean simulate)
    {
        if (this.isCreative(container))
        {
            return maxExtract;
        } else
        {
            return super.extractEnergy(container, maxExtract, simulate);
        }
    }

    public void setSourceBlock(ItemStack stack, ItemStack source)
    {
        if (!stack.hasTagCompound())
        {
            stack.setTagCompound(new NBTTagCompound());
        }

        NBTTagCompound sourceNBT = new NBTTagCompound();
        stack.setTagInfo("source", source.writeToNBT(sourceNBT));
    }

    public ItemStack getSourceItemStack(ItemStack stack)
    {
        boolean hasKey = (stack.hasTagCompound()) && (stack.stackTagCompound.hasKey("source"));
        if (hasKey)
        {
            ItemStack ret = new ItemStack(Blocks.air);
            ret.readFromNBT(stack.stackTagCompound.getCompoundTag("source"));
            return ret;
        }

        return null;
    }

    public void setTargetRadius(ItemStack stack, int radius)
    {
        if (!stack.hasTagCompound())
        {
            stack.setTagCompound(new NBTTagCompound());
        }
        stack.getTagCompound().setInteger("targetRadius", radius);
    }

    public int getTargetRadius(ItemStack stack)
    {
        int radius = 3;

        if (stack.hasTagCompound() && (stack.getTagCompound().hasKey("targetRadius")))
        {
            radius = stack.getTagCompound().getInteger("targetRadius");
        }

        if (radius > ItemConfig.itemExchangerMaxRadius)
        {
            radius = ItemConfig.itemExchangerMaxRadius;
        }

        return radius;
    }

    @Override
    public void handleKey(EntityPlayer player, ItemStack itemStack, Key.KeyCode key)
    {
        LogHelper.debug("Handling key for Exchanger " + key.toString());

        int radius = this.getTargetRadius(itemStack);

        if (key == Key.KeyCode.TOOL_INCREASE)
        {
            if (player.isSneaking())
            {
                radius = ItemConfig.itemExchangerMaxRadius;
            } else
            {
                radius++;
            }

            LogHelper.debug("Tool Increase");
        } else if (key == Key.KeyCode.TOOL_DECREASE)
        {
            if (player.isSneaking())
            {
                radius = 1;
            } else
            {
                radius--;
            }

            LogHelper.debug("Tool Decrease");
        }

        if (radius > ItemConfig.itemExchangerMaxRadius)
        {
            radius = ItemConfig.itemExchangerMaxRadius;
        }

        if (radius < 1)
        {
            radius = 1;
        }

        LogHelper.debug("Setting tool radius to " + radius);
        this.setTargetRadius(itemStack, radius);
    }

    @Override
    public Set<Key.KeyCode> getHandledKeys()
    {
        return ItemExchanger.handledKeys;
    }

    @Override
    public EnumRarity getRarity(ItemStack par1ItemStack)
    {
        int type = par1ItemStack.getItemDamage();

        if (type == Types.CREATIVE.ordinal())
        {
            return EnumRarity.epic;
        } else if (type == Types.REDSTONE.ordinal())
        {
            return EnumRarity.uncommon;
        } else if (type == Types.RESONANT.ordinal())
        {
            return EnumRarity.rare;
        } else
        {
            return EnumRarity.common;
        }
    }

    public static enum Types
    {
        CREATIVE, REDSTONE, RESONANT;
    }

    public static final int[] RECEIVE = {0, 1 * 2000, 10 * 2000};
    public static final int[] SEND = {10 * 1000000, 10 * 1000000, 10 * 1000000};
    public static final int[] CAPACITY = {0, 1 * 2000000, 10 * 1000000};
}
