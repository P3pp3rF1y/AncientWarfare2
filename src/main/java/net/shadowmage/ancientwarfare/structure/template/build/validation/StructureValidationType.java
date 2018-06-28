/*
 Copyright 2012-2013 John Cummens (aka Shadowmage, Shadowmage4513)
 This software is distributed under the terms of the GNU General Public License.
 Please see COPYING for precise license information.

 This file is part of Ancient Warfare.

 Ancient Warfare is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 Ancient Warfare is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with Ancient Warfare.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.shadowmage.ancientwarfare.structure.template.build.validation;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

public enum StructureValidationType {
	GROUND(StructureValidatorGround.class), UNDERGROUND(StructureValidatorUnderground.class, new StructureValidationProperty("minGenDepth", 0), new StructureValidationProperty("maxGenDepth", 0), new StructureValidationProperty("minOverfill", 0)), SKY(StructureValidatorSky.class, new StructureValidationProperty("minGenHeight", 0), new StructureValidationProperty("maxGenHeight", 0), new StructureValidationProperty("minFlyingHeight", 0)), WATER(StructureValidatorWater.class), UNDERWATER(StructureValidatorUnderwater.class, new StructureValidationProperty("minWaterDepth", 0), new StructureValidationProperty("maxWaterDepth", 0)), HARBOR(StructureValidatorHarbor.class), ISLAND(StructureValidatorIsland.class, new StructureValidationProperty("minWaterDepth", 0), new StructureValidationProperty("maxWaterDepth", 0));

	private Class<? extends StructureValidator> validatorClass;

	private List<StructureValidationProperty> properties = new ArrayList<>();

	StructureValidationType(Class<? extends StructureValidator> validatorClass, StructureValidationProperty... props) {
		this.validatorClass = validatorClass;

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
		try {
			return validatorClass.newInstance();
		}
		catch (InstantiationException e) {
			e.printStackTrace();
		}
		catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Nullable
	public static StructureValidationType getTypeFromName(String name) {
		try {
			return StructureValidationType.valueOf(name.toUpperCase(Locale.ENGLISH));
		}
		catch (IllegalArgumentException illegal) {
			return null;
		}
	}

    /*
	 * validation types:
     * ground:
     * validate border edge blocks for depth and leveling
     * validate border target blocks
     * <p/>
     * underground:
     * validate min/max overfill height is met
     * validate border target blocks
     * <p/>
     * water:
     * validate water depth along edges
     * <p/>
     * underwater:
     * validate min/max water depth at placement x/z
     * validate border edge blocks for depth and leveling
     * <p/>
     * sky:
     * validate min flying height along edges
     * <p/>
     * harbor:
     * validate edges--front all land, sides land/water, back all water. validate edge-depth and leveling *
     * <p/>
     * island:
     * validate min/max water depth at placement x/z
     * validate border edge blocks for depth and leveling
     */

	public static class ValidationProperty {
		public String displayName;
		public String propertyName;

		public Class clz;//property class -- boolean or int for most

		public ValidationProperty(String reg, String display, Class clz) {
			this.propertyName = reg;
			this.displayName = display;
			this.clz = clz;
		}
	}

}
