package net.shadowmage.ancientwarfare.core.input;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.shadowmage.ancientwarfare.core.config.AWCoreStatics;
import net.shadowmage.ancientwarfare.core.interfaces.IItemKeyInterface.ItemAltFunction;
import org.lwjgl.input.Keyboard;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

public class InputHandler {

	private static final String CATEGORY = "keybind.category.awCore";
	public static final KeyBinding ALT_ITEM_USE_1 = new KeyBinding(AWCoreStatics.KEY_ALT_ITEM_USE_1, ItemKeyConflictContext.INSTANCE, Keyboard.KEY_Z, CATEGORY);
	public static final KeyBinding ALT_ITEM_USE_2 = new KeyBinding(AWCoreStatics.KEY_ALT_ITEM_USE_2, ItemKeyConflictContext.INSTANCE, Keyboard.KEY_X, CATEGORY);
	public static final KeyBinding ALT_ITEM_USE_3 = new KeyBinding(AWCoreStatics.KEY_ALT_ITEM_USE_3, ItemKeyConflictContext.INSTANCE, Keyboard.KEY_C, CATEGORY);
	public static final KeyBinding ALT_ITEM_USE_4 = new KeyBinding(AWCoreStatics.KEY_ALT_ITEM_USE_4, ItemKeyConflictContext.INSTANCE, Keyboard.KEY_V, CATEGORY);
	public static final KeyBinding ALT_ITEM_USE_5 = new KeyBinding(AWCoreStatics.KEY_ALT_ITEM_USE_5, ItemKeyConflictContext.INSTANCE, Keyboard.KEY_B, CATEGORY);

	private static final Set<InputCallbackDispatcher> keybindingCallbacks = new HashSet<>();

	static {
		MinecraftForge.EVENT_BUS.register(new InputHandler());
	}

	private InputHandler() {
	}

	public static void initKeyBindings() {
		ClientRegistry.registerKeyBinding(ALT_ITEM_USE_1);
		ClientRegistry.registerKeyBinding(ALT_ITEM_USE_2);
		ClientRegistry.registerKeyBinding(ALT_ITEM_USE_3);
		ClientRegistry.registerKeyBinding(ALT_ITEM_USE_4);
		ClientRegistry.registerKeyBinding(ALT_ITEM_USE_5);

		initCallbacks();
	}

	private static void initCallbacks() {
		registerCallBack(ALT_ITEM_USE_1, new ItemInputCallback(ItemAltFunction.ALT_FUNCTION_1));
		registerCallBack(ALT_ITEM_USE_2, new ItemInputCallback(ItemAltFunction.ALT_FUNCTION_2));
		registerCallBack(ALT_ITEM_USE_3, new ItemInputCallback(ItemAltFunction.ALT_FUNCTION_3));
		registerCallBack(ALT_ITEM_USE_4, new ItemInputCallback(ItemAltFunction.ALT_FUNCTION_4));
		registerCallBack(ALT_ITEM_USE_5, new ItemInputCallback(ItemAltFunction.ALT_FUNCTION_5));
	}

	public static void registerCallBack(KeyBinding keyBinding, IInputCallback callback) {
		Predicate<InputCallbackDispatcher> matchingKeyBinding = d -> d.getKeyBinding().equals(keyBinding);
		if (keybindingCallbacks.stream().anyMatch(matchingKeyBinding)) {
			keybindingCallbacks.stream().filter(matchingKeyBinding).findFirst().ifPresent(d -> d.addInputCallback(callback));
		} else {
			keybindingCallbacks.add(new InputCallbackDispatcher(keyBinding, callback));
		}
	}

	@SubscribeEvent
	public void onKeyInput(KeyInputEvent evt) {
		Minecraft minecraft = Minecraft.getMinecraft();
		EntityPlayer player = minecraft.player;
		if (player == null) {
			return;
		}

		boolean state = Keyboard.getEventKeyState();

		if (state) {
			keybindingCallbacks.stream().filter(k -> k.getKeyBinding().isKeyDown()).forEach(InputCallbackDispatcher::onKeyPressed);
		}
	}

}
