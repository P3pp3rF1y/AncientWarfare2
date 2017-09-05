package net.shadowmage.ancientwarfare.structure.town;

import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.structure.config.AWStructureStatics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TownTemplateManager {

    public static final TownTemplateManager INSTANCE = new TownTemplateManager();

    private final HashMap<String, TownTemplate> templates;
    private final List<TownTemplate> searchCache;

    private TownTemplateManager() {
        templates = new HashMap<>();
        searchCache = new ArrayList<>();
    }

    public void loadTemplate(TownTemplate template) {
        templates.put(template.getTownTypeName(), template);
    }

    public TownTemplate getTemplate(String name) {
        return templates.get(name);
    }

    public TownTemplate selectTemplateForGeneration(World world, int x, int z, TownBoundingArea area) {
        TownTemplate selection = null;
        int width = area.getChunkWidth();
        int length = area.getChunkLength();

        int min = Math.min(width, length);
        int templateMinimumSize;


        String biomeName = AWStructureStatics.getBiomeName(world.getBiomeGenForCoords(x, z));
        int totalWeight = 0;
        for (TownTemplate t : templates.values()) {
            templateMinimumSize = t.getMinSize();
            if (min >= templateMinimumSize && isBiomeValid(biomeName, t)) {
                searchCache.add(t);
                totalWeight += t.getSelectionWeight();
            }
        }
        if (!searchCache.isEmpty() && totalWeight > 0) {
            totalWeight = world.rand.nextInt(totalWeight);
            for (TownTemplate t : searchCache) {
                totalWeight -= t.getSelectionWeight();
                if (totalWeight < 0) {
                    selection = t;
                    break;
                }
            }
        }
        searchCache.clear();
        return selection;
    }

    private boolean isBiomeValid(String biome, TownTemplate t) {
        boolean contains = t.getBiomeList().contains(biome);
        boolean wl = t.isBiomeWhiteList();
        return (wl && contains) || (!wl && !contains);
    }

}
