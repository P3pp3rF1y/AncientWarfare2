package net.shadowmage.ancientwarfare.structure.template.build.validation.properties;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class StructureValidationPropertyResourceLocation implements IStructureValidationProperty<ResourceLocation> {
	private String name;
	private ResourceLocation defaultValue;

	public StructureValidationPropertyResourceLocation(String name, ResourceLocation defaultValue) {
		this.name = name;
		this.defaultValue = defaultValue;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Class<ResourceLocation> getValueClass() {
		return ResourceLocation.class;
	}

	@Override
	public NBTTagCompound serializeNBT(NBTTagCompound tag, ResourceLocation value) {
		tag.setString(name, value.toString());
		return tag;
	}

	@Override
	public ResourceLocation deserializeNBT(NBTTagCompound tag) {
		return new ResourceLocation(tag.getString(name));
	}

	@Override
	public ResourceLocation getDefaultValue() {
		return defaultValue;
	}

	@Override
	public String getStringValue(ResourceLocation value) {
		return value.toString();
	}

	@Override
	public ResourceLocation parseValue(String valueString) {
		return new ResourceLocation(valueString);
	}

}
