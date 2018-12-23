package net.shadowmage.ancientwarfare.structure.template.build.validation.properties;

import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.core.util.StringTools;

public class StructureValidationPropertyIntArray implements IStructureValidationProperty<int[]> {

	private String name;
	private int[] defaultValue;

	public StructureValidationPropertyIntArray(String name, int[] defaultValue) {
		this.name = name;
		this.defaultValue = defaultValue;
	}

	@Override
	public NBTTagCompound serializeNBT(NBTTagCompound tag, int[] value) {
		tag.setIntArray(getName(), value);
		return tag;
	}

	@Override
	public int[] deserializeNBT(NBTTagCompound tag) {
		return tag.getIntArray(getName());
	}

	@Override
	public int[] getDefaultValue() {
		return defaultValue;
	}

	@Override
	public String getStringValue(int[] value) {
		return StringTools.getCSVStringForArray(value);
	}

	@Override
	public int[] parseValue(String valueString) {
		return StringTools.parseIntArray(valueString);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Class<int[]> getValueClass() {
		return int[].class;
	}
}
