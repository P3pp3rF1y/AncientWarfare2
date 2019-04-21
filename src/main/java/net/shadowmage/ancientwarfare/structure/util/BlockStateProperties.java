package net.shadowmage.ancientwarfare.structure.util;

import net.minecraft.block.properties.PropertyEnum;
import net.shadowmage.ancientwarfare.structure.block.WoodVariant;

public class BlockStateProperties {
	public static final PropertyEnum<WoodVariant> VARIANT = PropertyEnum.create("variant", WoodVariant.class);

	private BlockStateProperties() {}

}
