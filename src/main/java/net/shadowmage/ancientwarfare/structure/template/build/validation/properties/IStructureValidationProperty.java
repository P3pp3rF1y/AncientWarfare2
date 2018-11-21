package net.shadowmage.ancientwarfare.structure.template.build.validation.properties;

import net.minecraft.nbt.NBTTagCompound;

public interface IStructureValidationProperty<T> {
	String getName();

	Class<T> getValueClass();

	NBTTagCompound serializeNBT(NBTTagCompound tag, T value);

	T deserializeNBT(NBTTagCompound tag);

	T getDefaultValue();

	String getStringValue(T value);

	T parseValue(String valueString);
}
