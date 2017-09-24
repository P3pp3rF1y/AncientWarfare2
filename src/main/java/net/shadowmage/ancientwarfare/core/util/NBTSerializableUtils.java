package net.shadowmage.ancientwarfare.core.util;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.ArrayList;
import java.util.List;

/*
 * Created by Olivier on 15/05/2015.
 */
public interface NBTSerializableUtils {
    static void write(NBTTagCompound tag, String key, List<? extends INBTSerializable> elements) {
        NBTTagList list = new NBTTagList();
        for (INBTSerializable serializable : elements) {
            list.appendTag(serializable.serializeNBT());
        }
        tag.setTag(key, list);
    }

    static <T extends INBTSerializable> List<T> read(NBTTagCompound tag, String key, Class<T> supplier){
        NBTTagList tags = tag.getTagList(key, Constants.NBT.TAG_COMPOUND);
        ArrayList<T> list = new ArrayList<>();
        for(int i = 0; i < tags.tagCount(); i++){
            try {
                T element = supplier.newInstance();
                element.deserializeNBT(tags.getCompoundTagAt(i));
                list.add(element);
            }catch (Throwable ignored){}
        }
        return list;
    }
}
