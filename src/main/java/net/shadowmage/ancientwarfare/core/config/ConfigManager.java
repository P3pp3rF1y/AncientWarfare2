package net.shadowmage.ancientwarfare.core.config;

import cpw.mods.fml.client.config.IConfigElement;

import java.util.ArrayList;
import java.util.List;

public class ConfigManager {

    @SuppressWarnings("rawtypes")
    private static List<IConfigElement> configElements = new ArrayList<IConfigElement>();

    public static void registerConfigCategory(IConfigElement<?> c) {
        configElements.add(c);
    }

    @SuppressWarnings("rawtypes")
    public static List<IConfigElement> getConfigElements() {
        return configElements;
    }

}
