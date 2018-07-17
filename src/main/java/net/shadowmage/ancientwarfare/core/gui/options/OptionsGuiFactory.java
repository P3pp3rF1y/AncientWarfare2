package net.shadowmage.ancientwarfare.core.gui.options;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.core.config.ConfigManager;

import java.util.Collections;
import java.util.Set;

@SideOnly(Side.CLIENT)
public class OptionsGuiFactory implements IModGuiFactory {

	@Override
	public void initialize(Minecraft minecraftInstance) {
		//NOOP
	}

	@Override
	public boolean hasConfigGui() {
		return true;
	}

	@Override
	public GuiScreen createConfigGui(GuiScreen parentScreen) {
		return new OptionsGui(parentScreen);
	}

	@Override
	public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
		return Collections.emptySet();
	}

	private static final class OptionsGui extends GuiConfig {
		private OptionsGui(GuiScreen parentScreen) {
			super(parentScreen, ConfigManager.getConfigElements(), AncientWarfareCore.MOD_ID, "AWConfig", false, false, "awconfig.mod_name", "awconfig.config_name");
		}
	}

}
