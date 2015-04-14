package net.shadowmage.ancientwarfare.core.util;

import net.minecraft.nbt.*;
import net.shadowmage.ancientwarfare.core.util.Json.JsonAbstract;
import net.shadowmage.ancientwarfare.core.util.Json.JsonArray;
import net.shadowmage.ancientwarfare.core.util.Json.JsonObject;
import net.shadowmage.ancientwarfare.core.util.Json.JsonValue;

/**
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
public class JsonTagReader {

    public static NBTTagCompound parseTagCompound(String tag) {
        JsonObject object = Json.parseJson(tag);
        return getTagFrom(object);
    }

    public static NBTTagCompound getTagFrom(JsonObject compoundTagObject) {
        return (NBTTagCompound) getTagFor(compoundTagObject);
    }

    private static NBTBase getTagFor(JsonObject jsonTagBase) {
        JsonValue val = jsonTagBase.getValue("id");
        String id = val.getStringValue();
        JsonAbstract value = jsonTagBase.getAbstract("val");
        if ("ct".equals(id)) {
            return getCompoundTagFor((JsonObject) value);
        } else if ("ls".equals(id)) {
            return getListTagFor((JsonArray) value);
        } else if ("pb".equals(id)) {
            return getByteTagFor((JsonValue) value);
        } else if ("ps".equals(id)) {
            return getShortTagFor((JsonValue) value);
        } else if ("pi".equals(id)) {
            return getIntTagFor((JsonValue) value);
        } else if ("pl".equals(id)) {
            return getLongTagFor((JsonValue) value);
        } else if ("pf".equals(id)) {
            return getFloatTagFor((JsonValue) value);
        } else if ("pd".equals(id)) {
            return getDoubleTagFor((JsonValue) value);
        } else if ("ab".equals(id)) {
            return getByteArrayTagFor((JsonArray) value);
        } else if ("ai".equals(id)) {
            return getIntArrayTagFor((JsonArray) value);
        } else if ("ss".equals(id)) {
            return getStringTagFor((JsonValue) value);
        }
        return null;
    }

    private static NBTTagCompound getCompoundTagFor(JsonObject compoundTagValues) {
        NBTTagCompound tag = new NBTTagCompound();
        for (String key : compoundTagValues.keySet()) {
            tag.setTag(key, getTagFor(compoundTagValues.getObject(key)));
        }
        return tag;
    }

    private static NBTTagList getListTagFor(JsonArray listTagValues) {
        NBTTagList list = new NBTTagList();
        for (int i = 0; i < listTagValues.size(); i++) {
            list.appendTag(getTagFor(listTagValues.getObject(i)));
        }
        return list;
    }

    private static NBTTagIntArray getIntArrayTagFor(JsonArray value) {
        int[] array = new int[value.size()];
        for (int i = 0; i < value.size(); i++) {
            array[i] = (int) value.getValue(i).getIntegerValue();
        }
        return new NBTTagIntArray(array);
    }

    private static NBTTagByteArray getByteArrayTagFor(JsonArray value) {
        byte[] array = new byte[value.size()];
        for (int i = 0; i < value.size(); i++) {
            array[i] = (byte) value.getValue(i).getIntegerValue();
        }
        return new NBTTagByteArray(array);
    }

    private static NBTTagByte getByteTagFor(JsonValue value) {
        return new NBTTagByte((byte) value.getIntegerValue());
    }

    private static NBTTagShort getShortTagFor(JsonValue value) {
        return new NBTTagShort((short) value.getIntegerValue());
    }

    private static NBTTagInt getIntTagFor(JsonValue value) {
        return new NBTTagInt((int) value.getIntegerValue());
    }

    private static NBTTagLong getLongTagFor(JsonValue value) {
        return new NBTTagLong(value.getIntegerValue());
    }

    private static NBTTagFloat getFloatTagFor(JsonValue value) {
        return new NBTTagFloat((float) value.getFloatValue());
    }

    private static NBTTagDouble getDoubleTagFor(JsonValue value) {
        return new NBTTagDouble(value.getFloatValue());
    }

    private static NBTTagString getStringTagFor(JsonValue value) {
        return new NBTTagString(value.getStringValue());
    }

}
