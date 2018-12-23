package net.shadowmage.ancientwarfare.structure.template.build.validation.properties;

import net.minecraft.nbt.NBTTagCompound;

public class StructureValidationPropertyInteger implements IStructureValidationProperty<Integer> {
	private String name;
	private int defaultValue;

	public StructureValidationPropertyInteger(String name, int defaultValue) {
		this.name = name;
		this.defaultValue = defaultValue;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Class<Integer> getValueClass() {
		return Integer.class;
	}

	@Override
	public NBTTagCompound serializeNBT(NBTTagCompound tag, Integer value) {
		tag.setInteger(getName(), value);
		return tag;
	}

	@Override
	public Integer deserializeNBT(NBTTagCompound tag) {
		return tag.getInteger(getName());
	}

	@Override
	public Integer getDefaultValue() {
		return defaultValue;
	}

	@Override
	public String getStringValue(Integer value) {
		return value.toString();
	}

	@Override
	public Integer parseValue(String valueString) {
		return Integer.valueOf(valueString);
	}
}
