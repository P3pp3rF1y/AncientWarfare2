package net.shadowmage.ancientwarfare.structure.town;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.shadowmage.ancientwarfare.structure.AncientWarfareStructures;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

	public List<TownTemplate> getTemplatesValidAtPosition(World world, int x, int z) {
		//noinspection ConstantConditions
		Biome biome = world.provider.getBiomeForCoords(new BlockPos(x, 1, z));
		ResourceLocation rl = biome.getRegistryName();
		if (rl == null) {
			AncientWarfareStructures.log.info("Biome based on class {} isn't registered and because of that Ancient Warfare can't process biome validation. This may be an error which may need to be fixed by the mod that added the biome.", biome.getClass());
			return Collections.emptyList();
		}
		String biomeName = rl.toString();
		return templates.values().stream().filter(t -> isDimensionValid(world.provider.getDimension(), t) && isBiomeValid(biomeName, t)).collect(Collectors.toList());
	}

	public TownTemplate selectTemplateFittingArea(World world, TownBoundingArea area, List<TownTemplate> templates) {
		TownTemplate selection = null;
		int width = area.getChunkWidth();
		int length = area.getChunkLength();

		int min = Math.min(width, length);
		int templateMinimumSize;

		int totalWeight = 0;
		for (TownTemplate t : templates) {
			templateMinimumSize = t.getMinSize();
			if (min >= templateMinimumSize) {
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
