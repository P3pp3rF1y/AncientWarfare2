package net.shadowmage.ancientwarfare.structure.template.datafixes.fixers.json;

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

/*
 * Reads NBT tags from JSON formatted strings.<br>
 * All tags will have the outer value of "JSON:{}", with all data enclosed by the set of brackets<br>
 * All names and values will be enclosed by double quotes<br>.
 * All NBT types are represented as an object with two value fields, a type-code to denote how to deserialize, and the field containing the value of the object<br>
 * JSON values should be type-coded with the following codes:<br>
 * <li>NBTTagByte = pb
 * <li>NBTTagByteArray = ab
 * <li>NBTTagShort = ps
 * <li>NBTTagInt = pi
 * <li>NBTTagIntArray = ai
 * <li>NBTTagLong = pl
 * <li>NBTTagFloat = pf
 * <li>NBTTagDouble = pd
 * <li>NBTTagString = ss
 * <li>NBTTagList = ls
 * <li>NBTTagCompound = ct
 *
 * @author Shadowmage
 */
@SuppressWarnings("ConstantConditions")
public class JsonTagReader {
	private JsonTagReader() {}

	public static NBTTagCompound parseTagCompound(String tag) {
		return Json.parseJson(tag).map(JsonTagReader::getTagFrom).orElse(new NBTTagCompound());
	}

	private static NBTTagCompound getTagFrom(Json.JsonObject compoundTagObject) {
		return (NBTTagCompound) getTagFor(compoundTagObject);
	}

	private static NBTBase getTagFor(Json.JsonObject jsonTagBase) {
		Json.JsonValue val = jsonTagBase.getValue("id");
		String id = val.getStringValue();
		Json.JsonAbstract value = jsonTagBase.getAbstract();
		if ("ct".equals(id)) {
			return getCompoundTagFor((Json.JsonObject) value);
		} else if ("ls".equals(id)) {
			return getListTagFor((Json.JsonArray) value);
		} else if ("pb".equals(id)) {
			return getByteTagFor((Json.JsonValue) value);
		} else if ("ps".equals(id)) {
			return getShortTagFor((Json.JsonValue) value);
		} else if ("pi".equals(id)) {
			return getIntTagFor((Json.JsonValue) value);
		} else if ("pl".equals(id)) {
			return getLongTagFor((Json.JsonValue) value);
		} else if ("pf".equals(id)) {
			return getFloatTagFor((Json.JsonValue) value);
		} else if ("pd".equals(id)) {
			return getDoubleTagFor((Json.JsonValue) value);
		} else if ("ab".equals(id)) {
			return getByteArrayTagFor((Json.JsonArray) value);
		} else if ("ai".equals(id)) {
			return getIntArrayTagFor((Json.JsonArray) value);
		} else if ("ss".equals(id)) {
			return getStringTagFor((Json.JsonValue) value);
		}
		return null;
	}

	private static NBTTagCompound getCompoundTagFor(Json.JsonObject compoundTagValues) {
		NBTTagCompound tag = new NBTTagCompound();
		for (String key : compoundTagValues.keySet()) {
			tag.setTag(key, getTagFor(compoundTagValues.getObject(key)));
		}
		return tag;
	}

	private static NBTTagList getListTagFor(Json.JsonArray listTagValues) {
		NBTTagList list = new NBTTagList();
		for (int i = 0; i < listTagValues.size(); i++) {
			list.appendTag(getTagFor(listTagValues.getObject(i)));
		}
		return list;
	}

	private static NBTTagIntArray getIntArrayTagFor(Json.JsonArray value) {
		int[] array = new int[value.size()];
		for (int i = 0; i < value.size(); i++) {
			array[i] = (int) value.getValue(i).getIntegerValue();
		}
		return new NBTTagIntArray(array);
	}

	private static NBTTagByteArray getByteArrayTagFor(Json.JsonArray value) {
		byte[] array = new byte[value.size()];
		for (int i = 0; i < value.size(); i++) {
			array[i] = (byte) value.getValue(i).getIntegerValue();
		}
		return new NBTTagByteArray(array);
	}

	private static NBTTagByte getByteTagFor(Json.JsonValue value) {
		return new NBTTagByte((byte) value.getIntegerValue());
	}

	private static NBTTagShort getShortTagFor(Json.JsonValue value) {
		return new NBTTagShort((short) value.getIntegerValue());
	}

	private static NBTTagInt getIntTagFor(Json.JsonValue value) {
		return new NBTTagInt((int) value.getIntegerValue());
	}

	private static NBTTagLong getLongTagFor(Json.JsonValue value) {
		return new NBTTagLong(value.getIntegerValue());
	}

	private static NBTTagFloat getFloatTagFor(Json.JsonValue value) {
		return new NBTTagFloat((float) value.getFloatValue());
	}

	private static NBTTagDouble getDoubleTagFor(Json.JsonValue value) {
		return new NBTTagDouble(value.getFloatValue());
	}

	private static NBTTagString getStringTagFor(Json.JsonValue value) {
		return new NBTTagString(value.getStringValue());
	}

}
