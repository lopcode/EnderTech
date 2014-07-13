package io.endertech.handler;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import io.endertech.config.ItemConfig;
import io.endertech.item.ItemExchanger;
import io.endertech.util.BlockCoord;
import io.endertech.util.BlockHelper;
import io.endertech.util.Exchange;
import io.endertech.util.inventory.InventoryHelper;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

public class WorldEventHandler
{
    // Do exchanges per dimension
    public static Map<Integer, LinkedBlockingQueue<Exchange>> exchanges = new HashMap();

    public static void queueExchangeRequest(World world, BlockCoord coord, Block source, int sourceMeta, ItemStack target, int life, EntityPlayer player, int hotbar_id, Set<BlockCoord> visits)
    {
        if (target.isItemEqual(new ItemStack(source, 1, sourceMeta)))
        {
            return;
        }

        int dimensionId = world.provider.dimensionId;
        LinkedBlockingQueue<Exchange> queue = (LinkedBlockingQueue) exchanges.get(dimensionId);

        if (queue == null)
        {
            exchanges.put(dimensionId, new LinkedBlockingQueue());
            queue = (LinkedBlockingQueue) exchanges.get(dimensionId);
        }

        queue.offer(new Exchange(coord, source, sourceMeta, target, life, player, hotbar_id, visits));
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
        LinkedBlockingQueue<Exchange> queue = (LinkedBlockingQueue) exchanges.get(dimensionId);
        if (queue == null) return;

        checkAndPerformExchange(queue, world);
    }

    private void checkAndPerformExchange(LinkedBlockingQueue<Exchange> queue, World world)
    {
        Exchange exchange = queue.poll();
        if (exchange == null) return;

        ItemStack exchangerStack = exchange.player.inventory.getStackInSlot(exchange.hotbar_id);

        if (exchangerStack == null) return;

        if (!(exchangerStack.getItem() instanceof ItemExchanger)) return;

        ItemExchanger exchanger = (ItemExchanger) exchangerStack.getItem();

        if (exchanger == null) return;

        Block block = world.getBlock(exchange.coord.x, exchange.coord.y, exchange.coord.z);
        int blockMeta = world.getBlockMetadata(exchange.coord.x, exchange.coord.y, exchange.coord.z);

        if (exchange.target.isItemEqual(new ItemStack(block, 1, blockMeta))) return;

        if (exchange.visits.contains(exchange.coord)) return;

        if (exchanger.extractEnergy(exchange.player.inventory.getStackInSlot(exchange.hotbar_id), ItemConfig.itemExchangerBlockCost, true) < ItemConfig.itemExchangerBlockCost)
            return;

        int sourceSlot = InventoryHelper.findFirstItemStack(exchange.player.inventory, exchange.target);

        if (sourceSlot <= 0 && !exchanger.isCreative(exchangerStack)) return;

        if (!exchanger.isCreative(exchangerStack))
        {
            ArrayList<ItemStack> droppedItems = block.getDrops(exchange.player.worldObj, exchange.coord.x, exchange.coord.y, exchange.coord.z, exchange.sourceMeta, 0);
            boolean didPutItemsInInventory = InventoryHelper.checkAndPutItemStacksInToInventory(exchange.player.inventory, droppedItems);
            if (didPutItemsInInventory)
            {
                InventoryHelper.consumeItem(exchange.player.inventory, sourceSlot);
                performExchangeAndPropagate(queue, exchange, exchanger, world);
            }
        } else performExchangeAndPropagate(queue, exchange, exchanger, world);
    }

    private void performExchangeAndPropagate(LinkedBlockingQueue<Exchange> queue, Exchange exchange, ItemExchanger exchanger, World world)
    {
        performExchange(exchange, exchanger, world);
        if (exchange.remainingTicks > 0) propagateExchange(queue, world, exchange);
    }

    private void performExchange(Exchange exchange, ItemExchanger exchanger, World world)
    {
        world.setBlock(exchange.coord.x, exchange.coord.y, exchange.coord.z, Block.getBlockFromItem(exchange.target.getItem()), exchange.target.getItemDamage(), 3);
        exchanger.extractEnergy(exchange.player.inventory.getStackInSlot(exchange.hotbar_id), ItemConfig.itemExchangerBlockCost, false);
        world.playAuxSFX(2001, exchange.coord.x, exchange.coord.y, exchange.coord.z, Block.getIdFromBlock(exchange.source) + (exchange.sourceMeta << 12));
        exchange.visits.add(exchange.coord);
    }

    private void propagateExchange(LinkedBlockingQueue<Exchange> queue, World world, Exchange exchange)
    {
        for (int xx = -1; xx <= 1; xx++)
        {
            for (int yy = -1; yy <= 1; yy++)
            {
                for (int zz = -1; zz <= 1; zz++)
                {
                    if (!(xx == 0 && yy == 0 && zz == 0) && (world.getBlock(exchange.coord.x + xx, exchange.coord.y + yy, exchange.coord.z + zz) == exchange.source) && (world.getBlockMetadata(exchange.coord.x + xx, exchange.coord.y + yy, exchange.coord.z + zz) == exchange.sourceMeta) && (BlockHelper.isBlockExposed(world, exchange.coord.x + xx, exchange.coord.y + yy, exchange.coord.z + zz)))
                    {
                        queue.offer(new Exchange(new BlockCoord(exchange.coord.x + xx, exchange.coord.y + yy, exchange.coord.z + zz), exchange.source, exchange.sourceMeta, exchange.target, exchange.remainingTicks - 1, exchange.player, exchange.hotbar_id, exchange.visits));
                    }
                }
            }
        }
    }
}
