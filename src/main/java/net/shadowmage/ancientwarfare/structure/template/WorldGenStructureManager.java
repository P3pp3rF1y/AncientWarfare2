package net.shadowmage.ancientwarfare.structure.template;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.Loader;
import net.shadowmage.ancientwarfare.core.gamedata.AWGameData;
import net.shadowmage.ancientwarfare.structure.AncientWarfareStructure;
import net.shadowmage.ancientwarfare.structure.config.AWStructureStatics;
import net.shadowmage.ancientwarfare.structure.gamedata.StructureEntry;
import net.shadowmage.ancientwarfare.structure.gamedata.StructureMap;
import net.shadowmage.ancientwarfare.structure.registry.BiomeGroupRegistry;
import net.shadowmage.ancientwarfare.structure.template.build.validation.StructureValidator;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class WorldGenStructureManager {

	private HashMap<String, Set<StructureTemplate>> templatesByBiome = new HashMap<>();
	/*
	 * cached list objects, used for temp searching, as to not allocate new lists for every chunk-generated....
	 */
	private List<StructureEntry> searchCache = new ArrayList<>();
	private List<StructureTemplate> trimmedPotentialStructures = new ArrayList<>();
	private HashMap<String, Integer> distancesFound = new HashMap<>();
	BlockPos rearBorderPos = BlockPos.ORIGIN;

	public static final WorldGenStructureManager INSTANCE = new WorldGenStructureManager();

	private WorldGenStructureManager() {
	}

	public void loadBiomeList() {
		for (Biome biome : Biome.REGISTRY) {
			if (biome == null) {
				continue;
			}
			templatesByBiome.put(biome.getRegistryName().toString(), new HashSet<>());
		}
	}

	public void registerWorldGenStructure(StructureTemplate template) {
		StructureValidator validation = template.getValidationSettings();
		Set<String> biomes = validation.getBiomeList();
		Set<String> biomeGroupBiomes = new HashSet<>();
		validation.getBiomeGroupList().forEach(biomeGroup -> biomeGroupBiomes.addAll(BiomeGroupRegistry.getGroupBiomes(biomeGroup)));

		if (validation.isBiomeWhiteList()) {
			whitelistBiomes(template, biomes, biomeGroupBiomes);
		} else {
			blacklistBiomes(template, biomes, biomeGroupBiomes);
		}
	}

	private void whitelistBiomes(StructureTemplate template, Set<String> biomes, Set<String> biomeGroupBiomes) {
		for (String biome : biomes) {
			if (templatesByBiome.containsKey(biome)) {
				if (biomeGroupBiomes.isEmpty() || biomeGroupBiomes.contains(biome)) {
					templatesByBiome.get(biome).add(template);
				}
			} else if (Loader.isModLoaded((new ResourceLocation(biome)).getResourceDomain())) {
				AncientWarfareStructure.LOG.warn("Could not locate biome: {} while registering template: {} for world generation.", biome, template.name);
			}
		}
	}

	private void blacklistBiomes(StructureTemplate template, Set<String> biomes, Set<String> biomeGroupBiomes) {
		Set<String> biomesBaseList = biomeGroupBiomes.isEmpty() ? templatesByBiome.keySet() : biomeGroupBiomes;
		for (String biome : biomesBaseList) {
			if (!biomes.isEmpty() && biomes.contains(biome)) {
				continue;
			}
			templatesByBiome.get(biome).add(template);
		}
	}

	public StructureTemplate selectTemplateForGeneration(World world, Random rng, int x, int y, int z, EnumFacing face) {
		searchCache.clear();
		trimmedPotentialStructures.clear();
		distancesFound.clear();
		StructureMap map = AWGameData.INSTANCE.getData(world, StructureMap.class);
		if (map == null) {
			return null;
		}
		int foundValue = 0;
		int chunkDistance;
		float foundDistance;

		Biome biome = world.provider.getBiomeForCoords(new BlockPos(x, 1, z));
		//noinspection ConstantConditions
		String biomeName = biome.getRegistryName().toString();
		Collection<StructureEntry> duplicateSearchEntries = map.getEntriesNear(world, x, z, AWStructureStatics.duplicateStructureSearchRange, false, searchCache);
		for (StructureEntry entry : duplicateSearchEntries) {
			int mx = entry.getBB().getCenterX() - x;
			int mz = entry.getBB().getCenterZ() - z;
			foundDistance = MathHelper.sqrt((float) mx * mx + mz * mz);
			chunkDistance = (int) (foundDistance / 16.f);
			if (distancesFound.containsKey(entry.getName())) {
				int dist = distancesFound.get(entry.getName());
				if (chunkDistance < dist) {
					distancesFound.put(entry.getName(), chunkDistance);
				}
			} else {
				distancesFound.put(entry.getName(), chunkDistance);
			}
		}

		Collection<StructureEntry> clusterValueSearchEntries = map.getEntriesNear(world, x, z, AWStructureStatics.clusterValueSearchRange, false, searchCache);
		for (StructureEntry entry : clusterValueSearchEntries) {
			foundValue += entry.getValue();
		}
		Set<StructureTemplate> potentialStructures = templatesByBiome.get(biomeName);
		if (potentialStructures == null || potentialStructures.isEmpty()) {
			return null;
		}

		int remainingValueCache = AWStructureStatics.maxClusterValue - foundValue;
		StructureValidator settings;
		int dim = world.provider.getDimension();
		for (StructureTemplate template : potentialStructures)//loop through initial structures, only adding to 2nd list those which meet biome, unique, value, and minDuplicate distance settings
		{
			settings = template.getValidationSettings();

			boolean dimensionMatch = !settings.isDimensionWhiteList();
			for (int i = 0; i < settings.getAcceptedDimensions().length; i++) {
				int dimTest = settings.getAcceptedDimensions()[i];
				if (dimTest == dim) {
					dimensionMatch = !dimensionMatch;
					break;
				}
			}
			if (!dimensionMatch)//skip if dimension is blacklisted, or not present on whitelist
			{
				continue;
			}
			if (settings.isUnique() && map.isGeneratedUnique(template.name)) {
				continue;
			}//skip already generated uniques
			if (settings.getClusterValue() > remainingValueCache) {
				continue;
			}//skip if cluster value is to high to place in given area
			if (distancesFound.containsKey(template.name)) {
				int dist = distancesFound.get(template.name);
				if (dist < settings.getMinDuplicateDistance()) {
					continue;
				}//skip if minDuplicate distance is not met
			}
			if (!settings.shouldIncludeForSelection(world, x, y, z, face, template)) {
				continue;
			}
			trimmedPotentialStructures.add(template);
		}
		if (trimmedPotentialStructures.isEmpty()) {
			return null;
		}
		StructureTemplate toReturn = getWeightedRandomStructure(rng);
		distancesFound.clear();
		trimmedPotentialStructures.clear();
		return toReturn;
	}

	@Nullable
	private StructureTemplate getWeightedRandomStructure(Random rng) {
		int totalWeight = 0;
		for (StructureTemplate t : trimmedPotentialStructures) {
			totalWeight += t.getValidationSettings().getSelectionWeight();
		}
		int rnd = rng.nextInt(totalWeight + 1);
		StructureTemplate toReturn = null;
		for (StructureTemplate t : trimmedPotentialStructures) {
			rnd -= t.getValidationSettings().getSelectionWeight();
			if (rnd <= 0) {
				toReturn = t;
				break;
			}
		}
		return toReturn;
	}
}
