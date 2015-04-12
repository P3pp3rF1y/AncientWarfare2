package net.shadowmage.ancientwarfare.core.util;

import net.minecraft.nbt.*;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.util.Json.JsonAbstract;
import net.shadowmage.ancientwarfare.core.util.Json.JsonArray;
import net.shadowmage.ancientwarfare.core.util.Json.JsonObject;
import net.shadowmage.ancientwarfare.core.util.Json.JsonValue;

import java.util.Set;

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
        tag = (NBTTagList) tag.copy();
        while (tag.tagCount() > 0) {
            valueJson = getJsonFor(tag.removeTag(0));
            if (valueJson != null) {
                base.add(valueJson);
            }
        }
        return base;
    }

    @SuppressWarnings("unchecked")
    private static JsonObject getJsonValueForCompoundTag(NBTTagCompound tag) {
        JsonObject base = new JsonObject();
        JsonAbstract valueJson;
        for (String key : ((Set<String>) tag.func_150296_c())) {
            valueJson = getJsonFor(tag.getTag(key));
            if (valueJson != null) {
                base.writeAbstract(key, valueJson);
            }
        }
        return base;
    }

    private static JsonArray getJsonValueForByteArray(NBTTagByteArray tag) {
        JsonArray array = new JsonArray();
        byte[] bytes = tag.func_150292_c();
        for (byte b : bytes) {
            array.add(new JsonValue(String.valueOf(b)));
        }
        return array;
    }

    private static JsonArray getJsonValueForIntArray(NBTTagIntArray tag) {
        JsonArray array = new JsonArray();
        int[] ints = tag.func_150302_c();
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
                value = new JsonValue(String.valueOf(((NBTTagByte) tag).func_150290_f()));
                break;
            }
            case 2://short
            {
                value = new JsonValue(String.valueOf(((NBTTagShort) tag).func_150289_e()));
                break;
            }
            case 3://int
            {
                value = new JsonValue(String.valueOf(((NBTTagInt) tag).func_150287_d()));
                break;
            }
            case 4://long
            {
                value = new JsonValue(String.valueOf(((NBTTagLong) tag).func_150291_c()));
                break;
            }
            case 5://float
            {
                value = new JsonValue(String.valueOf(((NBTTagFloat) tag).func_150288_h()));
                break;
            }
            case 6://double
            {
                value = new JsonValue(String.valueOf(((NBTTagDouble) tag).func_150286_g()));
                break;
            }
            case 7://byte array
            {
                value = getJsonValueForByteArray((NBTTagByteArray) tag);
                break;
            }
            case 8://string
            {
                value = new JsonValue(((NBTTagString) tag).func_150285_a_());
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

    public static void JsonTest() {
        AWLog.logDebug("testing json read/write!!");
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger("fooInt", 1);
        tag.setString("fooString", "stringData");
        tag.setShort("fooShort", (short) 1);
        tag.setByteArray("fooByteArray", new byte[]{0, 1, 0, 1});
        tag.setIntArray("fooIntArray", new int[]{0, 1, 0, 1, 0, 1});

        NBTTagList list = new NBTTagList();
        list.appendTag(new NBTTagString("listString1"));
        list.appendTag(new NBTTagString("listString2"));
        list.appendTag(new NBTTagString("listString3"));
        list.appendTag(new NBTTagString("listString4"));
        tag.setTag("list", list);

        JsonObject o = JsonTagWriter.getJsonForTag(tag);
        String os = Json.getJsonData(o);

        AWLog.logDebug("pre out : " + os);
        o = Json.parseJson(os);
        os = Json.getJsonData(o);
        AWLog.logDebug("post out: " + os);

        AWLog.logDebug("pre tag : " + tag);
        tag = JsonTagReader.getTagFrom(o);
        AWLog.logDebug("post tag: " + tag);

        Integer.parseInt("foo");//crash
    }

}
