package net.shadowmage.ancientwarfare.core.input;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.core.config.AWCoreStatics;
import net.shadowmage.ancientwarfare.core.interfaces.IItemKeyInterface;
import net.shadowmage.ancientwarfare.core.interfaces.IItemKeyInterface.ItemKey;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.network.PacketItemInteraction;
import org.lwjgl.input.Keyboard;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public class InputHandler {

	public static final String KEY_ALT_ITEM_USE_0 = "keybind.alt_item_use_1";
	public static final String KEY_ALT_ITEM_USE_1 = "keybind.alt_item_use_2";
	public static final String KEY_ALT_ITEM_USE_2 = "keybind.alt_item_use_3";
	public static final String KEY_ALT_ITEM_USE_3 = "keybind.alt_item_use_4";
	public static final String KEY_ALT_ITEM_USE_4 = "keybind.alt_item_use_5";

	public static final InputHandler instance = new InputHandler();
	public static final Set<InputCallbackDispatcher> keybindingCallbacks = new HashSet<>();
	/*
	 * map of keys by their registry-name
	 */
	private final HashMap<String, Keybind> keybindMap;
	/*
	 * map of a -set- of keys by their key-id
	 */
	private final HashMap<Integer, Set<Keybind>> bindsByKey;
	private long lastMouseInput = -1;

	private InputHandler() {
		keybindMap = new HashMap<>();
		bindsByKey = new HashMap<>();
	}

	//TODO refactor the core keybinds to use this
	public static void registerCallBack(KeyBinding keyBinding, IInputCallback callback) {
		Predicate<InputCallbackDispatcher> matchingKeyBinding = d -> d.getKeyBinding().equals(keyBinding);
		if (keybindingCallbacks.stream().anyMatch(matchingKeyBinding)) {
			keybindingCallbacks.stream().filter(matchingKeyBinding).findFirst().ifPresent(d -> d.addInputCallback(callback));
		} else {
			keybindingCallbacks.add(new InputCallbackDispatcher(keyBinding, callback));
		}
	}

	public void loadConfig() {
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
			reassignKeyCode(k, getKeybindProp(name, k.key).getInt());
		}
	}

	public List<Property> getKeyConfig(String select) {
		List<Property> list = new ArrayList<>();
		for (Keybind entry : keybindMap.values()) {
			if (entry.getName().contains(select))
				list.add(getKeybindProp(entry.getName(), entry.getKeyCode()));
		}
		return list;
	}

	public Property getKeybindProp(String name) {
		Keybind k = getKeybind(name);
		if (k != null) {
			return getKeybindProp(name, k.key);
		}
		return null;
	}

	private Property getKeybindProp(String keyName, int defaultVal) {
		return AncientWarfareCore.statics.getKeyBindID(keyName, defaultVal);
	}

	@SubscribeEvent
	public void onKeyInput(KeyInputEvent evt) {
		Minecraft minecraft = Minecraft.getMinecraft();
		EntityPlayer player = minecraft.player;
		if (player == null) {
			return;
		}

		int key = Keyboard.getEventKey();
		boolean state = Keyboard.getEventKeyState();

		if (state) {
			if (bindsByKey.containsKey(key)) {
				bindsByKey.get(key).forEach(Keybind::onKeyPressed);
			}

			keybindingCallbacks.stream().filter(k -> k.getKeyBinding().isKeyDown()).forEach(InputCallbackDispatcher::onKeyPressed);
		}
	}

	public Keybind getKeybind(String name) {
		return keybindMap.get(name);
	}

	public String getKeybindBinding(String name) {
		return Keyboard.getKeyName(getKeybind(name).getKeyCode());
	}

	public void registerKeybind(String name, int keyCode, IInputCallback cb) {
		if (!keybindMap.containsKey(name)) {
			Property property = getKeybindProp(name, keyCode);
			property.setComment("Default key: " + Keyboard.getKeyName(keyCode));
			int key = property.getInt();
			Keybind k = new Keybind(name, key);
			keybindMap.put(name, k);
			if (!bindsByKey.containsKey(key)) {
				bindsByKey.put(key, new HashSet<>());
			}
			bindsByKey.get(key).add(k);
		} else {
			throw new RuntimeException("Attempt to register duplicate keybind: " + name);
		}
		if (cb != null) {
			keybindMap.get(name).inputHandlers.add(cb);
		}
	}

	public void reassignKeybind(String name, int newKey) {
		Keybind k = keybindMap.get(name);
		if (k == null) {
			return;
		}

		getKeybindProp(name, k.key).set(newKey);
		reassignKeyCode(k, newKey);
		AWCoreStatics.update();
	}

	private void reassignKeyCode(Keybind k, int newKey) {
		bindsByKey.get(k.key).remove(k);
		k.key = newKey;

		if (!bindsByKey.containsKey(newKey)) {
			bindsByKey.put(newKey, new HashSet<>());
		}
		bindsByKey.get(newKey).add(k);
	}

	public void addInputCallback(String name, IInputCallback cb) {
		keybindMap.get(name).inputHandlers.add(cb);
	}

	public Collection<Keybind> getKeybinds() {
		return keybindMap.values();
	}

	public static final class Keybind {
		List<IInputCallback> inputHandlers = new ArrayList<>();

		private int key;
		private final String name;
		private boolean isPressed;

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

		private void onKeyPressed() {
			isPressed = true;
			for (IInputCallback c : inputHandlers) {
				c.onKeyPressed();
			}
		}

		@Override
		public String toString() {
			return "Keybind [" + key + "," + name + "]";
		}

		public boolean isPressed() {
			return isPressed;
		}

		@Override
		public boolean equals(Object o) {
			return this == o || o instanceof Keybind && getName().equals(((Keybind) o).getName());
		}

		@Override
		public int hashCode() {
			return getName().hashCode();
		}
	}

	private static final class ItemInputCallback implements IInputCallback {
		private final ItemKey key;

		public ItemInputCallback(ItemKey key) {
			this.key = key;
		}

		@Override
		public void onKeyPressed() {
			Minecraft minecraft = Minecraft.getMinecraft();
			if (minecraft.currentScreen != null) {
				return;
			}
			if (!runAction(minecraft, EnumHand.MAIN_HAND)) {
				runAction(minecraft, EnumHand.OFF_HAND);
			}
		}

		private boolean runAction(Minecraft minecraft, EnumHand hand) {
			@Nonnull ItemStack stack = minecraft.player.getHeldItem(hand);
			if (!stack.isEmpty() && stack.getItem() instanceof IItemKeyInterface) {
				if (((IItemKeyInterface) stack.getItem()).onKeyActionClient(minecraft.player, stack, key)) {
					PacketItemInteraction pkt = new PacketItemInteraction(0, key);
					NetworkHandler.sendToServer(pkt);
					return true;
				}
			}
			return false;
		}
	}

}
