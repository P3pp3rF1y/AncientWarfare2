package net.shadowmage.ancientwarfare.core.config;

import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.client.config.IConfigElement;

public class ConfigManager
{

@SuppressWarnings("rawtypes")
private static List<IConfigElement> configElements = new ArrayList<IConfigElement>();

public static void registerConfigCategory(IConfigElement<?> c)
  {
  configElements.add(c);
  }

@SuppressWarnings("rawtypes")
public static List<IConfigElement> getConfigElements()
  {
  return configElements;
  }

}
