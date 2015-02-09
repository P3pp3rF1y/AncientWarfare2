package net.shadowmage.ancientwarfare.core.input;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent.KeyInputEvent;
import cpw.mods.fml.common.gameevent.InputEvent.MouseInputEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.shadowmage.ancientwarfare.core.config.AWCoreStatics;
import net.shadowmage.ancientwarfare.core.interfaces.IItemClickable;
import net.shadowmage.ancientwarfare.core.interfaces.IItemKeyInterface;
import net.shadowmage.ancientwarfare.core.interfaces.IItemKeyInterface.ItemKey;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.network.PacketItemInteraction;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.util.*;

public class InputHandler {

    public static final String KEY_ALT_ITEM_USE_0 = "keybind.alt_item_use_1";
    public static final String KEY_ALT_ITEM_USE_1 = "keybind.alt_item_use_2";
    public static final String KEY_ALT_ITEM_USE_2 = "keybind.alt_item_use_3";
    public static final String KEY_ALT_ITEM_USE_3 = "keybind.alt_item_use_4";
    public static final String KEY_ALT_ITEM_USE_4 = "keybind.alt_item_use_5";

    public static final InputHandler instance = new InputHandler();
    /**
     * map of keys by their registry-name
     */
    private final HashMap<String, Keybind> keybindMap;
    /**
     * map of a -set- of keys by their key-id
     */
    private final HashMap<Integer, Set<Keybind>> bindsByKey;
    private Configuration config;
    private long lastMouseInput = -1;
    private InputHandler() {
        keybindMap = new HashMap<String, Keybind>();
        bindsByKey = new HashMap<Integer, Set<Keybind>>();
    }

    public void loadConfig(Configuration config) {
        this.config = config;

        registerKeybind(KEY_ALT_ITEM_USE_0, Keyboard.KEY_Z, new ItemInputCallback(ItemKey.KEY_0));
        registerKeybind(KEY_ALT_ITEM_USE_1, Keyboard.KEY_X, new ItemInputCallback(ItemKey.KEY_1));
        registerKeybind(KEY_ALT_ITEM_USE_2, Keyboard.KEY_C, new ItemInputCallback(ItemKey.KEY_2));
        registerKeybind(KEY_ALT_ITEM_USE_3, Keyboard.KEY_V, new ItemInputCallback(ItemKey.KEY_3));
        registerKeybind(KEY_ALT_ITEM_USE_4, Keyboard.KEY_B, new ItemInputCallback(ItemKey.KEY_4));
    }

    public void updateFromConfig() {
        updateKeybind(KEY_ALT_ITEM_USE_0);
        updateKeybind(KEY_ALT_ITEM_USE_1);
        updateKeybind(KEY_ALT_ITEM_USE_2);
        updateKeybind(KEY_ALT_ITEM_USE_3);
        updateKeybind(KEY_ALT_ITEM_USE_4);
    }

    private void updateKeybind(String name) {
        Keybind k = getKeybind(name);
        if (k != null)//could be null if the keybind was added by a child-mod that is not currently present
        {
            reassignKeyCode(k, getKeybindProp(name, k.key).getInt(k.key));
        }
    }

    private Property getKeybindProp(String keyName, int defaultVal) {
        return config.get(AWCoreStatics.keybinds, keyName, defaultVal);
    }

    @SubscribeEvent
    public void onMouseInput(MouseInputEvent evt) {
        Minecraft minecraft = Minecraft.getMinecraft();
        if (minecraft == null || minecraft.currentScreen != null || minecraft.thePlayer == null || minecraft.theWorld == null) {
            return;
        }
        int button = Mouse.getEventButton();
        if (button < 0 || !Mouse.getEventButtonState()) {
            return;
        }
        long time = System.currentTimeMillis();
        if (lastMouseInput == -1 || time - lastMouseInput > 250) {
            lastMouseInput = time;
            ItemStack stack = minecraft.thePlayer.getCurrentEquippedItem();
            if (stack != null && stack.getItem() instanceof IItemClickable) {
                IItemClickable click = (IItemClickable) stack.getItem();
                if (button == 1 && click.onRightClickClient(minecraft.thePlayer, stack)) {
                    NetworkHandler.sendToServer(new PacketItemInteraction(2));
                } else if (button == 0 && click.onLeftClickClient(minecraft.thePlayer, stack)) {
                    NetworkHandler.sendToServer(new PacketItemInteraction(1));
                }
            }
        }
    }

    @SubscribeEvent
    public void onKeyInput(KeyInputEvent evt) {
        Minecraft minecraft = Minecraft.getMinecraft();
        if (minecraft == null) {
            return;
        }
        EntityPlayer player = minecraft.thePlayer;
        if (player == null) {
            return;
        }

        int key = Keyboard.getEventKey();
        boolean state = Keyboard.getEventKeyState();

        if (bindsByKey.containsKey(key)) {
            Set<Keybind> keys = bindsByKey.get(key);
            for (Keybind k : keys) {
                if (state) {
                    k.onKeyPressed();
                } else {
                    k.onKeyReleased();
                }
            }
        }
    }

    public Keybind getKeybind(String name) {
        return keybindMap.get(name);
    }

    public String getKeybindBinding(String name) {
        return Keyboard.getKeyName(getKeybind(name).getKeyCode());
    }

    public void registerKeybind(String name, int keyCode, InputCallback cb) {
        if (!keybindMap.containsKey(name)) {
            int key = config.get(AWCoreStatics.keybinds, name, keyCode).getInt(keyCode);
            Keybind k = new Keybind(name, key);
            keybindMap.put(name, k);
            if (!bindsByKey.containsKey(key)) {
                bindsByKey.put(key, new HashSet<Keybind>());
            }
            bindsByKey.get(key).add(k);
        } else {
            throw new RuntimeException("Attempt to register duplicate keybind: " + name);
        }
        if (cb != null) {
            keybindMap.get(name).inputHandlers.add(cb);
        }
        config.save();
    }

    public void reassignKeybind(String name, int newKey) {
        Keybind k = keybindMap.get(name);
        if (k == null) {
            return;
        }

        config.get(AWCoreStatics.keybinds, name, 0).set(newKey);
        reassignKeyCode(k, newKey);
        config.save();
    }

    private void reassignKeyCode(Keybind k, int newKey) {
        bindsByKey.get(k.key).remove(k);
        k.key = newKey;

        if (!bindsByKey.containsKey(newKey)) {
            bindsByKey.put(newKey, new HashSet<Keybind>());
        }
        bindsByKey.get(newKey).add(k);
    }

    public void addInputCallback(String name, InputCallback cb) {
        keybindMap.get(name).inputHandlers.add(cb);
    }

    public Collection<Keybind> getKeybinds() {
        return keybindMap.values();
    }

    public static final class Keybind {
        List<InputCallback> inputHandlers = new ArrayList<InputCallback>();

        private int key;
        private final String name;

        private Keybind(String name, int key) {
            this.name = name;
            this.key = key;
        }

        public String getName() {
            return name;
        }

        public int getKeyCode() {
            return key;
        }

        public void onKeyPressed() {
            for (InputCallback c : inputHandlers) {
                c.onKeyPressed();
            }
        }

        public void onKeyReleased() {
            for (InputCallback c : inputHandlers) {
                c.onKeyReleased();
            }
        }

        @Override
        public String toString() {
            return "Keybind [" + key + "," + name + "]";
        }
    }

    public static interface InputCallback {
        public void onKeyPressed();

        public void onKeyReleased();
    }

    private static final class ItemInputCallback implements InputCallback {
        ItemKey key;

        public ItemInputCallback(ItemKey key) {
            this.key = key;
        }

        @Override
        public void onKeyPressed() {
            Minecraft minecraft = Minecraft.getMinecraft();
            if (minecraft == null || minecraft.thePlayer == null || minecraft.currentScreen != null) {
                return;
            }
            ItemStack stack = minecraft.thePlayer.inventory.getCurrentItem();
            if (stack != null && stack.getItem() instanceof IItemKeyInterface) {
                if (((IItemKeyInterface) stack.getItem()).onKeyActionClient(minecraft.thePlayer, stack, key)) {
                    PacketItemInteraction pkt = new PacketItemInteraction(0, key);
                    NetworkHandler.sendToServer(pkt);
                }
            }
        }

        @Override
        public void onKeyReleased() {
        }

    }

}
