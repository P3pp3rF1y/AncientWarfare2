package net.shadowmage.ancientwarfare.core.gui.options;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.client.gui.ForgeGuiFactory.ForgeConfigGui.ChunkLoaderEntry;
import net.minecraftforge.client.gui.ForgeGuiFactory.ForgeConfigGui.GeneralEntry;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import cpw.mods.fml.client.IModGuiFactory;
import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.client.config.GuiConfigEntries;
import cpw.mods.fml.client.config.IConfigElement;
import cpw.mods.fml.client.config.DummyConfigElement.DummyCategoryElement;
import cpw.mods.fml.client.config.GuiConfigEntries.CategoryEntry;

public class OptionsGuiFactory implements IModGuiFactory
{

public OptionsGuiFactory()
  {
  // TODO Auto-generated constructor stub
  }

@Override
public void initialize(Minecraft minecraftInstance)
  {
  // TODO Auto-generated method stub
  AWLog.logDebug("INIT GUI FACTORY!!!");
  }

@Override
public Class<? extends GuiScreen> mainConfigGuiClass()
  {
  AWLog.logDebug("CALL TO RETRIEVE MAN CONFIG GUI CLASS!!!");
  return OptionsGui.class;
  }

@Override
public Set<RuntimeOptionCategoryElement> runtimeGuiCategories()
  {
  AWLog.logDebug("CALL TO RETRIEVE MAIN CONFIG RUNTIME CATEGORIES!!!");
  // TODO Auto-generated method stub
  return null;
  }

@Override
public RuntimeOptionGuiHandler getHandlerFor(RuntimeOptionCategoryElement element)
  {
  AWLog.logDebug("CALL TO RETRIEVE HANDLER FOR CONFIG RUNTIME CATEGORIES!!!");
  // TODO Auto-generated method stub
  return null;
  }

public static final class OptionsGui extends GuiConfig
{
public OptionsGui(GuiScreen parentScreen)
  {
  super(parentScreen, getElements(), "AncientWarfare", "wtfF00", false, false, "Ancient Warfare", "Client Side Config Options");
  }

private static List<IConfigElement> getElements()
  {
  List<IConfigElement> list = new ArrayList<IConfigElement>();
  list.add(new DummyCategoryElement("Keybinds", "wtf2", KeybindCategoryEntry.class));
  list.add(new DummyCategoryElement("Render Options", "wtf2", CategoryEntry.class));
  return list;
  }

}

public static final class KeybindCategoryEntry extends CategoryEntry
{

public KeybindCategoryEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement configElement)
  {
  super(owningScreen, owningEntryList, configElement);
  // TODO Auto-generated constructor stub
  }

@Override
protected GuiScreen buildChildScreen()
  {
  return new GuiConfig(this.owningScreen, getKeybindElements(), this.owningScreen.modID,
      owningScreen.allRequireWorldRestart || this.configElement.requiresWorldRestart(),
      owningScreen.allRequireMcRestart || this.configElement.requiresMcRestart(), this.owningScreen.title,
      ((this.owningScreen.titleLine2 == null ? "" : this.owningScreen.titleLine2) + " > " + this.name));
  }

private static List<IConfigElement> getKeybindElements()
  {
  List<IConfigElement> list = new ArrayList<IConfigElement>();
  Configuration config = AncientWarfareCore.config;
  list.add(new ConfigElement(config.get("05_keybinds", "keybind.alt_item_use_1", 44)));
  list.add(new ConfigElement(config.get("05_keybinds", "keybind.alt_item_use_2", 45)));
  list.add(new ConfigElement(config.get("05_keybinds", "keybind.alt_item_use_3", 46)));
  list.add(new ConfigElement(config.get("05_keybinds", "keybind.alt_item_use_4", 47)));
  list.add(new ConfigElement(config.get("05_keybinds", "keybind.alt_item_use_5", 48)));
  return list;
  }

}

}
