package net.shadowmage.ancientwarfare.core.entity;

import net.minecraft.entity.DataWatcher;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.core.util.StringTools;

import java.util.Comparator;

/**
 * Created by Olivier on 06/07/2015.
 */
public class WatchedData extends DataWatcher.WatchableObject{
    public enum Type{
        BYTE(Byte.class), SHORT(Short.class), INT(Integer.class), FLOAT(Float.class), STRING(String.class), STACK(ItemStack.class);

        private static final String TAG = "Value";
        private final Class c;
        private Type(Class c){
            this.c = c;
        }

        public Type next(){
            return Type.values()[(ordinal()+1)%Type.values().length];
        }

        public static int index(Class cl){
            for(Type type : values()){
                if(cl.equals(type.c)){
                    return type.ordinal();
                }
            }
            return -1;
        }

        public Object getValue(NBTTagCompound compound){
            if(this == BYTE){
                return compound.getByte(TAG);
            }else if(this == SHORT){
                return compound.getShort(TAG);
            }else if(this == INT){
                return compound.getInteger(TAG);
            }else if(this == FLOAT){
                return compound.getFloat(TAG);
            }else if(this == STRING){
                return compound.getString(TAG);
            }else if(this == STACK){
                return ItemStack.loadItemStackFromNBT(compound.getCompoundTag(TAG));
            }
            return null;
        }

        public void setValue(NBTTagCompound compound, Object object) {
            if(this == BYTE){
                compound.setByte(TAG, (Byte) object);
            }else if(this == SHORT){
                compound.setShort(TAG, (Short) object);
            }else if(this == INT){
                compound.setInteger(TAG, (Integer) object);
            }else if(this == FLOAT){
                compound.setFloat(TAG, (Float) object);
            }else if(this == STRING){
                compound.setString(TAG, (String) object);
            }else if(this == STACK){
                NBTTagCompound nbt = new NBTTagCompound();
                ((ItemStack) object).writeToNBT(nbt);
                compound.setTag(TAG, nbt);
            }
        }

        public Object parse(String text) {
            if(this == BYTE)
                return Byte.valueOf(text);
            else if(this == SHORT)
                return Short.valueOf(text);
            else if(this == INT)
                return Integer.valueOf(text);
            else if(this == FLOAT)
                return Float.valueOf(text);
            else if(this == STACK) {
                String[] splits = StringTools.parseStringArray(text);
                if(splits.length > 2){
                    return StringTools.safeParseStack(splits[0], splits[1], splits[2]);
                }else if(splits.length == 2){
                    return StringTools.safeParseStack(splits[0], "0", splits[1]);
                }
                return StringTools.safeParseStack(text, "0", "1");

            }
            return text;
        }

        public String toString(Object value){
            if(this == BYTE)
                return Byte.toString((Byte) value);
            else if(this == SHORT)
                return Short.toString((Short) value);
            else if(this == INT)
                return Integer.toString((Integer) value);
            else if(this == FLOAT)
                return Float.toString((Float) value);
            else if(this == STACK) {
                ItemStack stack = (ItemStack) value;
                String result = Item.itemRegistry.getNameForObject(stack.getItem());
                return result + "," + stack.getItemDamage() + "," + stack.stackSize;
            }
            return value.toString();
        }
    }

    public WatchedData(int key, Object value) {
        super(Type.index(value.getClass()), key, value);
    }

    public WatchedData(Type type, int key, String text){
        super(type.ordinal(), key, type.parse(text));
    }

    public boolean isValid(){
        return getObject()!=null && getDataValueId()<=31 && getObjectType()>=0 && getObjectType()<Type.values().length;
    }

    public Type getType(){
        return Type.values()[getObjectType()];
    }

    public boolean collideWith(DataWatcher.WatchableObject object){
        return getDataValueId() == object.getDataValueId();
    }

    public boolean canReplace(DataWatcher.WatchableObject object){
        return collideWith(object) && getObjectType() == object.getObjectType();
    }

    public void add(DataWatcher watcher){
        watcher.addObject(getDataValueId(), getObject());
    }

    public static WatchedData fromTag(NBTTagCompound tagCompound) {
        byte i = tagCompound.getByte("Index");
        Type t = Type.valueOf(tagCompound.getString("Type"));
        Object value = t.getValue(tagCompound);
        if(value!=null){
            return new WatchedData(i, value);
        }
        return null;
    }

    public NBTTagCompound toTag(){
        NBTTagCompound tagCompound = new NBTTagCompound();
        tagCompound.setByte("Index", (byte) getDataValueId());
        Type t = Type.values()[getObjectType()];
        tagCompound.setString("Type", t.name());
        t.setValue(tagCompound, getObject());
        return tagCompound;
    }

    @Override
    public boolean equals(Object object){
        if(this == object){
            return true;
        }
        if(object instanceof WatchedData){
            return this.canReplace((DataWatcher.WatchableObject) object) && this.getObject().equals(((DataWatcher.WatchableObject) object).getObject());
        }
        return false;
    }

    public enum IndexSorter implements Comparator<DataWatcher.WatchableObject>{
        INSTANCE;

        @Override
        public int compare(DataWatcher.WatchableObject o1, DataWatcher.WatchableObject o2) {
            return o1.getDataValueId() - o2.getDataValueId();
        }
    }
}
