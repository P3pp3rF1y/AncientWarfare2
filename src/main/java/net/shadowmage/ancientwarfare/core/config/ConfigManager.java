package net.shadowmage.ancientwarfare.core.config;

import net.minecraftforge.fml.client.config.IConfigElement;

import java.util.ArrayList;
import java.util.List;

public class ConfigManager {


    private static List<IConfigElement> configElements = new ArrayList<>();

    public static void registerConfigCategory(IConfigElement c) {
        configElements.add(c);
    }


    public static List<IConfigElement> getConfigElements() {
        return configElements;
    }

}
