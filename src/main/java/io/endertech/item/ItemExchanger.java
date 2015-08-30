package io.endertech.item;

import io.endertech.config.ItemConfig;
import io.endertech.handler.WorldEventHandler;
import io.endertech.util.*;
import io.endertech.util.helper.*;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.ForgeDirection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class ItemExchanger extends ItemExchangerBase implements IKeyHandler, IOutlineDrawer, IItemBlockAffector
{
    public static final int[] RECEIVE = {0, 1 * 2000, 10 * 2000};
    public static final int[] SEND = {10 * 1000000, 10 * 1000000, 10 * 1000000};
    public static final int[] CAPACITY = {0, 1 * 2000000, 10 * 1000000};
    public static Set<Block> creativeOverrideBlocks;
    private static Set<Key.KeyCode> handledKeys;

    static
    {
        handledKeys = new HashSet<Key.KeyCode>();
        handledKeys.add(Key.KeyCode.TOOL_INCREASE);
        handledKeys.add(Key.KeyCode.TOOL_DECREASE);

        creativeOverrideBlocks = new HashSet<Block>();
        creativeOverrideBlocks.add(Blocks.bedrock);
    }

    public ItemExchanger()
    {
        super();
        this.setNoRepair();
    }

    public static boolean isCreative(ItemStack stack)
    {
        if (stack == null) return false;

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

            if (isCreative(stack))
            {
                list.add(LocalisationHelper.localiseString("info.charge", "Infinite"));
            } else
            {
                list.add(LocalisationHelper.localiseString("info.charge", StringHelper.getEnergyString(this.getEnergyStored(stack)) + " / " + StringHelper.getEnergyString(this.getMaxEnergyStored(stack)) + " RF"));
            }

            //if (this.getMaxExtractRate(stack) > 0)
            //    list.add("Send: " + this.getMaxExtractRate(stack) + " RF/t");

            if (this.getMaxReceiveRate(stack) > 0)
            {
                list.add(LocalisationHelper.localiseString("info.charge.receive", StringHelper.getEnergyString(this.getMaxReceiveRate(stack)) + " RF/t"));
            }

            ItemStack pb = getSourceItemStack(stack);
            if (pb == null)
            {
                list.add(LocalisationHelper.localiseString("info.exchanger.source", "None"));
            } else
            {
                list.add(EnumChatFormatting.GREEN + LocalisationHelper.localiseString("info.exchanger.source", pb.getDisplayName()) + EnumChatFormatting.RESET);
            }

            list.add(EnumChatFormatting.GREEN + LocalisationHelper.localiseString("info.exchanger.radius", this.getTargetRadius(stack)));

            list.add(EnumChatFormatting.AQUA + "" + EnumChatFormatting.ITALIC + LocalisationHelper.localiseString("info.exchanger.shift_to_select_source") + EnumChatFormatting.RESET);
        } else
        {
            list.add(StringHelper.holdShiftForDetails);
        }
    }

    @Override
    public boolean onItemUse(ItemStack itemstack, EntityPlayer player, World world, int x, int y, int z, int side, float xFloat, float yFloat, float zFloat)
    {
        LogHelper.debug("Exchanger use on " + x + " " + y + " " + z);

        if (player.isSneaking())
        {
            LogHelper.debug("Shift right click");

            Block source = player.worldObj.getBlock(x, y, z);
            int sourceMeta = player.worldObj.getBlockMetadata(x, y, z);

            if (Exchange.blockSuitableForSelection(new BlockCoord(x, y, z), world, source, sourceMeta, itemstack))
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
            BlockCoord coord = new BlockCoord(x, y, z);
            Block source = player.worldObj.getBlock(x, y, z);
            int meta = player.worldObj.getBlockMetadata(x, y, z);

            if (Exchange.blockSuitableForExchange(coord, world, source, meta, pb, itemstack, 0))
                WorldEventHandler.queueExchangeRequest(player.worldObj, coord, this.getTargetRadius(itemstack), source, meta, pb, player, player.inventory.currentItem, ForgeDirection.getOrientation(side));
            //TeleportHelper.teleportPlayerToDimensionWithCoords((EntityPlayerMP) player, player.dimension, player.posX, player.posY + 10, player.posZ);
        }

        return true;
    }

    @Override
    public int extractEnergy(ItemStack container, int maxExtract, boolean simulate)
    {
        if (isCreative(container))
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

    @Override
    public boolean drawOutline(DrawBlockHighlightEvent event)
    {
        BlockCoord target = new BlockCoord(event.target.blockX, event.target.blockY, event.target.blockZ);
        World world = event.player.worldObj;

        if (event.player.isSneaking())
        {
            Block block = world.getBlock(target.x, target.y, target.z);
            int blockMeta = world.getBlockMetadata(target.x, target.y, target.z);
            if (!Exchange.blockSuitableForSelection(target, world, block, blockMeta, event.player.getCurrentEquippedItem()))
                RenderHelper.renderBlockOutline(event.context, event.player, target, RGBA.Red.setAlpha(0.6f), 2.0f, event.partialTicks);
            else
                RenderHelper.renderBlockOutline(event.context, event.player, target, RGBA.Green.setAlpha(0.6f), 2.0f, event.partialTicks);

            return true;
        }

        if (this.getSourceItemStack(event.currentItem) == null) return false;

        Set<BlockCoord> blocks = this.blocksAffected(event.currentItem, world, target, ForgeDirection.getOrientation(event.target.sideHit));
        if (blocks == null || blocks.size() == 0) return false;

        for (BlockCoord blockCoord : blocks)
        {
            RenderHelper.renderBlockOutline(event.context, event.player, blockCoord, RGBA.White.setAlpha(0.6f), 2.0f, event.partialTicks);
        }

        return true;
    }

    @Override
    public Set<BlockCoord> blocksAffected(ItemStack item, World world, BlockCoord origin, ForgeDirection side)
    {
        if (!(item.getItem() instanceof ItemExchanger)) return null;

        Set<BlockCoord> ret = new LinkedHashSet<BlockCoord>();
        int exchangerRadius = this.getTargetRadius(item) - 1;

        Block targetBlock = world.getBlock(origin.x, origin.y, origin.z);
        int targetMeta = world.getBlockMetadata(origin.x, origin.y, origin.z);

        for (int radius = 0; radius <= exchangerRadius; radius++)
        {
            Set<BlockCoord> squareSet = Geometry.squareSet(radius, new BlockCoord(origin.x, origin.y, origin.z), side);
            for (BlockCoord blockCoord : squareSet)
            {
                if (Exchange.blockSuitableForExchange(blockCoord, world, targetBlock, targetMeta, this.getSourceItemStack(item), item, radius))
                    ret.add(blockCoord);
            }
        }

        return ret;
    }

    public static enum Types
    {
        CREATIVE, REDSTONE, RESONANT;
    }
}
