package io.endertech.client;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.common.network.PacketDispatcher;
import io.endertech.config.KeyConfig;
import io.endertech.helper.LogHelper;
import io.endertech.items.IKeyHandler;
import io.endertech.network.message.MessageKeyPressed;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import org.lwjgl.input.Keyboard;

public class KeyBindingHandler
{
    public static KeyBinding keyToolIncrease = new KeyBinding(KeyConfig.keyToolIncreaseDescription, Keyboard.KEY_PRIOR);
    public static KeyBinding keyToolDecrease = new KeyBinding(KeyConfig.keyToolDecreaseDescription, Keyboard.KEY_NEXT);

    public static KeyBinding[] keyArray = new KeyBinding[] {keyToolIncrease, keyToolDecrease};
    public static boolean[] keyRepeating = new boolean[] {false, false, false};

    public KeyBindingHandler()
    {
        super(keyArray, keyRepeating);
    }

    @SubscribeEvent
    public void handleKeyInputEvent(InputEvent.KeyInputEvent event)
    {
        //LogHelper.info("KeyDown");
        if (FMLClientHandler.instance().getClient().inGameHasFocus)
        {
            //LogHelper.info("End&Focus");
            EntityPlayer player = FMLClientHandler.instance().getClient().thePlayer;
            if (player != null)
            {
                //LogHelper.info("PlayerNotNull");
                ItemStack equippedItem = player.getCurrentEquippedItem();

                if (equippedItem != null && equippedItem.getItem() instanceof IKeyHandler)
                {
                    if (player.worldObj.isRemote)
                    {
                        LogHelper.info("Remote, sent packet to server");
                        PacketDispatcher.sendPacketToServer(new MessageKeyPressed(KeyConfig.descriptionToCode(kb.keyDescription)).makePacket());
                    } else
                    {
                        LogHelper.info("Client, handling key press");
                        ((IKeyHandler) player.getCurrentEquippedItem().getItem()).handleKey(player, equippedItem, KeyConfig.descriptionToCode(kb.keyDescription));
                    }
                }
            }
        }
    }
}
