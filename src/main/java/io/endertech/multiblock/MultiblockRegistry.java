package io.endertech.multiblock;

import io.endertech.util.helper.LocalisationHelper;
import io.endertech.util.helper.LogHelper;
import net.minecraft.world.World;
import java.util.HashMap;

/**
 * This is a very static singleton registry class which directs incoming events to sub-objects, which
 * actually manage each individual world's multiblocks.
 *
 * @author Erogenous Beef
 */
public class MultiblockRegistry
{
    // World > WorldRegistry map
    private static HashMap<World, MultiblockWorldRegistry> registries = new HashMap<World, MultiblockWorldRegistry>();

    /**
     * Called before Tile Entities are ticked in the world. Do bookkeeping here.
     *
     * @param world The world being ticked
     */
    public static void tickStart(World world)
    {
        if (registries.containsKey(world))
        {
            MultiblockWorldRegistry registry = registries.get(world);
            registry.tickStart();
        }
    }

    /**
     * Called after Tile Entities are ticked in the world.
     *
     * @param world The world being ticked
     */
    public static void tickEnd(World world)
    {
        if (registries.containsKey(world))
        {
            MultiblockWorldRegistry registry = registries.get(world);
            registry.tickEnd();
        }
    }

    /**
     * Called when the world has finished loading a chunk.
     */
    public static void onChunkLoaded(World world, int chunkX, int chunkZ)
    {
        if (registries.containsKey(world))
        {
            registries.get(world).onChunkLoaded(chunkX, chunkZ);
        }
    }

    /**
     * Register a new part in the system. The part has been created either through user action or via a chunk loading.
     */
    public static void onPartAdded(World world, IMultiblockPart part)
    {
        MultiblockWorldRegistry registry = getOrCreateRegistry(world);
        registry.onPartAdded(part);
    }

    /**
     * Call to remove a part from world lists.
     */
    public static void onPartRemovedFromWorld(World world, IMultiblockPart part)
    {
        if (registries.containsKey(world))
        {
            registries.get(world).onPartRemovedFromWorld(part);
        }

    }


    /**
     * Called whenever a world is unloaded. Unload the relevant registry, if we have one.
     */
    public static void onWorldUnloaded(World world)
    {
        if (registries.containsKey(world))
        {
            registries.get(world).onWorldUnloaded();
            registries.remove(world);
        }
    }

    /**
     * Call to mark a controller as dirty. Dirty means that parts have
     * been added or removed this tick.
     *
     * @param world      The world containing the multiblock
     * @param controller The dirty controller
     */
    public static void addDirtyController(World world, MultiblockControllerBase controller)
    {
        if (registries.containsKey(world))
        {
            registries.get(world).addDirtyController(controller);
        } else
        {
            throw new IllegalArgumentException("Adding a dirty controller to a world that has no registered controllers!");
        }
    }

    /**
     * Call to mark a controller as dead. It should only be marked as dead
     * when it has no connected parts. It will be removed after the next world tick.
     *
     * @param world      The world formerly containing the multiblock
     * @param controller The dead controller
     */
    public static void addDeadController(World world, MultiblockControllerBase controller)
    {
        if (registries.containsKey(world))
        {
            registries.get(world).addDeadController(controller);
        } else
        {
            LogHelper.INSTANCE.warn(LocalisationHelper.localiseString("warning.multiblock.controller.dead_but_world_not_tracked", controller.hashCode(), world));
        }
    }

    /// *** PRIVATE HELPERS *** ///

    private static MultiblockWorldRegistry getOrCreateRegistry(World world)
    {
        if (registries.containsKey(world))
        {
            return registries.get(world);
        } else
        {
            MultiblockWorldRegistry newRegistry = new MultiblockWorldRegistry(world);
            registries.put(world, newRegistry);
            return newRegistry;
        }
    }

}
