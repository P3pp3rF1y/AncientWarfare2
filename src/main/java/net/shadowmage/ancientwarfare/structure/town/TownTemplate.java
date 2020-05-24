package net.shadowmage.ancientwarfare.structure.town;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import net.shadowmage.ancientwarfare.structure.AncientWarfareStructure;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplateManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.StringJoiner;

public final class TownTemplate {
	private String townTypeName = "";//

	private boolean biomeWhiteList = false;//
	private List<String> biomeList = new ArrayList<>();//

	private boolean dimensionWhiteList;//
	private List<Integer> dimensionList = new ArrayList<>();//

	private int minSize = 3;//size in chunks//
	private int maxSize = 9;//size in chunks//

	private int selectionWeight = 0;

	private int clusterValue = 0;//value inserted into template gen map to discourage nearby random structure spawns

	/*
	 * the nominal size of a town-block, in blocks
	 */
	private int townBlockSize = 0;
	private int townPlotSize = 0;
	private int townBuildingWidthExpansion = 0;//used to expand buildings by a set size, for use with structure templates that have no surrounding space in them, to ensure room between structures

	private List<IBlockState> roadFillBlocks = new ArrayList<>();

	private int wallStyle = 0;//0==no wall, 1==corner only, 2==random walls, 3==by pattern//
	private int wallSize = 0;//size in blocks//

	private int exteriorSize = 0;//area outside of the walls, in blocks
	private int exteriorPlotSize = 0;//size of each plot in the area outside of the walls

	private int interiorEmtpyPlotChance = 0;

	private float randomVillagersPerChunk = 0;

	private HashMap<Integer, TownWallEntry> wallsByID = new HashMap<>();
	private HashMap<Integer, int[]> wallPatterns = new HashMap<>();

	private int cornersTotalWeight;
	private List<TownWallEntry> cornerWalls = new ArrayList<>();

	private int gatesCenterTotalWeight;
	private List<TownWallEntry> gateCenterWalls = new ArrayList<>();

	private int gatesLeftTotalWeight;
	private List<TownWallEntry> gateLeftWalls = new ArrayList<>();

	private int gatesRightTotalWeight;
	private List<TownWallEntry> gateRightWalls = new ArrayList<>();

	private int wallTotalWeights;
	private List<TownWallEntry> walls = new ArrayList<>();

	private TownStructureEntry lamp;//
	private List<TownStructureEntry> uniqueStructureEntries = new ArrayList<>();
	private List<TownStructureEntry> mainStructureEntries = new ArrayList<>();
	private List<TownStructureEntry> houseStructureEntries = new ArrayList<>();
	private List<TownStructureEntry> cosmeticStructureEntries = new ArrayList<>();
	private List<TownStructureEntry> exteriorStructureEntries = new ArrayList<>();
	private boolean preventNaturalHostileSpawns = false;
	private ResourceLocation biomeReplacement = null;
	private String territoryName = "";

	public void setTownTypeName(String townTypeName) {
		this.townTypeName = townTypeName;
	}

	public final String getTownTypeName() {
		return townTypeName;
	}

	public final boolean isBiomeWhiteList() {
		return biomeWhiteList;
	}

	public final void setBiomeWhiteList(boolean biomeWhiteList) {
		this.biomeWhiteList = biomeWhiteList;
	}

	public final List<String> getBiomeList() {
		return biomeList;
	}

	public final void setBiomeList(List<String> biomes) {
		this.biomeList = biomes;
	}

	public final boolean isDimensionWhiteList() {
		return dimensionWhiteList;
	}

	public final void setDimensionWhiteList(boolean dimensionWhiteList) {
		this.dimensionWhiteList = dimensionWhiteList;
	}

	public final List<Integer> getDimensionList() {
		return dimensionList;
	}

	public final void setDimensionList(List<Integer> dimensions) {
		this.dimensionList = dimensions;
	}

	public final List<TownStructureEntry> getUniqueStructureEntries() {
		return uniqueStructureEntries;
	}

	public final List<TownStructureEntry> getMainStructureEntries() {
		return mainStructureEntries;
	}

	public final List<TownStructureEntry> getHouseStructureEntries() {
		return houseStructureEntries;
	}

	public final List<TownStructureEntry> getExteriorStructureEntries() {
		return exteriorStructureEntries;
	}

	public final List<TownStructureEntry> getCosmeticEntries() {
		return cosmeticStructureEntries;
	}

	public final int getMinSize() {
		return minSize;
	}

	public final void setMinSize(int minSize) {
		this.minSize = minSize;
	}

	public final int getMaxSize() {
		return maxSize;
	}

	public final void setMaxSize(int maxSize) {
		this.maxSize = maxSize;
	}

	public final void setTownBuildingWidthExpansion(int townBuildingWidthExpansion) {
		this.townBuildingWidthExpansion = townBuildingWidthExpansion;
	}

	public final int getTownBuildingWidthExpansion() {
		return townBuildingWidthExpansion;
	}

	public final int getSelectionWeight() {
		return selectionWeight;
	}

	public final void setSelectionWeight(int selectionWeight) {
		this.selectionWeight = selectionWeight;
	}

	public final int getClusterValue() {
		return clusterValue;
	}

	public final void setClusterValue(int clusterValue) {
		this.clusterValue = clusterValue;
	}

	public final Optional<TownStructureEntry> getLamp() {
		return Optional.ofNullable(lamp);
	}

	public final void setLamp(TownStructureEntry lamp) {
		this.lamp = lamp;
	}

	public final int getExteriorSize() {
		return exteriorSize;
	}

	public final void setExteriorSize(int exteriorSize) {
		this.exteriorSize = exteriorSize;
	}

	public final void setExteriorPlotSize(int exteriorPlotSize) {
		this.exteriorPlotSize = exteriorPlotSize;
	}

	public int getInteriorEmtpyPlotChance() {
		return interiorEmtpyPlotChance;
	}

	public void setInteriorEmtpyPlotChance(int interiorEmtpyPlotChance) {
		this.interiorEmtpyPlotChance = interiorEmtpyPlotChance;
	}

	public final int[] getWallPattern(int size) {
		return wallPatterns.get(size);
	}

	public final int getWallStyle() {
		return wallStyle;
	}

	public final int getWallSize() {
		return wallSize;
	}

	public final void setWallStyle(int wallStyle) {
		this.wallStyle = wallStyle;
	}

	public final void setWallSize(int wallSize) {
		this.wallSize = wallSize;
	}

	public final void addWallPattern(int size, int[] pattern) {
		wallPatterns.put(size, pattern);
	}

	public final void addWall(TownWallEntry e) {
		wallsByID.put(e.id, e);
		if (e.typeName.equalsIgnoreCase("wall")) {
			walls.add(e);
			wallTotalWeights += e.weight;
		} else if (e.typeName.equalsIgnoreCase("corner")) {
			cornerWalls.add(e);
			cornersTotalWeight += e.weight;
		} else if (e.typeName.equalsIgnoreCase("gate")) {
			gateCenterWalls.add(e);
			gatesCenterTotalWeight += e.weight;
		} else if (e.typeName.equalsIgnoreCase("lgate")) {
			gateLeftWalls.add(e);
			gatesLeftTotalWeight += e.weight;
		} else if (e.typeName.equalsIgnoreCase("rgate")) {
			gateRightWalls.add(e);
			gatesRightTotalWeight += e.weight;
		}
	}

	public final int getTownBlockSize() {
		return townBlockSize;
	}

	public final int getTownPlotSize() {
		return townPlotSize;
	}

	public final void setTownBlockSize(int townBlockSize) {
		this.townBlockSize = townBlockSize;
	}

	public final void setTownPlotSize(int townPlotSize) {
		this.townPlotSize = townPlotSize;
	}

	public float getRandomVillagersPerChunk() {
		return randomVillagersPerChunk;
	}

	public void setRandomVillagersPerChunk(float randomVillagersPerChunk) {
		this.randomVillagersPerChunk = randomVillagersPerChunk;
	}

	public final TownWallEntry getWall(int id) {
		return wallsByID.get(id);
	}

	public final String getRandomWeightedWall(Random rng) {
		return getRandomWeightedWallPiece(rng, walls, wallTotalWeights);
	}

	public final String getRandomWeightedCorner(Random rng) {
		return getRandomWeightedWallPiece(rng, cornerWalls, cornersTotalWeight);
	}

	public final String getRandomWeightedGate(Random rng) {
		return getRandomWeightedWallPiece(rng, gateCenterWalls, gatesCenterTotalWeight);
	}

	public final String getRandomWeightedGateLeft(Random rng) {
		return getRandomWeightedWallPiece(rng, gateLeftWalls, gatesLeftTotalWeight);
	}

	public final String getRandomWeightedGateRight(Random rng) {
		return getRandomWeightedWallPiece(rng, gateRightWalls, gatesRightTotalWeight);
	}

	private static String getRandomWeightedWallPiece(Random rng, List<TownWallEntry> list, int totalWeight) {
		int roll = rng.nextInt(totalWeight);
		for (TownWallEntry e : list) {
			roll -= e.weight;
			if (roll < 0) {
				return e.templateName;
			}
		}
		throw new IllegalArgumentException("Getting random wall from empty list of walls. Such list shouldn't have been loaded at all.");
	}

	public final void validateStructureEntries() {
		validateStructureList(uniqueStructureEntries);
		validateStructureList(mainStructureEntries);
		validateStructureList(houseStructureEntries);
		validateStructureList(cosmeticStructureEntries);
		validateStructureList(exteriorStructureEntries);
		Optional<TownStructureEntry> e = getLamp();
		if (e.isPresent() && !StructureTemplateManager.getTemplate(e.get().templateName).isPresent()) {
			AncientWarfareStructure.LOG.error("Error loading lamp template: {}", e.get().templateName);
		}
		wallTotalWeights = validateWallList(walls, wallTotalWeights);
		cornersTotalWeight = validateWallList(cornerWalls, cornersTotalWeight);
		gatesCenterTotalWeight = validateWallList(gateCenterWalls, gatesCenterTotalWeight);
		gatesLeftTotalWeight = validateWallList(gateLeftWalls, gatesLeftTotalWeight);
		gatesRightTotalWeight = validateWallList(gateRightWalls, gatesRightTotalWeight);
	}

	private void validateStructureList(Collection<TownStructureEntry> entries) {
		Iterator<TownStructureEntry> it = entries.iterator();
		TownStructureEntry e;
		while (it.hasNext() && (e = it.next()) != null) {
			if (!StructureTemplateManager.getTemplate(e.templateName).isPresent()) {
				AncientWarfareStructure.LOG.error("Error loading structure template: {} for town: {}", e.templateName, townTypeName);
				it.remove();
			}
		}
	}

	private int validateWallList(Collection<TownWallEntry> entries, int originalWeight) {
		Iterator<TownWallEntry> it = entries.iterator();
		TownWallEntry e;
		while (it.hasNext() && (e = it.next()) != null) {
			if (!StructureTemplateManager.getTemplate(e.templateName).isPresent()) {
				AncientWarfareStructure.LOG.error("Error loading structure template: {} for town: {}", e.templateName, townTypeName);
				it.remove();
				originalWeight -= e.weight;
			}
		}
		return originalWeight;
	}

	public final boolean isValid() {
		List<String> missingWallTypes = new ArrayList<>();
		if (wallStyle > 0 && cornerWalls.isEmpty()) {
			missingWallTypes.add("corner");
		}
		if (wallStyle > 1) {
			if (walls.isEmpty()) {
				missingWallTypes.add("wall");
			}
			if (gateCenterWalls.isEmpty()) {
				missingWallTypes.add("gate");
			}
			if (gateLeftWalls.isEmpty()) {
				missingWallTypes.add("lgate");
			}
			if (gateRightWalls.isEmpty()) {
				missingWallTypes.add("rgate");
			}
		}

		if (!missingWallTypes.isEmpty()) {
			StringJoiner missingTypes = new StringJoiner(", ");
			missingWallTypes.forEach(missingTypes::add);
			AncientWarfareStructure.LOG.error("Town template of: {} is missing {} wall types for specified wall style of: {}", townTypeName, missingTypes, wallStyle);
			return false;
		}

		return townTypeName != null && !townTypeName.isEmpty();
	}

	public List<IBlockState> getRoadFillBlocks() {
		return roadFillBlocks;
	}

	public void addRoadFillBlock(IBlockState state) {
		roadFillBlocks.add(state);
	}

	public void setPreventNaturalHostileSpawns(boolean preventNaturalHostileSpawns) {
		this.preventNaturalHostileSpawns = preventNaturalHostileSpawns;
	}

	public boolean shouldPreventNaturalHostileSpawns() {
		return preventNaturalHostileSpawns;
	}

	public void setBiomeReplacement(ResourceLocation biomeReplacement) {
		this.biomeReplacement = biomeReplacement;
	}

	public Optional<ResourceLocation> getBiomeReplacement() {
		return Optional.ofNullable(biomeReplacement);
	}

	public void setTerritoryName(String territoryName) {
		this.territoryName = territoryName.trim();
	}

	public String getTerritoryName() {
		return territoryName;
	}

	public static final class TownStructureEntry {
		String templateName;
		int min;//min # to generate

		public TownStructureEntry(String name, int min) {
			this.templateName = name;
			this.min = min;
		}

		@Override
		public String toString() {
			return "[Town Structure: " + templateName + " :: " + min + "]";
		}
	}

	public static final class TownWallEntry {
		String templateName;
		private String typeName;
		private int weight;
		private int id;

		public TownWallEntry(String name, String type, int id, int weight) {
			this.templateName = name;
			this.typeName = type;
			this.id = id;
			this.weight = weight;
		}

		@Override
		public String toString() {
			return "[Town Wall: " + typeName + " :: " + id + " :: " + templateName + "]";
		}
	}

}
