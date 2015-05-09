/**
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

import java.util.*;

public enum StructureValidationType {
    GROUND(StructureValidatorGround.class),
    UNDERGROUND(StructureValidatorUnderground.class, new StructureValidationProperty("minGenDepth", StructureValidationProperty.DATA_TYPE_INT, 0), new StructureValidationProperty("maxGenDepth", StructureValidationProperty.DATA_TYPE_INT, 0), new StructureValidationProperty("minOverfill", StructureValidationProperty.DATA_TYPE_INT, 0)),
    SKY(StructureValidatorSky.class, new StructureValidationProperty("minGenHeight", StructureValidationProperty.DATA_TYPE_INT, 0), new StructureValidationProperty("maxGenHeight", StructureValidationProperty.DATA_TYPE_INT, 0), new StructureValidationProperty("minFlyingHeight", StructureValidationProperty.DATA_TYPE_INT, 0)),
    WATER(StructureValidatorWater.class),
    UNDERWATER(StructureValidatorUnderwater.class, new StructureValidationProperty("minWaterDepth", StructureValidationProperty.DATA_TYPE_INT, 0), new StructureValidationProperty("maxWaterDepth", StructureValidationProperty.DATA_TYPE_INT, 0)),
    HARBOR(StructureValidatorHarbor.class),
    ISLAND(StructureValidatorIsland.class, new StructureValidationProperty("minWaterDepth", StructureValidationProperty.DATA_TYPE_INT, 0), new StructureValidationProperty("maxWaterDepth", StructureValidationProperty.DATA_TYPE_INT, 0));

    private Class<? extends StructureValidator> validatorClass;

    private List<StructureValidationProperty> properties = new ArrayList<StructureValidationProperty>();

    StructureValidationType(Class<? extends StructureValidator> validatorClass, StructureValidationProperty... props) {
        this.validatorClass = validatorClass;

        properties.add(new StructureValidationProperty(StructureValidator.PROP_SURVIVAL, StructureValidationProperty.DATA_TYPE_BOOLEAN, false));
        properties.add(new StructureValidationProperty(StructureValidator.PROP_WORLD_GEN, StructureValidationProperty.DATA_TYPE_BOOLEAN, false));
        properties.add(new StructureValidationProperty(StructureValidator.PROP_UNIQUE, StructureValidationProperty.DATA_TYPE_BOOLEAN, false));
        properties.add(new StructureValidationProperty(StructureValidator.PROP_PRESERVE_BLOCKS, StructureValidationProperty.DATA_TYPE_BOOLEAN, false));

        properties.add(new StructureValidationProperty(StructureValidator.PROP_SELECTION_WEIGHT, StructureValidationProperty.DATA_TYPE_INT, 0));
        properties.add(new StructureValidationProperty(StructureValidator.PROP_CLUSTER_VALUE, StructureValidationProperty.DATA_TYPE_INT, 0));
        properties.add(new StructureValidationProperty(StructureValidator.PROP_MIN_DUPLICATE_DISTANCE, StructureValidationProperty.DATA_TYPE_INT, 0));
        properties.add(new StructureValidationProperty(StructureValidator.PROP_BORDER_SIZE, StructureValidationProperty.DATA_TYPE_INT, 0));
        properties.add(new StructureValidationProperty(StructureValidator.PROP_MAX_LEVELING, StructureValidationProperty.DATA_TYPE_INT, 0));
        properties.add(new StructureValidationProperty(StructureValidator.PROP_MAX_FILL, StructureValidationProperty.DATA_TYPE_INT, 0));

        properties.add(new StructureValidationProperty(StructureValidator.PROP_BIOME_WHITE_LIST, StructureValidationProperty.DATA_TYPE_BOOLEAN, false));
        properties.add(new StructureValidationProperty(StructureValidator.PROP_DIMENSION_WHITE_LIST, StructureValidationProperty.DATA_TYPE_BOOLEAN, false));

        properties.add(new StructureValidationProperty(StructureValidator.PROP_BIOME_LIST, StructureValidationProperty.DATA_TYPE_STRING_SET, new HashSet<String>()));
        properties.add(new StructureValidationProperty(StructureValidator.PROP_BLOCK_LIST, StructureValidationProperty.DATA_TYPE_STRING_SET, new HashSet<String>()));
        properties.add(new StructureValidationProperty(StructureValidator.PROP_DIMENSION_LIST, StructureValidationProperty.DATA_TYPE_INT_ARRAY, new int[]{}));

        properties.add(new StructureValidationProperty(StructureValidator.PROP_BLOCK_SWAP, StructureValidationProperty.DATA_TYPE_BOOLEAN, false));

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
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static StructureValidationType getTypeFromName(String name) {
        if (name == null) {
            return null;
        }
        try {
            return StructureValidationType.valueOf(name.toUpperCase(Locale.ENGLISH));
        }catch (IllegalArgumentException illegal){
            return null;
        }
    }

    /**
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
        @SuppressWarnings("rawtypes")
        public Class clz;//property class -- boolean or int for most

        @SuppressWarnings("rawtypes")
        public ValidationProperty(String reg, String display, Class clz) {
            this.propertyName = reg;
            this.displayName = display;
            this.clz = clz;
        }
    }

}
