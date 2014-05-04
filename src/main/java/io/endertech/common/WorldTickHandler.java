package io.endertech.common;

import codechicken.lib.vec.BlockCoord;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;
import io.endertech.config.ItemConfig;
import io.endertech.helper.BlockHelper;
import io.endertech.items.ItemExchanger;
import io.endertech.lib.Reference;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

public class WorldTickHandler implements ITickHandler
{
    // Do exchanges per dimension
    public static Map<Integer, LinkedBlockingQueue<Exchange>> exchanges = new HashMap();

    public static void queueExchangeRequest(World world, BlockCoord coord, int sourceId, int sourceMetadata, int targetId, int targetMetadata, int life, EntityPlayer player, int hotbar_id, Set<BlockCoord> visits)
    {
        if ((Block.blocksList[sourceId] == null) || ((sourceId == targetId) && (sourceMetadata == targetMetadata)))
            return;

        int dimensionId = world.provider.dimensionId;
        LinkedBlockingQueue<Exchange> queue = (LinkedBlockingQueue) exchanges.get(dimensionId);

        if (queue == null)
        {
            exchanges.put(dimensionId, new LinkedBlockingQueue());
            queue = (LinkedBlockingQueue) exchanges.get(dimensionId);
        }

        queue.offer(new Exchange(coord, sourceId, sourceMetadata, targetId, targetMetadata, life, player, hotbar_id, visits));
        world.playSoundAtEntity(player, "mob.endermen.portal", 1.0F, 1.0F);
        exchanges.put(dimensionId, queue);
    }

    @Override
    public void tickStart(EnumSet<TickType> type, Object... tickData)
    {
        //LogHelper.info("Tick start");
    }

    @Override
    public void tickEnd(EnumSet<TickType> type, Object... tickData)
    {
        //LogHelper.info("Tick end");
        WorldServer world = (WorldServer) tickData[0];

        exchangeTick(world);
    }

    private void exchangeTick(WorldServer world)
    {
        int dimensionId = world.provider.dimensionId;
        LinkedBlockingQueue<Exchange> queue = (LinkedBlockingQueue) exchanges.get(dimensionId);

        if (queue == null)
            return;

        int rounds = 2;
        while (rounds > 0)
        {
            Exchange exchange = (Exchange) queue.poll();
            if (exchange == null)
                rounds = 0;
            else
            {
                int blockId = world.getBlockId(exchange.coord.x, exchange.coord.y, exchange.coord.z);
                int blockMetadata = world.getBlockMetadata(exchange.coord.x, exchange.coord.y, exchange.coord.z);
                ItemExchanger exchanger;

                if ((exchange.player.inventory.getStackInSlot(exchange.hotbar_id) != null) && ((exchange.player.inventory.getStackInSlot(exchange.hotbar_id).getItem() instanceof ItemExchanger)))
                {
                    ItemStack exchangerStack = exchange.player.inventory.getStackInSlot(exchange.hotbar_id);
                    exchanger = (ItemExchanger) exchangerStack.getItem();

                    if (((blockId != exchange.targetId) || (blockMetadata != exchange.targetMetadata)) && (exchanger != null) && exchanger.extractEnergy(exchange.player.inventory.getStackInSlot(exchange.hotbar_id), ItemConfig.itemExchangerBlockCost, true) >= ItemConfig.itemExchangerBlockCost && !exchange.visits.contains(exchange.coord))
                    {
                        if (!exchanger.isCreative(exchangerStack))
                            rounds--;

                        world.setBlock(exchange.coord.x, exchange.coord.y, exchange.coord.z, exchange.targetId, exchange.targetMetadata, 3);
                        exchanger.extractEnergy(exchange.player.inventory.getStackInSlot(exchange.hotbar_id), ItemConfig.itemExchangerBlockCost, false);
                        world.playAuxSFX(2001, exchange.coord.x, exchange.coord.y, exchange.coord.z, exchange.sourceId + (exchange.sourceMetadata << 12));
                        exchange.visits.add(exchange.coord);

                        if (exchange.remainingTicks > 0)
                        {
                            // TODO: Make this smarter about sides, replace in a plane
                            for (int xx = -1; xx <= 1; xx++)
                            {
                                for (int yy = -1; yy <= 1; yy++)
                                {
                                    for (int zz = -1; zz <= 1; zz++)
                                    {
                                        if (!(xx == 0 && yy == 0 && zz == 0) && (world.getBlockId(exchange.coord.x + xx, exchange.coord.y + yy, exchange.coord.z + zz) == exchange.sourceId) && (world.getBlockMetadata(exchange.coord.x + xx, exchange.coord.y + yy, exchange.coord.z + zz) == exchange.sourceMetadata) && (BlockHelper.isBlockExposed(world, exchange.coord.x + xx, exchange.coord.y + yy, exchange.coord.z + zz)))
                                        {
                                            queue.offer(new Exchange(new BlockCoord(exchange.coord.x + xx, exchange.coord.y + yy, exchange.coord.z + zz), exchange.sourceId, exchange.sourceMetadata, exchange.targetId, exchange.targetMetadata, exchange.remainingTicks - 1, exchange.player, exchange.hotbar_id, exchange.visits));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        exchanges.put(dimensionId, queue);
    }

    @Override
    public EnumSet<TickType> ticks()
    {
        return EnumSet.of(TickType.WORLD);
    }

    @Override
    public String getLabel()
    {
        return Reference.MOD_ID + "World";
    }
}
