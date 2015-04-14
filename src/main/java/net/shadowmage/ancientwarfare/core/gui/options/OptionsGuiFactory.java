package net.shadowmage.ancientwarfare.core.gui.options;

import cpw.mods.fml.client.IModGuiFactory;
import cpw.mods.fml.client.config.GuiConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.core.config.ConfigManager;

import java.util.Set;

public class OptionsGuiFactory implements IModGuiFactory {

    public OptionsGuiFactory() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public void initialize(Minecraft minecraftInstance) {
    }

    @Override
    public Class<? extends GuiScreen> mainConfigGuiClass() {
//  AWLog.logDebug("CALL TO RETRIEVE MAIN CONFIG GUI CLASS!!!");
        return OptionsGui.class;
    }

    @Override
    public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
//  AWLog.logDebug("CALL TO RETRIEVE MAIN CONFIG RUNTIME CATEGORIES!!!");
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public RuntimeOptionGuiHandler getHandlerFor(RuntimeOptionCategoryElement element) {
//  AWLog.logDebug("CALL TO RETRIEVE HANDLER FOR CONFIG RUNTIME CATEGORIES!!!");
        // TODO Auto-generated method stub
        return null;
    }

    public static final class OptionsGui extends GuiConfig {
        public OptionsGui(GuiScreen parentScreen) {
            super(parentScreen, ConfigManager.getConfigElements(), AncientWarfareCore.modID, "AWConfig", false, false, "awconfig.mod_name", "awconfig.config_name");
        }
    }

}
