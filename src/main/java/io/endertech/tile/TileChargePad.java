package io.endertech.tile;

import cofh.api.energy.IEnergyContainerItem;
import cofh.api.energy.IEnergyHandler;
import cofh.api.tileentity.IReconfigurableFacing;
import cofh.lib.util.helpers.ServerHelper;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.endertech.block.BlockChargePad;
import io.endertech.config.GeneralConfig;
import io.endertech.fx.EntityChargePadFX;
import io.endertech.network.PacketETBase;
import io.endertech.reference.Strings;
import io.endertech.util.IOutlineDrawer;
import io.endertech.util.RGBA;
import io.endertech.util.helper.RenderHelper;
import io.endertech.util.helper.StringHelper;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.common.util.ForgeDirection;
import java.util.*;

public class TileChargePad extends TileET implements IReconfigurableFacing, IEnergyHandler, IOutlineDrawer
{
    public static final short TICKS_PER_UPDATE = 20;
    public short ticksSinceLastUpdate = 0;
    public boolean isActive = false;
    public int storedEnergy = 0;
    public int sentPower = 0;

    public static void init()
    {
        GameRegistry.registerTileEntity(TileChargePad.class, "tile." + Strings.Blocks.CHARGE_PAD);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbtTagCompound)
    {
        super.readFromNBT(nbtTagCompound);

        if (nbtTagCompound.hasKey("Energy")) this.storedEnergy = nbtTagCompound.getInteger("Energy");
    }

    @Override
    public void writeToNBT(NBTTagCompound nbtTagCompound)
    {
        super.writeToNBT(nbtTagCompound);

        nbtTagCompound.setInteger("Energy", this.storedEnergy);
    }

    @Override
    public String toString()
    {
        return "Charged plane: position " + this.xCoord + ", " + this.yCoord + ", " + this.zCoord;
    }

    @Override
    public PacketETBase getPacket()
    {
        PacketETBase packet = super.getPacket();
        packet.addBool(this.isActive);
        packet.addInt(this.storedEnergy);
        packet.addInt(this.sentPower);

        return packet;
    }

    @Override
    public void handleTilePacket(PacketETBase tilePacket, boolean isServer)
    {
        super.handleTilePacket(tilePacket, isServer);

        boolean isActive = tilePacket.getBool();
        int storedEnergy = tilePacket.getInt();
        int sentPower = tilePacket.getInt();

        if (!isServer)
        {
            this.isActive = isActive;
            this.storedEnergy = storedEnergy;
            this.sentPower = sentPower;
        }
    }

    public AxisAlignedBB getAABBInFront(int distance)
    {
        ForgeDirection orientation = this.getOrientation();
        return this.getRenderBoundingBox().expand(Math.abs(orientation.offsetX) * (distance - 1), Math.abs(orientation.offsetY) * (distance - 1), Math.abs(orientation.offsetZ) * (distance - 1)).offset(orientation.offsetX, orientation.offsetY, orientation.offsetZ);
    }

    public Set<ItemStack> chargeableItemsInInventory(ItemStack[] itemStacks)
    {
        Set<ItemStack> itemsToCharge = new HashSet<ItemStack>();

        for (ItemStack itemStack : itemStacks)
        {
            if (itemStack == null) continue;

            Item item = itemStack.getItem();
            if (item != null && item instanceof IEnergyContainerItem)
            {
                IEnergyContainerItem chargeableItem = (IEnergyContainerItem) item;
                if (chargeableItem.receiveEnergy(itemStack, 1, true) == 1) itemsToCharge.add(itemStack);
            }
        }

        return itemsToCharge;
    }

    public List<Entity> getChargeableEntitesInAABB(AxisAlignedBB aabb)
    {
        List<Entity> chargeableEntitiesInRange = this.worldObj.getEntitiesWithinAABB(EntityPlayer.class, aabb);
        List<EntityItem> chargeableItemsInRange = this.worldObj.getEntitiesWithinAABB(EntityItem.class, aabb);
        for (EntityItem entityItem : chargeableItemsInRange)
        {
            ItemStack itemStack = entityItem.getEntityItem();
            if (itemStack != null)
            {
                Item item = itemStack.getItem();
                if (item != null)
                {
                    if (item instanceof IEnergyContainerItem) chargeableEntitiesInRange.add(entityItem);
                }
            }
        }

        return chargeableEntitiesInRange;
    }

    public double calculateEfficiencyForEntity(Entity entity)
    {
        double xDiff = Math.abs(((this.xCoord + 0.5D) - entity.posX) * orientation.offsetX);
        double yDiff = Math.abs(((this.yCoord + 0.5D) - entity.posY) * orientation.offsetY);
        double zDiff = Math.abs(((this.zCoord + 0.5D) - entity.posZ) * orientation.offsetZ);
        double distance = xDiff + yDiff + zDiff - 0.5;
        if (orientation == ForgeDirection.DOWN) distance -= 1.5;
        if (distance < 0.3) distance = 0;
        if (distance > 1.5) distance = 1.5;

        double efficiency = ((2.0 - distance) / 2.0);
        if (distance < 0.9) efficiency += 0.2;
        efficiency = Math.max(0.5, efficiency);
        efficiency = Math.min(1, efficiency);

        return efficiency;
    }

    public int chargeItemsGivenEntity(Entity entity, double maxCharge, int meta)
    {
        LinkedList<ItemStack> itemsToCharge = new LinkedList<ItemStack>();

        if (entity instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer) entity;
            itemsToCharge.addAll(this.chargeableItemsInInventory(player.inventory.mainInventory));
            itemsToCharge.addAll(this.chargeableItemsInInventory(player.inventory.armorInventory));
        } else if (entity instanceof EntityItem)
        {
            itemsToCharge.add(((EntityItem) entity).getEntityItem());
        }

        double efficiency = this.calculateEfficiencyForEntity(entity);
        int itemCount = itemsToCharge.size();
        if (itemCount > 0)
        {
            double chargePerItem = Math.floor(maxCharge / itemCount);

            for (ItemStack itemStack : itemsToCharge)
            {
                IEnergyContainerItem chargeableItem = (IEnergyContainerItem) itemStack.getItem();
                int couldReceive = chargeableItem.receiveEnergy(itemStack, (int) chargePerItem, true);
                int toSend = this.extractEnergy(couldReceive, meta, false);
                if (BlockChargePad.isCreative(meta)) toSend = couldReceive;

                int sent = chargeableItem.receiveEnergy(itemStack, (int) (toSend * efficiency), false);
                if (sent > 0 && entity instanceof EntityItem)
                {
                    EntityItem entityItem = (EntityItem) entity;
                    if (entityItem.lifespan < Integer.MAX_VALUE) entityItem.lifespan = Integer.MAX_VALUE;
                }

                return sent;
            }
        }

        return 0;
    }

    @Override
    public void updateEntity()
    {
        int meta = this.worldObj.getBlockMetadata(this.xCoord, this.yCoord, this.zCoord);

        if (ServerHelper.isServerWorld(this.worldObj))
        {
            boolean oldActive = this.isActive;
            this.isActive = this.sentPower > 0;

            sentPower = 0;
            double totalChargeSendable = this.extractEnergy(BlockChargePad.getMaxSendRate(meta), meta, true);
            if (BlockChargePad.isCreative(meta)) totalChargeSendable = BlockChargePad.SEND[0];

            if (totalChargeSendable > 0)
            {
                AxisAlignedBB front = this.getAABBInFront(2);
                List<Entity> ownersInRange = this.getChargeableEntitesInAABB(front);

                if (ownersInRange.size() > 0)
                {
                    double totalChargeForEntity = totalChargeSendable / ownersInRange.size();

                    for (Entity entity : ownersInRange)
                        sentPower += this.chargeItemsGivenEntity(entity, totalChargeForEntity, meta);
                }
            }

            boolean shouldSendUpdate = false;
            shouldSendUpdate = shouldSendUpdate || (this.isActive != oldActive);

            if (this.ticksSinceLastUpdate == TICKS_PER_UPDATE)
            {
                this.ticksSinceLastUpdate = 0;
                shouldSendUpdate = true;
            }

            if (shouldSendUpdate) this.sendDescriptionPacket();

            this.ticksSinceLastUpdate++;
            if (this.ticksSinceLastUpdate > TICKS_PER_UPDATE) this.ticksSinceLastUpdate = TICKS_PER_UPDATE;
        }

        if (this.sentPower > 0 && ServerHelper.isClientWorld(this.worldObj)) this.spawnParticles(meta);

    }

    @SideOnly(Side.CLIENT)
    public void spawnParticles(int meta)
    {
        EffectRenderer er = FMLClientHandler.instance().getClient().effectRenderer;

        ForgeDirection orientation = this.getOrientation();

        Random rand = this.worldObj.rand;

        for (int particle = this.getParticleCount(meta); particle > 0; particle--)
        {
            double x = this.xCoord + (0.5F * orientation.offsetX) + (rand.nextFloat() * 0.8) + 0.1;
            double y = this.yCoord + (0.5F * orientation.offsetY) + (rand.nextFloat() * 0.8) + 0.1;
            double z = this.zCoord + (0.5F * orientation.offsetZ) + (rand.nextFloat() * 0.8) + 0.1;
            double[] velocity = getParticleVelocity();

            float[] colour = getParticleColour(rand);
            er.addEffect(new EntityChargePadFX(this.worldObj, x, y, z, getParticleMaxAge(), velocity, colour, this.getParticleSizeModifier(meta)));
        }
    }

    @Override
    public int getFacing()
    {
        return this.getOrientation().ordinal();
    }

    @Override
    public boolean allowYAxisFacing()
    {
        return true;
    }

    @Override
    public boolean rotateBlock()
    {
        int orientation = this.getFacing();
        orientation++;
        if (orientation >= ForgeDirection.VALID_DIRECTIONS.length) orientation = 0;

        return this.setFacing(orientation);
    }

    @Override
    public boolean setFacing(int side)
    {
        if (side == this.getOrientation().ordinal()) return false;
        else
        {
            this.setOrientation(side);
            this.sendDescriptionPacket();
            return true;
        }
    }

    @Override
    public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate)
    {
        boolean canReceive = from != this.getOrientation();
        if (!canReceive) return 0;

        int blockMeta = this.worldObj.getBlockMetadata(this.xCoord, this.yCoord, this.zCoord);
        int energyReceived = Math.min(BlockChargePad.getMaxEnergyStored(blockMeta) - this.storedEnergy, Math.min(BlockChargePad.getMaxReceiveRate(blockMeta), maxReceive));

        if (!simulate)
        {
            this.storedEnergy += energyReceived;
        }

        return energyReceived;
    }

    @Override
    public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate)
    {
        return 0;
    }

    public int extractEnergy(int maxExtract, int meta, boolean simulate)
    {
        int energyExtracted = Math.min(this.storedEnergy, Math.min(BlockChargePad.getMaxSendRate(meta), maxExtract));

        if (!simulate)
        {
            this.storedEnergy -= energyExtracted;
        }

        return energyExtracted;
    }

    @Override
    public int getEnergyStored(ForgeDirection from)
    {
        return this.storedEnergy;
    }

    @Override
    public int getMaxEnergyStored(ForgeDirection from)
    {
        int blockMeta = this.worldObj.getBlockMetadata(this.xCoord, this.yCoord, this.zCoord);
        return BlockChargePad.getMaxEnergyStored(blockMeta);
    }

    @Override
    public boolean canConnectEnergy(ForgeDirection from)
    {
        return from != this.getOrientation();
    }

    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip)
    {
        if (this.isActive)
        {
            currenttip.add(EnumChatFormatting.GREEN + "Active" + EnumChatFormatting.RESET);
        } else
        {
            currenttip.add(EnumChatFormatting.RED + "Inactive" + EnumChatFormatting.RESET);
        }

        int blockMeta = this.worldObj.getBlockMetadata(this.xCoord, this.yCoord, this.zCoord);
        if (BlockChargePad.isCreative(blockMeta)) currenttip.add("Charge: Infinite");
        else
            currenttip.add("Charge: " + StringHelper.getEnergyString(this.storedEnergy) + " / " + StringHelper.getEnergyString(BlockChargePad.getMaxEnergyStored(blockMeta)) + " RF");

        currenttip.add("Sent: " + StringHelper.getEnergyString(this.sentPower) + " RF/t");

        return currenttip;
    }

    @SideOnly(Side.CLIENT)
    protected int getParticleMaxAge()
    {
        return 16;
    }

    @SideOnly(Side.CLIENT)
    protected double[] getParticleVelocity()
    {
        ForgeDirection orientation = this.getOrientation();
        return new double[] {orientation.offsetX * 0.15D, orientation.offsetY * 0.15D, orientation.offsetZ * 0.15D};
    }

    @SideOnly(Side.CLIENT)
    protected float[] getParticleColour(Random rand)
    {
        float r = 1.0F;
        float g = 0F + (rand.nextFloat() * 0.25F);
        float b = 0F + (rand.nextFloat() * 0.25F);
        return new float[] {r, g, b};
    }

    @SideOnly(Side.CLIENT)
    protected int getParticleCount(int meta)
    {
        if (meta == 0) return 12;
        else if (meta == 2) return 4;
        else return 2;
    }

    @SideOnly(Side.CLIENT)
    protected float getParticleSizeModifier(int meta)
    {
        if (meta == 0) return 2.0F;
        else if (meta == 2) return 1.5F;
        else return 1.0F;
    }

    @Override
    public boolean drawOutline(DrawBlockHighlightEvent event)
    {
        if (GeneralConfig.debugRender)
        {
            AxisAlignedBB front = this.getAABBInFront(2);
            RenderHelper.renderAABBOutline(event.context, event.player, front, RGBA.Red.setAlpha(0.6f), 2.0f, event.partialTicks);
            return true;
        } else
        {
            return false;
        }
    }
}
