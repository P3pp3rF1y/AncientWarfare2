package net.shadowmage.ancientwarfare.automation;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.StatCollector;
import net.shadowmage.ancientwarfare.automation.proxy.ClientProxyAutomation.AutomationCategory;

public class KeyHandler {
    public static final int TOGGLE_WORKBOUNDS_RENDER = 0;
    
    private final Minecraft mc;
    
    private static final String[] desc = {
            "awconfig.keybind.toggle_workbounds_render"
    };
    
    private static final int[] keyValues = {
            Keyboard.KEY_F8
    };
    
    /** Make this public or provide a getter if you'll need access to the key bindings from elsewhere */
    public static final KeyBinding[] keys = new KeyBinding[desc.length];
    
    public KeyHandler(Minecraft mc) {
        this.mc = mc;
        for (int i = 0; i < desc.length; ++i) {
            keys[i] = new KeyBinding(desc[i], keyValues[i], StatCollector.translateToLocal("awconfig.mod_name"));
            ClientRegistry.registerKeyBinding(keys[i]);
        }
    }
    
    @SubscribeEvent
    public void onKeyInput(KeyInputEvent event) {
        if (mc.inGameHasFocus) {
            if (keys[TOGGLE_WORKBOUNDS_RENDER].getIsKeyPressed()) {
                String newValue = "true";
                if (AutomationCategory.renderWorkBounds.get().equals("true"))
                    newValue = "false";
                AutomationCategory.renderWorkBounds.set(newValue);
                
                if (AncientWarfareAutomation.statics.getConfig().hasChanged())
                    AncientWarfareAutomation.statics.getConfig().save();
            }
        }
    }
}
