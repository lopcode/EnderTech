package io.endertech.client.handler;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import io.endertech.item.IKeyHandler;
import io.endertech.network.PacketKeyPressed;
import io.endertech.reference.Strings;
import io.endertech.util.Key;
import io.endertech.util.helper.LogHelper;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import org.lwjgl.input.Keyboard;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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

    public static ETKeyBinding keyToolIncrease = new ETKeyBinding(Strings.Keys.keyToolIncreaseDescription, Keyboard.KEY_PRIOR, "key.endertech.tools", Key.KeyCode.TOOL_INCREASE);
    public static ETKeyBinding keyToolDecrease = new ETKeyBinding(Strings.Keys.keyToolDecreaseDescription, Keyboard.KEY_NEXT, "key.endertech.tools", Key.KeyCode.TOOL_DECREASE);
    public static ETKeyBinding[] keyBindings = new ETKeyBinding[] {keyToolIncrease, keyToolDecrease};

    public static Map<String, Key.KeyCode> keyCodeMap = new HashMap<String, Key.KeyCode>();

    public static void init()
    {
        for (ETKeyBinding keyBinding : keyBindings)
        {
            ClientRegistry.registerKeyBinding(keyBinding.getMinecraftKeyBinding());
            keyCodeMap.put(keyBinding.getMinecraftKeyBinding().getKeyDescription(), keyBinding.getKeyCode());
        }
    }

    public static Key.KeyCode whichKeyPressed()
    {
        for (ETKeyBinding keyBinding : keyBindings)
        {
            if (keyBinding.getMinecraftKeyBinding().getIsKeyPressed())
            {
                return keyCodeMap.get(keyBinding.getMinecraftKeyBinding().getKeyDescription());
            }
        }

        return Key.KeyCode.UNKNOWN;
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
                    Key.KeyCode keyCode = whichKeyPressed();
                    Set<Key.KeyCode> handledKeyCodes = ((IKeyHandler) equippedItem.getItem()).getHandledKeys();

                    if (!handledKeyCodes.contains(keyCode)) return;

                    if (player.worldObj.isRemote)
                    {
                        LogHelper.debug("Remote, sent " + keyCode.toString() + " to server");
                        new PacketKeyPressed().sendKeyPressedPacket(keyCode);
                    } else
                    {
                        LogHelper.debug("Client, handling key press: " + keyCode.toString());
                        ((IKeyHandler) player.getCurrentEquippedItem().getItem()).handleKey(player, equippedItem, keyCode);
                    }
                }
            }
        }
    }
}
