package net.shadowmage.ancientwarfare.structure.template.build.validation.properties;

import java.util.HashSet;

public class StructureValidationProperties {
	private StructureValidationProperties() {}

	public static final StructureValidationPropertyInteger MIN_GENERATION_DEPTH = new StructureValidationPropertyInteger("minGenDepth", 0);
	public static final StructureValidationPropertyInteger MAX_GENERATION_DEPTH = new StructureValidationPropertyInteger("maxGenDepth", 0);
	public static final StructureValidationPropertyInteger MIN_OVERFILL = new StructureValidationPropertyInteger("minOverfill", 0);

	public static final StructureValidationPropertyInteger MIN_GENERATION_HEIGHT = new StructureValidationPropertyInteger("minGenerationHeight", 0);
	public static final StructureValidationPropertyInteger MAX_GENERATION_HEIGHT = new StructureValidationPropertyInteger("maxGenerationHeight", 0);
	public static final StructureValidationPropertyInteger MIN_FLYING_HEIGHT = new StructureValidationPropertyInteger("minFlyingHeight", 0);

	public static final StructureValidationPropertyInteger MIN_WATER_DEPTH = new StructureValidationPropertyInteger("minWaterDepth", 0);
	public static final StructureValidationPropertyInteger MAX_WATER_DEPTH = new StructureValidationPropertyInteger("maxWaterDepth", 0);

	public static final StructureValidationPropertyBool SURVIVAL = new StructureValidationPropertyBool("survival", false);
	public static final StructureValidationPropertyBool WORLD_GEN = new StructureValidationPropertyBool("worldGenEnabled", false);
	public static final StructureValidationPropertyBool UNIQUE = new StructureValidationPropertyBool("unique", false);
	public static final StructureValidationPropertyBool PRESERVE_BLOCKS = new StructureValidationPropertyBool("preserveBlocks", false);

	public static final StructureValidationPropertyInteger SELECTION_WEIGHT = new StructureValidationPropertyInteger("selectionWeight", 1);
	public static final StructureValidationPropertyInteger CLUSTER_VALUE = new StructureValidationPropertyInteger("clusterValue", 1);
	public static final StructureValidationPropertyInteger MIN_DUPLICATE_DISTANCE = new StructureValidationPropertyInteger("minDuplicateDistance", 1);
	public static final StructureValidationPropertyInteger BORDER_SIZE = new StructureValidationPropertyInteger("border", 0);
	public static final StructureValidationPropertyInteger MAX_LEVELING = new StructureValidationPropertyInteger("leveling", 0);
	public static final StructureValidationPropertyInteger MAX_FILL = new StructureValidationPropertyInteger("fill", 0);

	public static final StructureValidationPropertyBool BIOME_WHITE_LIST = new StructureValidationPropertyBool("biomeWhiteList", false);
	public static final StructureValidationPropertyBool DIMENSION_WHITE_LIST = new StructureValidationPropertyBool("dimensionWhiteList", false);

	public static final StructureValidationPropertyStringSet BIOME_LIST = new StructureValidationPropertyStringSet("biomeList",
			new HashSet<>());

	public static final StructureValidationPropertyStringSet BIOME_GROUP_LIST = new StructureValidationPropertyStringSet("biomeGroupList",
			new HashSet<>());

	public static final StructureValidationPropertyIntArray DIMENSION_LIST = new StructureValidationPropertyIntArray("dimensionList", new int[0]);

	public static final StructureValidationPropertyBool BLOCK_SWAP = new StructureValidationPropertyBool("blockSwap", false);
}
