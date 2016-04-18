package net.shadowmage.ancientwarfare.core.interfaces;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Olivier on 15/05/2015.
 */
public interface INBTSerialable {
    void readFromNBT(NBTTagCompound tag);

    NBTTagCompound writeToNBT(NBTTagCompound tag);

    public static class Helper {

        public static void write(NBTTagCompound tag, String key, List<? extends INBTSerialable> elements) {
            NBTTagList list = new NBTTagList();
            for (INBTSerialable serialable : elements) {
                list.appendTag(serialable.writeToNBT(new NBTTagCompound()));
            }
            tag.setTag(key, list);
        }

        public static <T extends INBTSerialable> List<T> read(NBTTagCompound tag, String key, Class<T> supplier){
            NBTTagList tags = tag.getTagList(key, Constants.NBT.TAG_COMPOUND);
            ArrayList<T> list = new ArrayList<T>();
            for(int i = 0; i < tags.tagCount(); i++){
                try {
                    T element = supplier.newInstance();
                    element.readFromNBT(tags.getCompoundTagAt(i));
                    list.add(element);
                }catch (Throwable ignored){}
            }
            return list;
        }
    }
}
