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
import net.shadowmage.ancientwarfare.structure.registry.TerritorySettingRegistry;
import net.shadowmage.ancientwarfare.structure.template.build.validation.StructureValidator;
import net.shadowmage.ancientwarfare.structure.util.CollectionUtils;
import net.shadowmage.ancientwarfare.structure.worldgen.Territory;
import net.shadowmage.ancientwarfare.structure.worldgen.TerritoryManager;
import net.shadowmage.ancientwarfare.structure.worldgen.WorldGenDetailedLogHelper;
import net.shadowmage.ancientwarfare.structure.worldgen.stats.ValidationRejectionReason;
import net.shadowmage.ancientwarfare.structure.worldgen.stats.WorldGenStatistics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.StringJoiner;
import java.util.function.Predicate;

import static net.shadowmage.ancientwarfare.structure.template.build.validation.properties.StructureValidationProperties.TERRITORY_NAME;

public class WorldGenStructureManager {
	public static final String GENERIC_TERRITORY_NAME = "generic";
	private HashMap<String, Set<StructureTemplate>> templatesByBiome = new HashMap<>();
	private HashMap<String, Set<StructureTemplate>> templatesByTerritoryName = new HashMap<>();

	/*
	 * cached list objects, used for temp searching, as to not allocate new lists for every chunk-generated....
	 */
	private List<StructureEntry> searchCache = new ArrayList<>();
	private List<StructureTemplate> trimmedPotentialStructures = new ArrayList<>();
	private HashMap<String, Integer> distancesFound = new HashMap<>();

	public static final WorldGenStructureManager INSTANCE = new WorldGenStructureManager();

	private WorldGenStructureManager() {
	}

	public void clearCachedTemplates() {
		templatesByBiome.clear();
		templatesByTerritoryName.clear();
	}

	public void loadBiomeList() {
		for (Biome biome : Biome.REGISTRY) {
			if (biome == null) {
				continue;
			}
			//noinspection ConstantConditions
			templatesByBiome.put(biome.getRegistryName().toString(), new HashSet<>());
		}
	}

	void registerWorldGenStructure(StructureTemplate template) {
		StructureValidator validation = template.getValidationSettings();
		Set<String> biomes = validation.getBiomeList();
		Set<String> biomeGroupBiomes = new HashSet<>();
		validation.getBiomeGroupList().forEach(biomeGroup -> biomeGroupBiomes.addAll(BiomeGroupRegistry.getGroupBiomes(biomeGroup)));

		String territoryName = template.getValidationSettings().getPropertyValue(TERRITORY_NAME);
		if (territoryName.isEmpty()) {
			territoryName = GENERIC_TERRITORY_NAME;
		}
		Set<StructureTemplate> templates = templatesByTerritoryName.getOrDefault(territoryName, new HashSet<>());
		templates.add(template);
		templatesByTerritoryName.put(territoryName, templates);

		if (validation.isBiomeWhiteList()) {
			whitelistBiomes(template, biomes, biomeGroupBiomes, territoryName);
		} else {
			blacklistBiomes(template, biomes, biomeGroupBiomes, territoryName);
		}
	}

	private void whitelistBiomes(StructureTemplate template, Set<String> biomes, Set<String> biomeGroupBiomes, String territoryName) {
		addTemplateToBiomes(template, biomeGroupBiomes, b -> true, territoryName);
		addTemplateToBiomes(template, biomes, b -> biomeGroupBiomes.isEmpty() || biomeGroupBiomes.contains(b), territoryName);
	}

	private void addTemplateToBiomes(StructureTemplate template, Set<String> biomeGroupBiomes, Predicate<String> checkBiome, String territoryName) {
		for (String biomeName : biomeGroupBiomes) {
			if (templatesByBiome.containsKey(biomeName) && checkBiome.test(biomeName)) {
				addBiomeTemplate(template, territoryName, biomeName);
			} else if (Loader.isModLoaded((new ResourceLocation(biomeName)).getResourceDomain())) {
				AncientWarfareStructure.LOG.warn("Could not locate biome: {} while registering template: {} for world generation.", biomeName, template.name);
			}
		}
	}

	private void addBiomeTemplate(StructureTemplate template, String territoryName, String biomeName) {
		templatesByBiome.get(biomeName).add(template);
		TerritoryManager.addTerritoryInBiome(territoryName, biomeName);
	}

	private void blacklistBiomes(StructureTemplate template, Set<String> biomes, Set<String> biomeGroupBiomes, String territoryName) {
		Set<String> biomesBaseList = biomeGroupBiomes.isEmpty() ? templatesByBiome.keySet() : biomeGroupBiomes;
		for (String biome : biomesBaseList) {
			if (!biomes.isEmpty() && biomes.contains(biome)) {
				continue;
			}
			if (templatesByBiome.containsKey(biome)) {
				addBiomeTemplate(template, territoryName, biome);
			}
		}
	}

	public Optional<StructureTemplate> selectTemplateForGeneration(World world, Random rng, int x, int y, int z, EnumFacing face, Territory territory) {
		searchCache.clear();
		trimmedPotentialStructures.clear();
		distancesFound.clear();
		StructureMap map = AWGameData.INSTANCE.getPerWorldData(world, StructureMap.class);

		Biome biome = world.provider.getBiomeForCoords(new BlockPos(x, 1, z));

		//noinspection ConstantConditions
		String biomeName = biome.getRegistryName().toString();
		Set<StructureTemplate> potentialStructures = new HashSet<>();
		getTerritoryTemplates(territory.getTerritoryName()).ifPresent(potentialStructures::addAll);
		getTerritoryTemplates(GENERIC_TERRITORY_NAME).ifPresent(potentialStructures::addAll);
		Set<StructureTemplate> biomeTemplates = templatesByBiome.get(biomeName);
		potentialStructures.removeIf(t -> !biomeTemplates.contains(t));
		if (potentialStructures.isEmpty()) {
			return Optional.empty();
		}

		int remainingValueCache = territory.getRemainingClusterValue();

		WorldGenDetailedLogHelper.log("Selecting template at x {} z {} in biome \"{}\" and territory \"{}\" with remaining cluster value of {}",
				() -> x, () -> z, () -> biomeName, territory::getTerritoryName, territory::getRemainingClusterValue);

		int duplicateSearchDistance = 0;
		for (StructureTemplate template : potentialStructures) {
			duplicateSearchDistance = Math.max(duplicateSearchDistance, template.getValidationSettings().getMinDuplicateDistance());
		}

		Collection<StructureEntry> duplicateSearchEntries = map.getEntriesNear(world, x, z, duplicateSearchDistance / 16, false, searchCache);
		for (StructureEntry entry : duplicateSearchEntries) {
			int mx = entry.getBB().getCenterX() - x;
			int mz = entry.getBB().getCenterZ() - z;
			int foundDistance = (int) MathHelper.sqrt((float) mx * mx + mz * mz);
			if (distancesFound.containsKey(entry.getName())) {
				int dist = distancesFound.get(entry.getName());
				if (foundDistance < dist) {
					distancesFound.put(entry.getName(), foundDistance);
				}
			} else {
				distancesFound.put(entry.getName(), foundDistance);
			}
		}

		int dim = world.provider.getDimension();
		for (StructureTemplate template : potentialStructures)//loop through initial structures, only adding to 2nd list those which meet biome, unique, value, and minDuplicate distance settings
		{
			if (validateTemplate(world, x, y, z, face, map, remainingValueCache, dim, template)) {
				trimmedPotentialStructures.add(template);
			}
		}
		if (trimmedPotentialStructures.isEmpty()) {
			return Optional.empty();
		}
		StructureTemplate toReturn = CollectionUtils.getWeightedRandomElement(rng, trimmedPotentialStructures, e -> getStructureWeight(x, y, z, territory, e),
				(totalWeight, selected) -> {
					WorldGenDetailedLogHelper.log("Out of total of {} structures with weight total of {} structure \"{}\" with weight {} was selected",
							trimmedPotentialStructures::size, () -> totalWeight, () -> selected == null ? "" : selected.name,
							() -> selected == null ? "" : getStructureWeight(x, y, z, territory, selected));
					WorldGenDetailedLogHelper.log("Following structures and weights were considered: \n {}",
							() -> {
								StringJoiner joiner = new StringJoiner(", ");
								trimmedPotentialStructures.forEach(structure -> joiner.add(structure.name + ":" + getStructureWeight(x, y, z, territory, structure)));
								return joiner.toString();
							});
					trimmedPotentialStructures.forEach(structure -> WorldGenStatistics.recordStructureConsideredInRandom(structure.name, getStructureWeight(x, y, z, territory, structure), totalWeight, biomeName, territory.getTerritoryName()));
				}
		).orElse(null);
		distancesFound.clear();
		trimmedPotentialStructures.clear();
		return Optional.ofNullable(toReturn);
	}

	private int getStructureWeight(int x, int y, int z, Territory territory, StructureTemplate e) {
		boolean bigStructure = e.getValidationSettings().getClusterValue() > 50;
		int weight = e.getValidationSettings().getSelectionWeight();
		if (bigStructure) {
			return Math.max(0, (int) (weight - ((territory.getTerritoryCenter().distanceSq(x, y, z) /
					TerritorySettingRegistry.getMaxTerritoryCenterDistanceSq(territory.getTerritoryName())) * weight)));
		}
		return weight;
	}

	private boolean validateTemplate(World world, int x, int y, int z, EnumFacing face, StructureMap map, int remainingValueCache, int dim, StructureTemplate template) {
		StructureValidator settings = template.getValidationSettings();
		boolean dimensionMatch = !settings.isDimensionWhiteList();
		for (int i = 0; i < settings.getAcceptedDimensions().length; i++) {
			int dimTest = settings.getAcceptedDimensions()[i];
			if (dimTest == dim) {
				dimensionMatch = !dimensionMatch;
				break;
			}
		}
		if (!dimensionMatch) {
			WorldGenStatistics.addStructureValidationRejection(template.name, ValidationRejectionReason.WRONG_DIMENSION);
			WorldGenDetailedLogHelper.log("Structure \"{}\" is defined for different dimension", () -> template.name);
			return false;
		}
		if (settings.isUnique() && map.isGeneratedUnique(template.name)) {
			WorldGenStatistics.addStructureValidationRejection(template.name, ValidationRejectionReason.UNIQUE_ALREADY_GENERATED);
			WorldGenDetailedLogHelper.log("Unique structure \"{}\" already generated", () -> template.name);
			return false;
		}
		if (settings.getClusterValue() > remainingValueCache) {
			WorldGenStatistics.addStructureValidationRejection(template.name, ValidationRejectionReason.TOO_HIGH_CLUSTER_VALUE);
			WorldGenDetailedLogHelper.log("Structure \"{}\" has too high cluster value {}", () -> template.name, () -> template.getValidationSettings().getClusterValue());
			return false;
		}
		if (distancesFound.containsKey(template.name)) {
			int dist = distancesFound.get(template.name);
			if (dist < settings.getMinDuplicateDistance()) {
				WorldGenStatistics.addStructureValidationRejection(template.name, ValidationRejectionReason.DUPLICATE_IN_RANGE);
				WorldGenDetailedLogHelper.log("Structure \"{}\" has duplicate {} blocks away", () -> template.name, () -> dist);
				return false;
			}
		}
		return settings.shouldIncludeForSelection(world, x, y, z, face, template);
	}

	public Optional<Set<StructureTemplate>> getTerritoryTemplates(String territoryName) {
		return Optional.ofNullable(templatesByTerritoryName.get(territoryName));
	}
}
