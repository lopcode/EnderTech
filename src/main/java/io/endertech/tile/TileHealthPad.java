package io.endertech.tile;

import cofh.lib.util.helpers.ServerHelper;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.endertech.fx.EntityHealthPadFX;
import io.endertech.gui.client.GuiHealthPad;
import io.endertech.gui.container.ContainerHealthPad;
import io.endertech.network.PacketETBase;
import io.endertech.reference.Strings;
import io.endertech.util.helper.LocalisationHelper;
import io.endertech.util.helper.StringHelper;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.util.ForgeDirection;
import java.util.List;
import java.util.Random;

public class TileHealthPad extends TilePad
{
    public static final int[] RECEIVE = {0, 1 * 2000, 10 * 2000};
    public static final int[] SEND = {10 * 1000000, 1 * 2000, 10 * 2000};
    public static final int[] CAPACITY = {-1, 1 * 2000000, 10 * 1000000};
    public static byte particleSkip = 0;

    public int sentHealth = 0;

    public TileHealthPad()
    {
        super();

        this.tileName = "Health Pad";
    }

    public static void init()
    {
        GameRegistry.registerTileEntity(TileHealthPad.class, "tile." + Strings.Blocks.HEALTH_PAD);
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
        return "Health Pad: position " + this.xCoord + ", " + this.yCoord + ", " + this.zCoord;
    }

    @Override
    public void updateEntity()
    {
        int meta = this.worldObj.getBlockMetadata(this.xCoord, this.yCoord, this.zCoord);
        this.isCreative = (meta == 0);

        if (ServerHelper.isServerWorld(this.worldObj))
        {
            boolean oldActive = this.isActive;
            this.isActive = this.sentHealth > 0;

            sentHealth = 1;

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

        if (this.sentHealth > 0 && ServerHelper.isClientWorld(this.worldObj))
        {
            particleSkip++;

            if (particleSkip == 3)
            {
                this.spawnParticles(meta);
                particleSkip = 0;
            }
        }
    }

    @Override
    public PacketETBase getPacket()
    {
        PacketETBase packet = super.getPacket();
        packet.addInt(this.sentHealth);

        return packet;
    }

    @Override
    public void handleTilePacket(PacketETBase tilePacket, boolean isServer)
    {
        super.handleTilePacket(tilePacket, isServer);

        int sentHealth = tilePacket.getInt();

        if (!isServer)
        {
            this.sentHealth = sentHealth;
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

        currenttip.add(LocalisationHelper.localiseString("info.sent", this.sentHealth + " health"));

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

            EntityHealthPadFX particleFX = new EntityHealthPadFX(this.worldObj, x, y, z, getParticleMaxAge(), getParticleVelocity(), getParticleColour(rand), this.getParticleSizeModifier(meta));
            er.addEffect(particleFX);
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
        if (meta == 0) return 2;
        else if (meta == 2) return 2;
        else return 1;
    }

    @SideOnly(Side.CLIENT)
    public float getParticleSizeModifier(int meta)
    {
        if (meta == 0) return 0.9F;
        else if (meta == 2) return 0.75F;
        else return 0.6F;
    }

    @Override
    public Object getGuiClient(InventoryPlayer inventory)
    {
        return new GuiHealthPad(inventory, this);
    }

    @Override
    public Object getGuiServer(InventoryPlayer inventory)
    {
        return new ContainerHealthPad(inventory, this);
    }
}
