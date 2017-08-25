package net.shadowmage.ancientwarfare.core.proxy;

import cpw.mods.fml.client.config.DummyConfigElement.DummyCategoryElement;
import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.client.config.GuiConfigEntries;
import cpw.mods.fml.client.config.GuiConfigEntries.CategoryEntry;
import cpw.mods.fml.client.config.IConfigElement;
import cpw.mods.fml.client.registry.ClientRegistry;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.Item;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.shadowmage.ancientwarfare.core.api.AWBlocks;
import net.shadowmage.ancientwarfare.core.config.AWCoreStatics;
import net.shadowmage.ancientwarfare.core.config.ConfigManager;
import net.shadowmage.ancientwarfare.core.gui.GuiBackpack;
import net.shadowmage.ancientwarfare.core.gui.GuiResearchBook;
import net.shadowmage.ancientwarfare.core.gui.crafting.GuiEngineeringStation;
import net.shadowmage.ancientwarfare.core.gui.research.GuiResearchStation;
import net.shadowmage.ancientwarfare.core.input.InputHandler;
import net.shadowmage.ancientwarfare.core.model.crafting_table.ModelEngineeringStation;
import net.shadowmage.ancientwarfare.core.model.crafting_table.ModelResearchStation;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.render.TileCraftingTableRender;
import net.shadowmage.ancientwarfare.core.tile.TileEngineeringStation;
import net.shadowmage.ancientwarfare.core.tile.TileResearchStation;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import java.util.ArrayList;
import java.util.List;

/**
 * client-proxy for AW-Core
 *
 * @author Shadowmage
 */
public class ClientProxy extends ClientProxyBase {

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public void registerClient() {
        FMLCommonHandler.instance().bus().register(InputHandler.instance);
        NetworkHandler.registerGui(NetworkHandler.GUI_CRAFTING, GuiEngineeringStation.class);
        NetworkHandler.registerGui(NetworkHandler.GUI_RESEARCH_STATION, GuiResearchStation.class);
        NetworkHandler.registerGui(NetworkHandler.GUI_BACKPACK, GuiBackpack.class);
        NetworkHandler.registerGui(NetworkHandler.GUI_RESEARCH_BOOK, GuiResearchBook.class);
        InputHandler.instance.loadConfig();

        TileCraftingTableRender render = new TileCraftingTableRender(new ModelEngineeringStation(), "textures/model/core/tile_engineering_station.png");
        ClientRegistry.bindTileEntitySpecialRenderer(TileEngineeringStation.class, render);
        MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(AWBlocks.engineeringStation), render);

        render = new TileCraftingTableRender(new ModelResearchStation(), "textures/model/core/tile_research_station.png");
        ClientRegistry.bindTileEntitySpecialRenderer(TileResearchStation.class, render);
        MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(AWBlocks.researchStation), render);

        ConfigManager.registerConfigCategory(new DummyCategoryElement("awconfig.core_keybinds", "awconfig.core_keybinds", KeybindCategoryEntry.class));

        if (AWCoreStatics.DEBUG) {
            setDebugResolution();
        }
    }

    public void setDebugResolution() {
        org.lwjgl.opengl.DisplayMode mode = new DisplayMode(512, 288);
        try {
            Display.setDisplayMode(mode);
        } catch (LWJGLException e) {
            e.printStackTrace();
        }
    }

    public void onConfigChanged() {
        InputHandler.instance.updateFromConfig();
    }

    public static final class KeybindCategoryEntry extends CategoryEntry {

        @SuppressWarnings("rawtypes")
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
            List<IConfigElement> list = new ArrayList<IConfigElement>();
            for(Property property : props) {
                list.add(new ConfigElement(property));
            }
            return list;
        }

    }

}
