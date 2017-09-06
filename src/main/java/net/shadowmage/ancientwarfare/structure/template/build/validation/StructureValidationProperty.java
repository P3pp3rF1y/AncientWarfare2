package net.shadowmage.ancientwarfare.structure.template.build.validation;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.common.util.Constants;

import java.util.HashSet;
import java.util.Set;

public class StructureValidationProperty {

    public static final int DATA_TYPE_UNKNOWN = 0;
    public static final int DATA_TYPE_INT = 1;
    public static final int DATA_TYPE_BYTE = 2;
    public static final int DATA_TYPE_BOOLEAN = 3;
    public static final int DATA_TYPE_FLOAT = 4;
    public static final int DATA_TYPE_STRING = 5;
    public static final int DATA_TYPE_INT_ARRAY = 6;
    public static final int DATA_TYPE_STRING_SET = 7;

    String regName;
    final int dataType;
    Object data;

    private StructureValidationProperty(String regName, int dataType, Object defaultValue) {
        this.regName = regName;
        this.data = defaultValue;
        this.dataType = dataType;
    }

    public StructureValidationProperty(String regName, Object defaultValue) {
        this.regName = regName;
        this.data = defaultValue;
        if(data == null){
            dataType = DATA_TYPE_UNKNOWN;
        }else if (Integer.class.isInstance(data)) {
            dataType = DATA_TYPE_INT;
        }else if (Byte.class.isInstance(data)) {
            dataType = DATA_TYPE_BYTE;
        }else if (Float.class.isInstance(data)) {
            dataType = DATA_TYPE_FLOAT;
        }else if (Boolean.class.isInstance(data)) {
            dataType = DATA_TYPE_BOOLEAN;
        }else if (String.class.isInstance(data)) {
            dataType = DATA_TYPE_STRING;
        }else if (Set.class.isInstance(data)) {
            dataType = DATA_TYPE_STRING_SET;
        }else if (int[].class.isAssignableFrom(data.getClass())) {
            dataType = DATA_TYPE_INT_ARRAY;
        }else {
            dataType = DATA_TYPE_UNKNOWN;
        }
    }

    public int getDataType() {
        return dataType;
    }

    public String getRegName() {
        return regName;
    }

    public void setValue(Object value) {
        switch (dataType) {
            case DATA_TYPE_UNKNOWN: {
                data = value;
            }
            break;
            case DATA_TYPE_INT: {
                if (Integer.class.isInstance(value)) {
                    data = value;
                }
            }
            break;
            case DATA_TYPE_BYTE: {
                if (Byte.class.isInstance(value)) {
                    data = value;
                }
            }
            break;
            case DATA_TYPE_FLOAT: {
                if (Float.class.isInstance(value)) {
                    data = value;
                }
            }
            break;
            case DATA_TYPE_BOOLEAN: {
                if (Boolean.class.isInstance(value)) {
                    data = value;
                }
            }
            break;
            case DATA_TYPE_STRING: {
                if (String.class.isInstance(value)) {
                    data = value;
                }
            }
            break;
            case DATA_TYPE_STRING_SET: {
                if (Set.class.isInstance(value)) {
                    data = value;
                }
            }
            break;
            case DATA_TYPE_INT_ARRAY: {
                if (int[].class.isAssignableFrom(value.getClass())) {
                    data = value;
                }
            }
            break;
        }
    }

    public int getDataInt() {
        if (dataType == DATA_TYPE_INT) {
            return (Integer) data;
        }
        return 0;
    }

    public int[] getDataIntArray() {
        if (dataType == DATA_TYPE_INT_ARRAY) {
            return (int[]) data;
        }
        return new int[0];
    }

    public float getDataFloat() {
        if (dataType == DATA_TYPE_FLOAT) {
            return (Float) data;
        }
        return 0.f;
    }

    public String getDataString() {
        if (dataType == DATA_TYPE_STRING) {
            return (String) data;
        }
        return "";
    }

    public boolean getDataBoolean() {
        if (dataType == DATA_TYPE_BOOLEAN) {
            return (Boolean) data;
        }
        return false;
    }


    public Set<String> getDataStringSet() {
        if(dataType == DATA_TYPE_STRING_SET) {
            return (Set<String>) data;
        }
        return new HashSet<>();
    }

    public void readFromNBT(NBTTagCompound tag) {
        switch (dataType) {
            case DATA_TYPE_INT: {
                data = tag.getInteger(regName);
            }
            break;
            case DATA_TYPE_INT_ARRAY: {
                data = tag.getIntArray(regName);
            }
            break;
            case DATA_TYPE_BYTE: {
                data = tag.getByte(regName);
            }
            break;
            case DATA_TYPE_FLOAT: {
                data = tag.getFloat(regName);
            }
            break;
            case DATA_TYPE_BOOLEAN: {
                data = tag.getBoolean(regName);
            }
            break;
            case DATA_TYPE_STRING: {
                data = tag.getString(regName);
            }
            break;
            case DATA_TYPE_STRING_SET: {
                Set<String> data = new HashSet<>();
                NBTTagList names = tag.getTagList(regName, Constants.NBT.TAG_STRING);
                for (int i = 0; i < names.tagCount(); i++) {
                    data.add(names.getStringTagAt(i));
                }
                this.data = data;
            }
            break;
        }
    }

    public void writeToNBT(NBTTagCompound tag) {
        switch (dataType) {
            case DATA_TYPE_INT: {
                tag.setInteger(regName, getDataInt());
            }
            break;
            case DATA_TYPE_INT_ARRAY: {
                tag.setIntArray(regName, getDataIntArray());
            }
            break;
            case DATA_TYPE_BYTE: {
                tag.setByte(regName, (Byte) data);
            }
            break;
            case DATA_TYPE_FLOAT: {
                tag.setFloat(regName, getDataFloat());
            }
            break;
            case DATA_TYPE_BOOLEAN: {
                tag.setBoolean(regName, getDataBoolean());
            }
            break;
            case DATA_TYPE_STRING: {
                tag.setString(regName, getDataString());
            }
            break;
            case DATA_TYPE_STRING_SET: {
                NBTTagList names = new NBTTagList();
                Set<String> data = getDataStringSet();
                for (String name : data) {
                    names.appendTag(new NBTTagString(name));
                }
                tag.setTag(regName, names);
            }
            break;
        }
    }

    public StructureValidationProperty copy() {
        Object copy = data;
        if(copy != null){
            if(dataType == DATA_TYPE_STRING_SET) {
                copy = new HashSet<>(getDataStringSet());
            }else if(dataType == DATA_TYPE_INT_ARRAY){
                int[] temp = getDataIntArray();
                copy = new int[temp.length];
                System.arraycopy(temp, 0, copy, 0, temp.length);
            }
        }
        return new StructureValidationProperty(regName, dataType, copy);
    }
}
