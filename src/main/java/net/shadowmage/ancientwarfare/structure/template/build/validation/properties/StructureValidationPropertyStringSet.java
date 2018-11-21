package net.shadowmage.ancientwarfare.structure.template.build.validation.properties;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.common.util.Constants;
import net.shadowmage.ancientwarfare.core.util.StringTools;

import java.util.HashSet;
import java.util.Set;

public class StructureValidationPropertyStringSet implements IStructureValidationProperty<Set> {

	private String name;
	private Set<String> defaultValue;

	public StructureValidationPropertyStringSet(String name, Set<String> defaultValue) {
		this.name = name;
		this.defaultValue = defaultValue;
	}

	@Override
	public NBTTagCompound serializeNBT(NBTTagCompound tag, Set value) {
		NBTTagList list = new NBTTagList();
		//noinspection unchecked
		value.forEach(element -> list.appendTag(new NBTTagString((String) element)));
		tag.setTag(getName(), list);
		return tag;
	}

	@Override
	public Set<String> deserializeNBT(NBTTagCompound tag) {
		NBTTagList list = tag.getTagList(getName(), Constants.NBT.TAG_STRING);
		Set<String> ret = new HashSet<>();
		for (int i = 0; i < list.tagCount(); i++) {
			ret.add(list.getStringTagAt(i));
		}
		return ret;
	}

	@Override
	public Set<String> getDefaultValue() {
		return defaultValue;
	}

	@Override
	public String getStringValue(Set value) {
		//noinspection unchecked
		return StringTools.getCSVValueFor(value);
	}

	@Override
	public Set parseValue(String valueString) {
		return StringTools.parseStringSet(valueString);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Class<Set> getValueClass() {
		return Set.class;
	}
}
