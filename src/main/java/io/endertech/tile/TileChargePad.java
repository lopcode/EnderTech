package io.endertech.tile;

import cofh.api.energy.IEnergyContainerItem;
import cofh.lib.util.helpers.ServerHelper;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.endertech.block.BlockChargePad;
import io.endertech.fx.EntityChargePadFX;
import io.endertech.gui.client.GuiChargePad;
import io.endertech.gui.container.ContainerChargePad;
import io.endertech.network.PacketETBase;
import io.endertech.reference.Strings;
import io.endertech.util.helper.LocalisationHelper;
import io.endertech.util.helper.StringHelper;
import net.minecraft.block.Block;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;
import java.util.*;

public class TileChargePad extends TilePad
{
    public static final int[] RECEIVE = {0, 1 * 2000, 10 * 2000};
    public static final int[] SEND = {10 * 1000000, 1 * 2000, 10 * 2000};
    public static final int[] CAPACITY = {-1, 1 * 2000000, 10 * 1000000};

    public int sentPower = 0;

    public TileChargePad()
    {
        super();

        this.tileName = "Charge Pad";
    }

    public static void init()
    {
        GameRegistry.registerTileEntity(TileChargePad.class, "tile." + Strings.Blocks.CHARGE_PAD);
    }

    public int getMaxEnergyStored(int meta)
    {
        return CAPACITY[meta];
    }

    public int getMaxReceiveRate(int meta)
    {
        return RECEIVE[meta];
    }

    public int getMaxSendRate(int meta)
    {
        return SEND[meta];
    }

    @Override
    public String toString()
    {
        return "Charge Pad: position " + this.xCoord + ", " + this.yCoord + ", " + this.zCoord;
    }

    public Set<ItemStack> chargeableItemsInInventory(ItemStack[] itemStacks)
    {
        Set<ItemStack> itemsToCharge = new HashSet<ItemStack>();

        for (ItemStack itemStack : itemStacks)
        {
            if (itemStack == null) continue;

            Item item = itemStack.getItem();
            if (item instanceof IEnergyContainerItem)
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

    public List<ItemStack> getItemsToChargeFromEntity(Entity entity)
    {
        LinkedList<ItemStack> itemsToCharge = new LinkedList<ItemStack>();

        if (entity instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer) entity;
            itemsToCharge.addAll(this.chargeableItemsInInventory(player.inventory.mainInventory));
            itemsToCharge.addAll(this.chargeableItemsInInventory(player.inventory.armorInventory));
        } else if (entity instanceof EntityItem)
        {
            EntityItem entityItem = (EntityItem) entity;
            ItemStack item = entityItem.getEntityItem();

            if (item.stackSize == 1) itemsToCharge.add(item);
        }

        return itemsToCharge;
    }

    public int chargeItemsGivenEntity(Entity entity, int maxCharge, int meta)
    {
        List<ItemStack> itemsToCharge = this.getItemsToChargeFromEntity(entity);
        double efficiency = this.calculateEfficiencyForEntity(entity);

        int totalSent = 0;
        int itemCount = itemsToCharge.size();
        if (itemCount > 0)
        {
            int chargePerItem = (int) Math.floor(maxCharge / itemCount);
            if (chargePerItem == 0 && maxCharge > 0) chargePerItem = 1;

            for (ItemStack itemStack : itemsToCharge)
            {
                IEnergyContainerItem chargeableItem = (IEnergyContainerItem) itemStack.getItem();
                int couldReceive = chargeableItem.receiveEnergy(itemStack, chargePerItem, true);
                int toSend = this.extractEnergy(couldReceive, meta, false);
                if (this.isCreative) toSend = couldReceive;

                int sent = chargeableItem.receiveEnergy(itemStack, (int) (toSend * efficiency), false);
                if (sent > 0 && entity instanceof EntityItem)
                {
                    EntityItem entityItem = (EntityItem) entity;
                    if (entityItem.lifespan < Integer.MAX_VALUE) entityItem.lifespan = Integer.MAX_VALUE;
                }

                totalSent += sent;
            }

            if (totalSent >= maxCharge) return maxCharge;
        }

        return totalSent;
    }

    @Override
    public void updateEntity()
    {
        int meta = this.worldObj.getBlockMetadata(this.xCoord, this.yCoord, this.zCoord);
        this.isCreative = (meta == 0);

        if (ServerHelper.isServerWorld(this.worldObj))
        {
            boolean oldActive = this.isActive;
            this.isActive = this.sentPower > 0;

            sentPower = 0;
            int totalChargeSendable = this.extractEnergy(getMaxSendRate(meta), meta, true);
            if (this.isCreative) totalChargeSendable = SEND[0];

            if (totalChargeSendable > 0)
            {
                AxisAlignedBB front = this.getAABBInFront(2);
                List<Entity> ownersInRange = this.getChargeableEntitesInAABB(front);

                int totalChargeForEntity = (int) (((double) totalChargeSendable) / ownersInRange.size());
                if (ownersInRange.size() > 0)
                {
                    for (Entity entity : ownersInRange)
                    {
                        int powerSentToEntity = this.chargeItemsGivenEntity(entity, totalChargeForEntity, meta);
                        sentPower += powerSentToEntity;
                    }
                }
            }

            this.chargeFromGUISlot();

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

    @Override
    public PacketETBase getPacket()
    {
        PacketETBase packet = super.getPacket();
        packet.addInt(this.sentPower);

        return packet;
    }

    @Override
    public void handleTilePacket(PacketETBase tilePacket, boolean isServer)
    {
        super.handleTilePacket(tilePacket, isServer);

        int sentPower = tilePacket.getInt();

        if (!isServer)
        {
            this.sentPower = sentPower;
        }
    }

    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip)
    {
        if (this.isActive)
        {
            currenttip.add(EnumChatFormatting.GREEN + LocalisationHelper.localiseString("info.active") + EnumChatFormatting.RESET);
        } else
        {
            currenttip.add(EnumChatFormatting.RED + LocalisationHelper.localiseString("info.inactive") + EnumChatFormatting.RESET);
        }

        int blockMeta = this.worldObj.getBlockMetadata(this.xCoord, this.yCoord, this.zCoord);
        if (this.isCreative) currenttip.add(LocalisationHelper.localiseString("info.charge", "Infinite"));
        else
            currenttip.add(LocalisationHelper.localiseString("info.charge", StringHelper.getEnergyString(this.storedEnergy) + " / " + StringHelper.getEnergyString(this.getMaxEnergyStored(blockMeta)) + " RF"));

        currenttip.add(LocalisationHelper.localiseString("info.sent", StringHelper.getEnergyString(this.sentPower) + " RF/t"));

        return currenttip;
    }

    @SideOnly(Side.CLIENT)
    public void spawnParticles(int meta)
    {
        EffectRenderer er = FMLClientHandler.instance().getClient().effectRenderer;
        ForgeDirection orientation = this.getOrientation();
        Random rand = this.worldObj.rand;

        for (int particle = this.getParticleCount(meta); particle > 0; particle--)
        {
            double xSign = (rand.nextBoolean() ? -1 : 1);
            double ySign = (rand.nextBoolean() ? -1 : 1);
            double zSign = (rand.nextBoolean() ? -1 : 1);

            double xAddition = xSign * (rand.nextDouble() * 0.3) + (0.05 * xSign);
            double yAddition = ySign * (rand.nextDouble() * 0.3) + (0.05 * ySign);
            double zAddition = zSign * (rand.nextDouble() * 0.3) + (0.05 * zSign);

            double x = this.xCoord + (0.5F * orientation.offsetX) + 0.5 + xAddition;
            double y = this.yCoord + (0.5F * orientation.offsetY) + 0.5 + yAddition;
            double z = this.zCoord + (0.5F * orientation.offsetZ) + 0.5 + zAddition;

            er.addEffect(new EntityChargePadFX(this.worldObj, x, y, z, getParticleMaxAge(), getParticleVelocity(), getParticleColour(rand), this.getParticleSizeModifier(meta)));
        }
    }

    @SideOnly(Side.CLIENT)
    public int getParticleMaxAge()
    {
        return 16;
    }

    @SideOnly(Side.CLIENT)
    public double[] getParticleVelocity()
    {
        ForgeDirection orientation = this.getOrientation();
        return new double[] {orientation.offsetX * 0.15D, orientation.offsetY * 0.15D, orientation.offsetZ * 0.15D};
    }

    @SideOnly(Side.CLIENT)
    public float[] getParticleColour(Random rand)
    {
        if (this.isItemInChargeSlotTuberous()) return getRainbowParticleColour(rand);

        float r = 1.0F;
        float g = 0F + (rand.nextFloat() * 0.25F);
        float b = 0F + (rand.nextFloat() * 0.25F);
        return new float[] {r, g, b};
    }

    @SideOnly(Side.CLIENT)
    public int getParticleCount(int meta)
    {
        if (meta == 0) return 5;
        else if (meta == 2) return 2;
        else return 1;
    }

    @SideOnly(Side.CLIENT)
    public float getParticleSizeModifier(int meta)
    {
        if (meta == 0) return 2.0F;
        else if (meta == 2) return 1.75F;
        else return 1.5F;
    }

    public String getName()
    {
        Block block = this.worldObj.getBlock(this.xCoord, this.yCoord, this.zCoord);
        int blockMeta = this.worldObj.getBlockMetadata(this.xCoord, this.yCoord, this.zCoord);

        return LocalisationHelper.localiseString(block.getUnlocalizedName() + "." + blockMeta + ".name");
    }

    @Override
    public Object getGuiClient(InventoryPlayer inventory)
    {
        return new GuiChargePad(inventory, this);
    }

    @Override
    public Object getGuiServer(InventoryPlayer inventory)
    {
        return new ContainerChargePad(inventory, this);
    }

    public IIcon getFrontIcon()
    {
        Block block = this.worldObj.getBlock(this.xCoord, this.yCoord, this.zCoord);

        if (!(block instanceof BlockChargePad)) return null;

        int blockMeta = this.worldObj.getBlockMetadata(this.xCoord, this.yCoord, this.zCoord);
        BlockChargePad blockChargePad = (BlockChargePad) block;

        if (this.isActive) return blockChargePad.getActiveIcon(blockMeta);
        else return blockChargePad.getInactiveIcon(blockMeta);
    }
}
