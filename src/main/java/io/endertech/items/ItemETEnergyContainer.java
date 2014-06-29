package io.endertech.items;

import cofh.api.energy.IEnergyContainerItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Reference implementation of {@link cofh.api.energy.IEnergyContainerItem}. Use/extend this or implement your own.
 * Extended by Drayshak for EnderTech
 *
 * @author King Lemming
 * @author Drayshak
 */
public class ItemETEnergyContainer extends ItemETBase implements IEnergyContainerItem
{
    private int capacity;
    private int maxReceive;
    private int maxExtract;

    public ItemETEnergyContainer()
    {
        super();
    }

    public ItemETEnergyContainer(int capacity, int maxReceive, int maxExtract)
    {
        super();
        this.capacity = capacity;
        this.maxReceive = maxReceive;
        this.maxExtract = maxExtract;

        this.setMaxDamage(1 + this.capacity);
    }

    public ItemETEnergyContainer setCapacity(int capacity)
    {
        this.capacity = capacity;
        return this;
    }

    /* IEnergyContainerItem */
    @Override
    public int receiveEnergy(ItemStack container, int maxReceive, boolean simulate)
    {
        if (container.stackTagCompound == null)
        {
            container.stackTagCompound = new NBTTagCompound();
        }

        int energy = container.stackTagCompound.getInteger("Energy");
        int energyReceived = Math.min(this.getMaxEnergyStored(container) - energy, Math.min(this.getMaxReceiveRate(container), maxReceive));

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
        if (container.stackTagCompound == null || !container.stackTagCompound.hasKey("Energy") || this.getMaxExtractRate(container) == 0)
        {
            return 0;
        }

        int energy = container.stackTagCompound.getInteger("Energy");
        int energyExtracted = Math.min(energy, Math.min(this.getMaxExtractRate(container), maxExtract));

        if (!simulate)
        {
            energy -= energyExtracted;
            container.stackTagCompound.setInteger("Energy", energy);
        }

        return energyExtracted;
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
    public boolean isDamaged(ItemStack stack)
    {
        return true;
    }

    @Override
    public int getMaxEnergyStored(ItemStack stack)
    {
        return capacity;
    }

    public int getMaxReceiveRate(ItemStack stack)
    {
        return maxReceive;
    }

    public int getMaxExtractRate(ItemStack stack)
    {
        return maxExtract;
    }

    @Override
    public int getDisplayDamage(ItemStack stack)
    {
        if (stack.stackTagCompound == null)
        {
            return 1 + this.getMaxEnergyStored(stack);
        }

        return 1 + this.getMaxEnergyStored(stack) - this.getEnergyStored(stack);
    }

    @Override
    public int getMaxDamage(ItemStack stack)
    {
        return 1 + this.getMaxEnergyStored(stack);
    }

    public static ItemStack setDefaultTag(ItemStack container, int energy)
    {
        container.setTagCompound(new NBTTagCompound());
        container.stackTagCompound.setInteger("Energy", energy);

        return container;
    }
}
