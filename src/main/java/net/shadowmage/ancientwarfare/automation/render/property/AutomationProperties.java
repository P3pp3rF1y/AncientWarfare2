package net.shadowmage.ancientwarfare.automation.render.property;

import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.common.property.Properties;
import net.minecraftforge.common.property.PropertyFloat;
import net.shadowmage.ancientwarfare.automation.block.TorqueTier;

public class AutomationProperties {
	public static final IUnlistedProperty<Boolean> ACTIVE = Properties.toUnlisted(PropertyBool.create("active"));
	public static final IUnlistedProperty<Boolean> DYNAMIC = Properties.toUnlisted(PropertyBool.create("dynamic"));
	public static final IUnlistedProperty<Float>[] ROTATIONS = new IUnlistedProperty[6];
	public static final IUnlistedProperty<Boolean> USE_INPUT = Properties.toUnlisted(PropertyBool.create("use_input"));
	public static final IUnlistedProperty<Float> INPUT_ROTATION = new PropertyFloat("input_rotation");
	public static final PropertyEnum<TorqueTier> TIER = PropertyEnum.create("tier", TorqueTier.class);
	public static final IUnlistedProperty<Boolean> IS_CONTROL = Properties.toUnlisted(PropertyBool.create("is_control"));
	public static final IUnlistedProperty<Integer> HEIGHT = Properties.toUnlisted(PropertyInteger.create("height", 0, 30));
	public static final IUnlistedProperty<Integer> WIDTH = Properties.toUnlisted(PropertyInteger.create("width", 0, 30));
	public static final IUnlistedProperty<Float> ROTATION = new PropertyFloat("rotation");

	static {
		for (EnumFacing facing : EnumFacing.VALUES) {
			ROTATIONS[facing.getIndex()] = new PropertyFloat("rotation_" + facing.name().toLowerCase());
		}
	}
}
