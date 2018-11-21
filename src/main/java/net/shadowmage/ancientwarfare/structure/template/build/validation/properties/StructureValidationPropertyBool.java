package net.shadowmage.ancientwarfare.structure.template.build.validation.properties;

import net.minecraft.nbt.NBTTagCompound;

public class StructureValidationPropertyBool implements IStructureValidationProperty<Boolean> {
	private String name;
	private boolean defaultValue;

	public StructureValidationPropertyBool(String name, boolean defaultValue) {
		this.name = name;
		this.defaultValue = defaultValue;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Class<Boolean> getValueClass() {
		return Boolean.class;
	}

	@Override
	public NBTTagCompound serializeNBT(NBTTagCompound tag, Boolean value) {
		tag.setBoolean(getName(), value);
		return tag;
	}

	@Override
	public Boolean deserializeNBT(NBTTagCompound tag) {
		return tag.getBoolean(getName());
	}

	@Override
	public Boolean getDefaultValue() {
		return defaultValue;
	}

	@Override
	public String getStringValue(Boolean value) {
		return value.toString();
	}

	@Override
	public Boolean parseValue(String valueString) {
		return Boolean.valueOf(valueString);
	}
}
