package net.shadowmage.ancientwarfare.automation;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.shadowmage.ancientwarfare.automation.proxy.ClientProxyAutomation.AutomationCategory;
import org.lwjgl.input.Keyboard;

public class KeyHandler {
    //TODO refactor to one common way - keybindings in core with call back handlers
    public static final int TOGGLE_WORKBOUNDS_RENDER = 0;
    
    private final Minecraft mc;
    
    private static final String[] desc = {
            "awconfig.keybind.toggle_workbounds_render"
    };
    
    private static final int[] keyValues = {
            Keyboard.KEY_F8
    };
    
    /* Make this public or provide a getter if you'll need access to the key bindings from elsewhere */
    public static final KeyBinding[] keys = new KeyBinding[desc.length];
    
    public KeyHandler(Minecraft mc) {
        this.mc = mc;
        for (int i = 0; i < desc.length; ++i) {
            keys[i] = new KeyBinding(desc[i], keyValues[i], I18n.format("awconfig.mod_name"));
            ClientRegistry.registerKeyBinding(keys[i]);
        }
    }
    
    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (mc.inGameHasFocus) {
            if (keys[TOGGLE_WORKBOUNDS_RENDER].isKeyDown()) {
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
