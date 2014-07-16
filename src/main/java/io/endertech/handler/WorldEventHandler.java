package io.endertech.handler;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import io.endertech.config.ItemConfig;
import io.endertech.item.ItemExchanger;
import io.endertech.util.BlockCoord;
import io.endertech.util.BlockHelper;
import io.endertech.util.Exchange;
import io.endertech.util.Geometry;
import io.endertech.util.inventory.InventoryHelper;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import java.util.*;

public class WorldEventHandler
{
    // Do exchanges per dimension
    public static Map<Integer, Set<Exchange>> exchanges = new HashMap();

    public static enum ExchangeResult
    {
        FAIL_ENERGY,
        FAIL_MISMATCH,
        FAIL_NO_SOURCE_BLOCKS,
        FAIL_INVENTORY_SPACE,
        FAIL_BLOCK_NOT_REPLACEABLE,
        FAIL_BLOCK_NOT_EXPOSED,
        SUCCESS
    }

    public static void queueExchangeRequest(World world, BlockCoord origin, int radius, Block source, int sourceMeta, ItemStack target, EntityPlayer player, int hotbar_id)
    {
        if (target.isItemEqual(new ItemStack(source, 1, sourceMeta)))
        {
            return;
        }

        int dimensionId = world.provider.dimensionId;
        Set<Exchange> queue = (LinkedHashSet) exchanges.get(dimensionId);

        if (queue == null)
        {
            exchanges.put(dimensionId, new LinkedHashSet());
            queue = exchanges.get(dimensionId);
        }

        queue.add(new Exchange(origin, radius, source, sourceMeta, target, player, hotbar_id));
        world.playSoundAtEntity(player, "mob.endermen.portal", 1.0F, 1.0F);
        exchanges.put(dimensionId, queue);
    }

    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event)
    {
        exchangeTick(event.world);
    }

    private void exchangeTick(World world)
    {
        int dimensionId = world.provider.dimensionId;
        Set<Exchange> queue = exchanges.get(dimensionId);
        if (queue == null || queue.size() == 0) return;

        checkAndPerformExchanges(queue, world);
    }

    private void checkAndPerformExchanges(Set<Exchange> queue, World world)
    {
        Set<Exchange> removals = new HashSet<Exchange>();
        for (Exchange exchange : queue)
        {
            ItemStack exchangerStack = exchange.player.inventory.getStackInSlot(exchange.hotbar_id);
            boolean cullExchange = false;
            if (exchangerStack == null || !(exchangerStack.getItem() instanceof ItemExchanger)) cullExchange = true;

            ItemExchanger exchanger = (ItemExchanger) exchangerStack.getItem();
            if (exchanger == null) cullExchange = true;

            if (cullExchange)
            {
                removals.add(exchange);
                continue;
            }

            exchange.currentRadiusTicks--;
            if (exchange.currentRadiusTicks > 0) continue;

            Set<BlockCoord> blocks = Geometry.squareSet(exchange.currentRadius - 1, exchange.origin);
            boolean stop = false;
            for (BlockCoord blockCoord : blocks)
            {
                if (stop) break;
                ExchangeResult result = checkAndPerformExchange(exchange, exchanger, exchangerStack, world, new BlockCoord(blockCoord.x, exchange.origin.y, blockCoord.z));
                switch (result)
                {
                    case FAIL_ENERGY:
                    case FAIL_NO_SOURCE_BLOCKS:
                    case FAIL_INVENTORY_SPACE:
                        stop = true;
                        removals.add(exchange);
                        break;
                }
            }

            exchange.currentRadius++;
            if (exchange.currentRadius > exchange.radius) removals.add(exchange);
            else exchange.currentRadiusTicks = Exchange.radiusTicksDefault;
        }

        for (Exchange removal : removals)
            queue.remove(removal);
    }

    public static boolean blockSuitableForExchange(BlockCoord blockCoord, World world, Block source, int sourceMeta, ItemStack target)
    {
        Block worldBlock = world.getBlock(blockCoord.x, blockCoord.y, blockCoord.z);
        int worldMeta = world.getBlockMetadata(blockCoord.x, blockCoord.y, blockCoord.z);

        if (!BlockHelper.isBlockExposed(world, blockCoord.x, blockCoord.y, blockCoord.z)) return false;
        if (world.isAirBlock(blockCoord.x, blockCoord.y, blockCoord.z)) return false;

        if (source != worldBlock || sourceMeta != worldMeta) return false;
        if (target.isItemEqual(new ItemStack(source, 1, sourceMeta))) return false;

        return true;
    }

    private ExchangeResult checkAndPerformExchange(Exchange exchange, ItemExchanger exchanger, ItemStack exchangerStack, World world, BlockCoord blockCoord)
    {
        Block block = world.getBlock(blockCoord.x, blockCoord.y, blockCoord.z);

        if (!blockSuitableForExchange(blockCoord, world, exchange.source, exchange.sourceMeta, exchange.target))
            return ExchangeResult.FAIL_BLOCK_NOT_REPLACEABLE;

        if (exchanger.extractEnergy(exchange.player.inventory.getStackInSlot(exchange.hotbar_id), ItemConfig.itemExchangerBlockCost, true) < ItemConfig.itemExchangerBlockCost)
            return ExchangeResult.FAIL_ENERGY;

        int sourceSlot = InventoryHelper.findFirstItemStack(exchange.player.inventory, exchange.target);

        if (sourceSlot <= 0 && !exchanger.isCreative(exchangerStack)) return ExchangeResult.FAIL_NO_SOURCE_BLOCKS;

        if (!exchanger.isCreative(exchangerStack))
        {
            ArrayList<ItemStack> droppedItems = block.getDrops(exchange.player.worldObj, blockCoord.x, blockCoord.y, blockCoord.z, exchange.sourceMeta, 0);
            boolean didPutItemsInInventory = InventoryHelper.checkAndPutItemStacksInToInventory(exchange.player.inventory, droppedItems);
            if (didPutItemsInInventory)
            {
                InventoryHelper.consumeItem(exchange.player.inventory, sourceSlot);
                performExchange(exchange, blockCoord, exchanger, world);
            } else return ExchangeResult.FAIL_INVENTORY_SPACE;
        } else performExchange(exchange, blockCoord, exchanger, world);

        return ExchangeResult.SUCCESS;
    }

    private void performExchange(Exchange exchange, BlockCoord blockCoord, ItemExchanger exchanger, World world)
    {
        world.setBlock(blockCoord.x, blockCoord.y, blockCoord.z, Block.getBlockFromItem(exchange.target.getItem()), exchange.target.getItemDamage(), 3);
        exchanger.extractEnergy(exchange.player.inventory.getStackInSlot(exchange.hotbar_id), ItemConfig.itemExchangerBlockCost, false);
        world.playAuxSFX(2001, blockCoord.x, blockCoord.y, blockCoord.z, Block.getIdFromBlock(exchange.source) + (exchange.sourceMeta << 12));
    }
}
