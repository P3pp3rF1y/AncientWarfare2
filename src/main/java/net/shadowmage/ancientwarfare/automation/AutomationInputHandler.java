package net.shadowmage.ancientwarfare.automation;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.automation.proxy.ClientProxyAutomation.AutomationCategory;
import net.shadowmage.ancientwarfare.core.input.InputHandler;
import org.lwjgl.input.Keyboard;

@SideOnly(Side.CLIENT)
public class AutomationInputHandler {
	private static final String KEYBIND_CATEGORY = "keybind.category.awAutomation";
	private static final String KEY_TOGGLE_WORKBOUNDS_RENDER = "keybind.awAutomation.toggle_workbounds_render";

	public static final KeyBinding TOGGLE_WORKBOUNDS_RENDER = new KeyBinding(KEY_TOGGLE_WORKBOUNDS_RENDER, KeyConflictContext.IN_GAME, Keyboard.KEY_F8,
			KEYBIND_CATEGORY);

	public static void initKeyBindings() {
		ClientRegistry.registerKeyBinding(TOGGLE_WORKBOUNDS_RENDER);

		InputHandler.registerCallBack(TOGGLE_WORKBOUNDS_RENDER, AutomationInputHandler::toggleWorkboundsConfigValue);
	}

	private static void toggleWorkboundsConfigValue() {
		String newValue = "true";
		if ("true".equals(AutomationCategory.renderWorkBounds.get()))
			newValue = "false";
		AutomationCategory.renderWorkBounds.set(newValue);

		if (AncientWarfareAutomation.statics.getConfig().hasChanged())
			AncientWarfareAutomation.statics.getConfig().save();
	}
}
