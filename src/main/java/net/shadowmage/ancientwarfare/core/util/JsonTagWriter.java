package net.shadowmage.ancientwarfare.core.util;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.nbt.NBTTagString;
import net.shadowmage.ancientwarfare.core.util.Json.JsonAbstract;
import net.shadowmage.ancientwarfare.core.util.Json.JsonArray;
import net.shadowmage.ancientwarfare.core.util.Json.JsonObject;
import net.shadowmage.ancientwarfare.core.util.Json.JsonValue;

public class JsonTagWriter {

	public static JsonObject getJsonForTag(NBTTagCompound tag) {
		JsonObject base = new JsonObject();
		base.writeValue("id", new JsonValue(Json.getTagType(tag)));
		base.writeAbstract("val", getJsonValueForCompoundTag(tag));
		return base;
	}

	private static JsonArray getJsonValueForListTag(NBTTagList tag) {
		JsonArray base = new JsonArray();
		JsonAbstract valueJson;
		tag = tag.copy();
		while (tag.tagCount() > 0) {
			valueJson = getJsonFor(tag.removeTag(0));
			if (valueJson != null) {
				base.add(valueJson);
			}
		}
		return base;
	}

	private static JsonObject getJsonValueForCompoundTag(NBTTagCompound tag) {
		JsonObject base = new JsonObject();
		JsonAbstract valueJson;
		for (String key : tag.getKeySet()) {
			valueJson = getJsonFor(tag.getTag(key));
			if (valueJson != null) {
				base.writeAbstract(key, valueJson);
			}
		}
		return base;
	}

	private static JsonArray getJsonValueForByteArray(NBTTagByteArray tag) {
		JsonArray array = new JsonArray();
		byte[] bytes = tag.getByteArray();
		for (byte b : bytes) {
			array.add(new JsonValue(String.valueOf(b)));
		}
		return array;
	}

	private static JsonArray getJsonValueForIntArray(NBTTagIntArray tag) {
		JsonArray array = new JsonArray();
		int[] ints = tag.getIntArray();
		for (int b : ints) {
			array.add(new JsonValue(String.valueOf(b)));
		}
		return array;
	}

	private static JsonAbstract getJsonFor(NBTBase tag) {
		String typeId = Json.getTagType(tag);
		byte id = tag.getId();
		if (typeId == null) {
			return null;
		}
		JsonObject object = new JsonObject();
		JsonAbstract value = null;
		object.writeValue("id", new JsonValue(typeId));
		switch (id) {
			case 1://byte
			{
				value = new JsonValue(String.valueOf(((NBTTagByte) tag).getByte()));
				break;
			}
			case 2://short
			{
				value = new JsonValue(String.valueOf(((NBTTagShort) tag).getShort()));
				break;
			}
			case 3://int
			{
				value = new JsonValue(String.valueOf(((NBTTagInt) tag).getInt()));
				break;
			}
			case 4://long
			{
				value = new JsonValue(String.valueOf(((NBTTagLong) tag).getLong()));
				break;
			}
			case 5://float
			{
				value = new JsonValue(String.valueOf(((NBTTagFloat) tag).getFloat()));
				break;
			}
			case 6://double
			{
				value = new JsonValue(String.valueOf(((NBTTagDouble) tag).getDouble()));
				break;
			}
			case 7://byte array
			{
				value = getJsonValueForByteArray((NBTTagByteArray) tag);
				break;
			}
			case 8://string
			{
				value = new JsonValue(((NBTTagString) tag).getString());
				break;
			}
			case 9://list
			{
				value = getJsonValueForListTag((NBTTagList) tag);
				break;
			}
			case 10://compound
			{
				value = getJsonValueForCompoundTag((NBTTagCompound) tag);
				break;
			}
			case 11://int array
			{
				value = getJsonValueForIntArray((NBTTagIntArray) tag);
				break;
			}
		}
		if (value == null) {
			return null;
		}
		object.writeAbstract("val", value);
		return object;
	}
}
