package net.shadowmage.ancientwarfare.structure.town;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

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

	public Optional<TownTemplate> getTemplate(String name) {
		return Optional.ofNullable(templates.get(name));
	}

	public Collection<TownTemplate> getTemplates() {
		return templates.values();
	}

	public TownTemplate selectTemplateForGeneration(World world, int x, int z, TownBoundingArea area) {
		TownTemplate selection = null;
		int width = area.getChunkWidth();
		int length = area.getChunkLength();

		int min = Math.min(width, length);
		int templateMinimumSize;

		//noinspection ConstantConditions
		String biomeName = world.getBiome(new BlockPos(x, 1, z)).getRegistryName().toString();
		int totalWeight = 0;
		for (TownTemplate t : templates.values()) {
			templateMinimumSize = t.getMinSize();
			if (min >= templateMinimumSize && isDimensionValid(world.provider.getDimension(), t) && isBiomeValid(biomeName, t)) {
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

	private boolean isDimensionValid(int dimension, TownTemplate t) {
		return t.getDimensionList().contains(dimension) == t.isDimensionWhiteList();
	}

	public void removeAll() {
		templates.clear();
	}
}
