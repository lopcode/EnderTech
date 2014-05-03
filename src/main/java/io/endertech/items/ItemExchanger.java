package io.endertech.items;

import codechicken.lib.vec.BlockCoord;
import io.endertech.EnderTech;
import io.endertech.client.KeyBindingHandler;
import io.endertech.common.WorldTickHandler;
import io.endertech.helper.BlockHelper;
import io.endertech.helper.LogHelper;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import org.lwjgl.input.Keyboard;

import java.util.HashSet;
import java.util.List;

public class ItemExchanger extends ItemETEnergyContainer implements IKeyHandler
{
    public static final int MIN_RADIUS = 0;
    public static final int MAX_RADIUS = 7;

    public ItemExchanger(int itemID)
    {
        super(itemID);
        this.setMaxStackSize(1);
        this.setCreativeTab(EnderTech.tabItems);
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

        if (Keyboard.isKeyDown(42) || (Keyboard.isKeyDown(54)))
        {
            if (stack.stackTagCompound == null)
            {
                setDefaultTag(stack, 0);
            }

            if (stack.getItemDamage() == Types.CREATIVE.ordinal())
                list.add("Charge: Infinite");
            else
                list.add("Charge: " + (int) (this.getEnergyStored(stack) / 1000.0) + "k / " + (int) (this.getMaxEnergyStored(stack) / 1000.0) + "k RF");

            //if (this.getMaxExtractRate(stack) > 0)
            //    list.add("Send: " + this.getMaxExtractRate(stack) + " RF/t");

            if (this.getMaxReceiveRate(stack) > 0)
                list.add("Receive: " + this.getMaxReceiveRate(stack) + " RF/t");

            ItemStack pb = getSourceBlock(stack);
            if (pb == null)
                list.add("Source block: None");
            else
                list.add("Source block: " + pb.getDisplayName());

            list.add("Radius: " + this.getTargetRadius(stack));
        } else
        {
            list.add("Hold Shift for info");
        }
    }

    @Override
    public boolean onItemUse(ItemStack itemstack, EntityPlayer player, World world, int x, int y, int z, int par7, float xFloat, float yFloat, float zFloat)
    {
        LogHelper.info("Item use on " + x + " " + y + " " + z);

        if (player.isSneaking())
        {
            LogHelper.info("Shift right click");

            int sourceId = player.worldObj.getBlockId(x, y, z);
            int sourceMetadata = player.worldObj.getBlockMetadata(x, y, z);

            if (sourceId > 0 && world.getBlockTileEntity(x, y, z) == null && !BlockHelper.softBlocks.contains(Block.blocksList[sourceId]))
            {
                LogHelper.info("Setting source block to " + Block.blocksList[sourceId].getLocalizedName());
                setSourceBlock(itemstack, sourceId, sourceMetadata);
            } else
            {
                LogHelper.info("Failed to set source block");
            }

            return false;
        }

        ItemStack pb = getSourceBlock(itemstack);

        if ((pb != null) && (player.worldObj.getBlockTileEntity(x, y, z) == null) && !player.worldObj.isRemote)
        {
            WorldTickHandler.queueExchangeRequest(player.worldObj, new BlockCoord(x, y, z), player.worldObj.getBlockId(x, y, z), player.worldObj.getBlockMetadata(x, y, z), pb.itemID, pb.getItemDamage(), this.getTargetRadius(itemstack), player, player.inventory.currentItem, new HashSet<BlockCoord>());
        }

        return true;
    }

    @Override
    public int extractEnergy(ItemStack container, int maxExtract, boolean simulate)
    {
        if (container.getItemDamage() == Types.CREATIVE.ordinal())
            return maxExtract;
        else
            return super.extractEnergy(container, maxExtract, simulate);
    }

    public void setSourceBlock(ItemStack stack, int sourceId, int sourceMetadata)
    {
        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
        stack.getTagCompound().setInteger("sourceId", sourceId);
        stack.getTagCompound().setInteger("sourceMetadata", sourceMetadata);
    }

    public ItemStack getSourceBlock(ItemStack stack)
    {
        return (stack.hasTagCompound()) && (stack.getTagCompound().hasKey("sourceId")) && (stack.getTagCompound().hasKey("sourceMetadata")) ? new ItemStack(stack.getTagCompound().getInteger("sourceId"), 1, stack.getTagCompound().getInteger("sourceMetadata")) : null;
    }

    public void setTargetRadius(ItemStack stack, int radius)
    {
        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
        stack.getTagCompound().setInteger("targetRadius", radius);
    }

    public int getTargetRadius(ItemStack stack)
    {
        int radius = 3;
        if (stack.hasTagCompound() && (stack.getTagCompound().hasKey("targetRadius")))
            return stack.getTagCompound().getInteger("targetRadius");
        else
            return radius;
    }

    @Override
    public void handleKey(EntityPlayer player, ItemStack itemStack, String keyDescription)
    {
        LogHelper.info("Handling key for Exchanger");

        int radius = this.getTargetRadius(itemStack);

        if (keyDescription.equals(KeyBindingHandler.keyToolIncrease.keyDescription))
        {
            radius++;

            LogHelper.info("Tool Increase");
        } else if (keyDescription.equals(KeyBindingHandler.keyToolDecrease.keyDescription))
        {
            radius--;

            LogHelper.info("Tool Decrease");
        }

        if (radius > ItemExchanger.MAX_RADIUS)
            radius = ItemExchanger.MAX_RADIUS;

        if (radius < ItemExchanger.MIN_RADIUS)
            radius = ItemExchanger.MIN_RADIUS;

        LogHelper.info("Setting tool radius to " + radius);
        this.setTargetRadius(itemStack, radius);
    }

    public static enum Types
    {
        CREATIVE, BASIC, ADVANCED;

        private Types()
        {
        }
    }

    public static final int[] RECEIVE = {0, 1 * 2000, 10 * 2000};
    public static final int[] SEND = {10 * 1000000, 10 * 1000000, 10 * 1000000};
    public static final int[] CAPACITY = {0, 1 * 2000000, 10 * 1000000};
}
