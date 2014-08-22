package io.endertech.multiblock;

import io.endertech.network.PacketETBase;
import io.endertech.util.BlockCoord;
import io.endertech.util.helper.LocalisationHelper;
import io.endertech.util.helper.LogHelper;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.chunk.IChunkProvider;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Base logic class for Multiblock-connected tile entities. Most multiblock machines
 * should derive from this and implement their game logic in certain abstract methods.
 */
public abstract class MultiblockTileEntityBase extends IMultiblockPart
{
    private MultiblockControllerBase controller;
    private boolean visited;

    private boolean saveMultiblockData;
    protected PacketETBase cachedMultiblockPacket;
    protected NBTTagCompound cachedMultiblockNBT;
    private boolean paused;

    public MultiblockTileEntityBase()
    {
        super();
        controller = null;
        visited = false;
        saveMultiblockData = false;
        paused = false;
        cachedMultiblockPacket = null;
        cachedMultiblockNBT = null;
    }

    ///// Multiblock Connection Base Logic
    @Override
    public Set<MultiblockControllerBase> attachToNeighbors()
    {
        Set<MultiblockControllerBase> controllers = null;
        MultiblockControllerBase bestController = null;

        // Look for a compatible controller in our neighboring parts.
        IMultiblockPart[] partsToCheck = getNeighboringParts();
        for (IMultiblockPart neighborPart : partsToCheck)
        {
            if (neighborPart.isConnected())
            {
                MultiblockControllerBase candidate = neighborPart.getMultiblockController();
                if (!candidate.getClass().equals(this.getMultiblockControllerType()))
                {
                    // Skip multiblocks with incompatible types
                    continue;
                }

                if (controllers == null)
                {
                    controllers = new HashSet<MultiblockControllerBase>();
                    bestController = candidate;
                } else if (!controllers.contains(candidate) && candidate.shouldConsume(bestController))
                {
                    bestController = candidate;
                }

                controllers.add(candidate);
            }
        }

        // If we've located a valid neighboring controller, attach to it.
        if (bestController != null)
        {
            // attachBlock will call onAttached, which will set the controller.
            this.controller = bestController;
            bestController.attachBlock(this);
        }

        return controllers;
    }

    @Override
    public void assertDetached()
    {
        if (this.controller != null)
        {
            LogHelper.info(LocalisationHelper.localiseString("assert.multiblock.part.detached", xCoord, yCoord, zCoord));
            this.controller = null;
        }
    }

    ///// Overrides from base TileEntity methods

    @Override
    public void readFromNBT(NBTTagCompound data)
    {
        super.readFromNBT(data);

        // We can't directly initialize a multiblock controller yet, so we cache the data here until
        // we receive a validate() call, which creates the controller and hands off the cached data.
        if (data.hasKey("multiblockData"))
        {
            this.cachedMultiblockNBT = data.getCompoundTag("multiblockData");
            //            LogHelper.info("Reading from NBT and caching multiblock NBT");
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound data)
    {
        super.writeToNBT(data);

        if (isMultiblockSaveDelegate() && isConnected())
        {
            NBTTagCompound multiblockData = new NBTTagCompound();
            this.controller.writeToNBT(multiblockData);
            data.setTag("multiblockData", multiblockData);
            //            LogHelper.info("Writing multiblock to NBT");
        }
    }

    /**
     * Generally, TileEntities that are part of a multiblock should not subscribe to updates
     * from the main game loop. Instead, you should have lists of TileEntities which need to
     * be notified during an update() in your Controller and perform callbacks from there.
     *
     * @see net.minecraft.tileentity.TileEntity#canUpdate()
     */
    @Override
    public boolean canUpdate() { return false; }

    /**
     * Called when a block is removed by game actions, such as a player breaking the block
     * or the block being changed into another block.
     *
     * @see net.minecraft.tileentity.TileEntity#invalidate()
     */
    @Override
    public void invalidate()
    {
        super.invalidate();
        detachSelf(false);
    }

    /**
     * Called from Minecraft's tile entity loop, after all tile entities have been ticked,
     * as the chunk in which this tile entity is contained is unloading.
     * Happens before the Forge TickEnd event.
     *
     * @see net.minecraft.tileentity.TileEntity#onChunkUnload()
     */
    @Override
    public void onChunkUnload()
    {
        super.onChunkUnload();
        detachSelf(true);
    }

    /**
     * This is called when a block is being marked as valid by the chunk, but has not yet fully
     * been placed into the world's TileEntity cache. this.worldObj, xCoord, yCoord and zCoord have
     * been initialized, but any attempts to read data about the world can cause infinite loops -
     * if you call getTileEntity on this TileEntity's coordinate from within validate(), you will
     * blow your call stack.
     * <p/>
     * TL;DR: Here there be dragons.
     *
     * @see net.minecraft.tileentity.TileEntity#validate()
     */
    @Override
    public void validate()
    {
        super.validate();
        MultiblockRegistry.onPartAdded(this.worldObj, this);
    }

    @Override
    public boolean hasMultiblockNBTCache()
    {
        return this.cachedMultiblockNBT != null;
    }

    @Override
    public boolean hasMultiblockMessageCache()
    {
        return this.cachedMultiblockPacket != null;
    }

    @Override
    public NBTTagCompound getMultiblockNBTCache()
    {
        return this.cachedMultiblockNBT;
    }

    @Override
    public PacketETBase getMultiblockPacketCache()
    {
        return this.cachedMultiblockPacket;
    }


    @Override
    public void onMultiblockDataAssimilated()
    {
        this.cachedMultiblockNBT = null;
        this.cachedMultiblockPacket = null;
    }

    ///// Game logic callbacks (IMultiblockPart)

    @Override
    public abstract void onMachineAssembled(MultiblockControllerBase multiblockControllerBase);

    @Override
    public abstract void onMachineBroken();

    @Override
    public abstract void onMachineActivated();

    @Override
    public abstract void onMachineDeactivated();

    ///// Miscellaneous multiblock-assembly callbacks and support methods (IMultiblockPart)

    @Override
    public boolean isConnected()
    {
        return (controller != null);
    }

    @Override
    public MultiblockControllerBase getMultiblockController()
    {
        return controller;
    }

    @Override
    public BlockCoord getWorldLocation()
    {
        return new BlockCoord(this.xCoord, this.yCoord, this.zCoord);
    }

    @Override
    public void becomeMultiblockSaveDelegate()
    {
        this.saveMultiblockData = true;
    }

    @Override
    public void forfeitMultiblockSaveDelegate()
    {
        this.saveMultiblockData = false;
    }

    @Override
    public boolean isMultiblockSaveDelegate() { return this.saveMultiblockData; }

    @Override
    public void setUnvisited()
    {
        this.visited = false;
    }

    @Override
    public void setVisited()
    {
        this.visited = true;
    }

    @Override
    public boolean isVisited()
    {
        return this.visited;
    }

    @Override
    public void onAssimilated(MultiblockControllerBase newController)
    {
        assert (this.controller != newController);
        this.controller = newController;
    }

    @Override
    public void onAttached(MultiblockControllerBase newController)
    {
        this.controller = newController;
    }

    @Override
    public void onDetached(MultiblockControllerBase oldController)
    {
        this.controller = null;
    }

    @Override
    public abstract MultiblockControllerBase createNewMultiblock();

    @Override
    public IMultiblockPart[] getNeighboringParts()
    {
        BlockCoord[] neighbors = new BlockCoord[] {new BlockCoord(this.xCoord - 1, this.yCoord, this.zCoord), new BlockCoord(this.xCoord, this.yCoord - 1, this.zCoord), new BlockCoord(this.xCoord, this.yCoord, this.zCoord - 1), new BlockCoord(this.xCoord, this.yCoord, this.zCoord + 1), new BlockCoord(this.xCoord, this.yCoord + 1, this.zCoord), new BlockCoord(this.xCoord + 1, this.yCoord, this.zCoord)};

        TileEntity te;
        List<IMultiblockPart> neighborParts = new ArrayList<IMultiblockPart>();
        IChunkProvider chunkProvider = worldObj.getChunkProvider();
        for (BlockCoord neighbor : neighbors)
        {
            if (!chunkProvider.chunkExists(neighbor.getChunkX(), neighbor.getChunkZ()))
            {
                // Chunk not loaded, skip it.
                continue;
            }

            te = this.worldObj.getTileEntity(neighbor.x, neighbor.y, neighbor.z);
            if (te instanceof IMultiblockPart)
            {
                neighborParts.add((IMultiblockPart) te);
            }
        }
        IMultiblockPart[] tmp = new IMultiblockPart[neighborParts.size()];
        return neighborParts.toArray(tmp);
    }

    @Override
    public void onOrphaned(MultiblockControllerBase controller, int oldSize, int newSize)
    {
        worldObj.markTileEntityChunkModified(xCoord, yCoord, zCoord, this);
    }

    ///// Private/Protected Logic Helpers
    /*
     * Detaches this block from its controller. Calls detachBlock() and clears the controller member.
	 */
    protected void detachSelf(boolean chunkUnloading)
    {
        if (this.controller != null)
        {
            // Clean part out of controller
            this.controller.detachBlock(this, chunkUnloading);

            // The above should call onDetached, but, just in case...
            this.controller = null;
        }

        // Clean part out of lists in the registry
        MultiblockRegistry.onPartRemovedFromWorld(worldObj, this);
    }
}
