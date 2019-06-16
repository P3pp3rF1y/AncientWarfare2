package net.shadowmage.ancientwarfare.structure.render.property;

import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.util.EnumFacing;

public class StructureProperties {
	public static final PropertyEnum<TopBottomPart> TOP_BOTTOM_PART = PropertyEnum.create("part", TopBottomPart.class);
	public static final PropertyEnum<EnumFacing.Axis> AXIS = PropertyEnum.create("axis", EnumFacing.Axis.class);
}
