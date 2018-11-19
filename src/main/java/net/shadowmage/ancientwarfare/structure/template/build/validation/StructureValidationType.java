package net.shadowmage.ancientwarfare.structure.template.build.validation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Supplier;

import static net.shadowmage.ancientwarfare.structure.template.build.validation.StructureValidatorIsland.PROP_MAX_WATER_DEPTH;
import static net.shadowmage.ancientwarfare.structure.template.build.validation.StructureValidatorIsland.PROP_MIN_WATER_DEPTH;

public enum StructureValidationType {
	GROUND(StructureValidatorGround::new),
	UNDERGROUND(StructureValidatorUnderground::new,
			new StructureValidationProperty("minGenDepth", 0),
			new StructureValidationProperty("maxGenDepth", 0),
			new StructureValidationProperty("minOverfill", 0)),
	SKY(StructureValidatorSky::new,
			new StructureValidationProperty("minGenHeight", 0),
			new StructureValidationProperty("maxGenHeight", 0),
			new StructureValidationProperty("minFlyingHeight", 0)),
	WATER(StructureValidatorWater::new),
	UNDERWATER(StructureValidatorUnderwater::new,
			new StructureValidationProperty("minWaterDepth", 0),
			new StructureValidationProperty("maxWaterDepth", 0)),
	HARBOR(StructureValidatorHarbor::new),
	ISLAND(StructureValidatorIsland::new,
			new StructureValidationProperty(PROP_MIN_WATER_DEPTH, 0),
			new StructureValidationProperty(PROP_MAX_WATER_DEPTH, 0));

	private final Supplier<? extends StructureValidator> createValidator;

	private List<StructureValidationProperty> properties = new ArrayList<>();

	StructureValidationType(Supplier<? extends StructureValidator> createValidator, StructureValidationProperty... props) {
		this.createValidator = createValidator;

		properties.add(new StructureValidationProperty(StructureValidator.PROP_SURVIVAL, false));
		properties.add(new StructureValidationProperty(StructureValidator.PROP_WORLD_GEN, false));
		properties.add(new StructureValidationProperty(StructureValidator.PROP_UNIQUE, false));
		properties.add(new StructureValidationProperty(StructureValidator.PROP_PRESERVE_BLOCKS, false));

		properties.add(new StructureValidationProperty(StructureValidator.PROP_SELECTION_WEIGHT, 0));
		properties.add(new StructureValidationProperty(StructureValidator.PROP_CLUSTER_VALUE, 0));
		properties.add(new StructureValidationProperty(StructureValidator.PROP_MIN_DUPLICATE_DISTANCE, 0));
		properties.add(new StructureValidationProperty(StructureValidator.PROP_BORDER_SIZE, 0));
		properties.add(new StructureValidationProperty(StructureValidator.PROP_MAX_LEVELING, 0));
		properties.add(new StructureValidationProperty(StructureValidator.PROP_MAX_FILL, 0));

		properties.add(new StructureValidationProperty(StructureValidator.PROP_BIOME_WHITE_LIST, false));
		properties.add(new StructureValidationProperty(StructureValidator.PROP_DIMENSION_WHITE_LIST, false));

		properties.add(new StructureValidationProperty(StructureValidator.PROP_BIOME_LIST, new HashSet<>()));
		properties.add(new StructureValidationProperty(StructureValidator.PROP_BLOCK_LIST, new HashSet<>()));
		properties.add(new StructureValidationProperty(StructureValidator.PROP_DIMENSION_LIST, new int[] {}));

		properties.add(new StructureValidationProperty(StructureValidator.PROP_BLOCK_SWAP, false));

		Collections.addAll(properties, props);
	}

	public List<StructureValidationProperty> getValidationProperties() {
		return this.properties;
	}

	public String getName() {
		return name().toLowerCase(Locale.ENGLISH);
	}

	public StructureValidator getValidator() {
		return createValidator.get();
	}

	public static Optional<StructureValidationType> getTypeFromName(String name) {
		try {
			return Optional.of(StructureValidationType.valueOf(name.toUpperCase(Locale.ENGLISH)));
		}
		catch (IllegalArgumentException illegal) {
			return Optional.empty();
		}
	}
}
