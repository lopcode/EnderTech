package io.endertech.client;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.KeyBindingRegistry;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.common.network.PacketDispatcher;
import io.endertech.config.KeyConfig;
import io.endertech.helper.LogHelper;
import io.endertech.items.IKeyHandler;
import io.endertech.lib.Reference;
import io.endertech.network.packet.PacketKeyPressed;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import org.lwjgl.input.Keyboard;

import java.util.EnumSet;

public class KeyBindingHandler extends KeyBindingRegistry.KeyHandler
{
    public static KeyBinding keyToolIncrease = new KeyBinding(KeyConfig.keyToolIncreaseDescription, Keyboard.KEY_PRIOR);
    public static KeyBinding keyToolDecrease = new KeyBinding(KeyConfig.keyToolDecreaseDescription, Keyboard.KEY_NEXT);

    public static KeyBinding[] keyArray = new KeyBinding[]{keyToolIncrease, keyToolDecrease};
    public static boolean[] keyRepeating = new boolean[]{false, false};

    public KeyBindingHandler()
    {
        super(keyArray, keyRepeating);
    }

    @Override
    public void keyDown(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd, boolean isRepeat)
    {
        LogHelper.info("KeyDown");
        if (tickEnd && FMLClientHandler.instance().getClient().inGameHasFocus)
        {
            LogHelper.info("End&Focus");
            EntityPlayer player = FMLClientHandler.instance().getClient().thePlayer;
            if (player != null)
            {
                LogHelper.info("PlayerNotNull");
                ItemStack equippedItem = player.getCurrentEquippedItem();

                if (equippedItem != null && equippedItem.getItem() instanceof IKeyHandler)
                {
                    if (player.worldObj.isRemote)
                    {
                        LogHelper.info("Remote, sent packet to server");
                        PacketDispatcher.sendPacketToServer(new PacketKeyPressed(kb.keyDescription).makePacket());
                    } else
                    {
                        LogHelper.info("Client, handling key press");
                        ((IKeyHandler) player.getCurrentEquippedItem().getItem()).handleKey(player, equippedItem, kb.keyDescription);
                    }
                }
            }
        }
    }

    @Override
    public void keyUp(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd)
    {

    }

    @Override
    public EnumSet<TickType> ticks()
    {
        return EnumSet.of(TickType.CLIENT);
    }

    @Override
    public String getLabel()
    {
        return Reference.MOD_ID + "KeyBindingHandler";
    }
}
