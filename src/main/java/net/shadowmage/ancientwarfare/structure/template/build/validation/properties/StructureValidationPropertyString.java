package net.shadowmage.ancientwarfare.structure.template.build.validation.properties;

import net.minecraft.nbt.NBTTagCompound;

public class StructureValidationPropertyString implements IStructureValidationProperty<String> {
	private final String name;
	private final String defaultValue;

	public StructureValidationPropertyString(String name, String defaultValue) {
		this.name = name;
		this.defaultValue = defaultValue;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Class<String> getValueClass() {
		return String.class;
	}

	@Override
	public NBTTagCompound serializeNBT(NBTTagCompound tag, String value) {
		tag.setString(getName(), value);
		return tag;
	}

	@Override
	public String deserializeNBT(NBTTagCompound tag) {
		return tag.getString(getName());
	}

	@Override
	public String getDefaultValue() {
		return defaultValue;
	}

	@Override
	public String getStringValue(String value) {
		return value;
	}

	@Override
	public String parseValue(String valueString) {
		return valueString;
	}
}
