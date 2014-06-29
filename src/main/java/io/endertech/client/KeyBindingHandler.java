package io.endertech.client;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import io.endertech.config.KeyConfig;
import io.endertech.helper.LogHelper;
import io.endertech.items.IKeyHandler;
import io.endertech.network.NetworkHandler;
import io.endertech.network.message.MessageKeyPressed;
import io.endertech.util.Key;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import org.lwjgl.input.Keyboard;
import java.util.HashMap;
import java.util.Map;

public class KeyBindingHandler
{
    private static class ETKeyBinding
    {
        private KeyBinding keyBinding;
        private Key.KeyCode keyCode;

        public ETKeyBinding(String description, int keyboardCode, String category, Key.KeyCode keyCode)
        {
            this.keyBinding = new KeyBinding(description, keyboardCode, category);
            this.keyCode = keyCode;
        }

        public Key.KeyCode getKeyCode()
        {
            return this.keyCode;
        }

        public KeyBinding getMinecraftKeyBinding()
        {
            return this.keyBinding;
        }
    }

    public static ETKeyBinding keyToolIncrease = new ETKeyBinding(KeyConfig.keyToolIncreaseDescription, Keyboard.KEY_PRIOR, "key.endertech.tools", Key.KeyCode.TOOL_INCREASE);
    public static ETKeyBinding keyToolDecrease = new ETKeyBinding(KeyConfig.keyToolDecreaseDescription, Keyboard.KEY_NEXT, "key.endertech.tools", Key.KeyCode.TOOL_DECREASE);
    public static ETKeyBinding[] keyBindings = new ETKeyBinding[] {keyToolIncrease, keyToolDecrease};

    public static Map<Integer, Key.KeyCode> keyCodeMap = new HashMap<Integer, Key.KeyCode>();

    public static void init()
    {
        for (ETKeyBinding keyBinding : keyBindings)
        {
            ClientRegistry.registerKeyBinding(keyBinding.getMinecraftKeyBinding());
            keyCodeMap.put(keyBinding.getMinecraftKeyBinding().getKeyCode(), keyBinding.getKeyCode());
        }
    }

    public static Key.KeyCode whichKeyPressed()
    {
        for (ETKeyBinding keyBinding : keyBindings)
        {
            if (keyBinding.getMinecraftKeyBinding().getIsKeyPressed())
            {
                return keyCodeMap.get(keyBinding.getKeyCode());
            }
        }

        return Key.KeyCode.UNKNOWN;
    }

    @SubscribeEvent
    public static void handleKeyInputEvent(InputEvent.KeyInputEvent event)
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
                        NetworkHandler.INSTANCE.sendToServer(new MessageKeyPressed(whichKeyPressed()));
                    } else
                    {
                        LogHelper.info("Client, handling key press");
                        ((IKeyHandler) player.getCurrentEquippedItem().getItem()).handleKey(player, equippedItem, whichKeyPressed());
                    }
                }
            }
        }
    }
}
