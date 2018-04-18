package net.shadowmage.ancientwarfare.core.proxy;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.config.DummyConfigElement.DummyCategoryElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.GuiConfigEntries;
import net.minecraftforge.fml.client.config.GuiConfigEntries.CategoryEntry;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.core.config.AWCoreStatics;
import net.shadowmage.ancientwarfare.core.config.ConfigManager;
import net.shadowmage.ancientwarfare.core.input.InputHandler;
import net.shadowmage.ancientwarfare.core.render.EngineeringStationRenderer;
import net.shadowmage.ancientwarfare.core.render.ResearchStationRenderer;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import java.util.ArrayList;
import java.util.List;

/*
 * client-proxy for AW-Core
 *
 * @author Shadowmage
 */
public class ClientProxy extends ClientProxyBase {

	@Override
	public void preInit() {
		super.preInit();

		MinecraftForge.EVENT_BUS.register(InputHandler.instance);
		MinecraftForge.EVENT_BUS.register(this);
		InputHandler.instance.loadConfig();

		ConfigManager.registerConfigCategory(new DummyCategoryElement("awconfig.core_keybinds", "awconfig.core_keybinds", KeybindCategoryEntry.class));

		if (AWCoreStatics.DEBUG) {
			setDebugResolution();
		}
	}

	public void setDebugResolution() {
		org.lwjgl.opengl.DisplayMode mode = new DisplayMode(512, 288);
		try {
			Display.setDisplayMode(mode);
		}
		catch (LWJGLException e) {
			e.printStackTrace();
		}
	}

	public void onConfigChanged() {
		InputHandler.instance.updateFromConfig();
	}

	public static final class KeybindCategoryEntry extends CategoryEntry {

		public KeybindCategoryEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement configElement) {
			super(owningScreen, owningEntryList, configElement);
		}

		@Override
		protected GuiScreen buildChildScreen() {
			return new GuiConfig(this.owningScreen, getKeybindElements(), this.owningScreen.modID,
					owningScreen.allRequireWorldRestart || this.configElement.requiresWorldRestart(),
					owningScreen.allRequireMcRestart || this.configElement.requiresMcRestart(), this.owningScreen.title,
					((this.owningScreen.titleLine2 == null ? "" : this.owningScreen.titleLine2) + " > " + this.name));
		}

		private static List<IConfigElement> getKeybindElements() {
			List<Property> props = InputHandler.instance.getKeyConfig("item_use");
			List<IConfigElement> list = new ArrayList<>();
			for (Property property : props) {
				list.add(new ConfigElement(property));
			}
			return list;
		}

	}

	@SubscribeEvent
	public void onPreTextureStitch(TextureStitchEvent.Pre evt) {
		EngineeringStationRenderer.INSTANCE
				.setSprite(evt.getMap().registerSprite(new ResourceLocation(AncientWarfareCore.modID + ":model/core/tile_engineering_station")));
		ResearchStationRenderer.INSTANCE
				.setSprite(evt.getMap().registerSprite(new ResourceLocation(AncientWarfareCore.modID + ":model/core/tile_research_station")));
	}
}
