package net.shadowmage.ancientwarfare.core.gamedata;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraftforge.event.world.WorldEvent;

import java.util.HashMap;

/**
 * Helps building specific world data.
 */
public class AWGameData {

    public static final AWGameData INSTANCE = new AWGameData();

    @SuppressWarnings("unchecked")
    public <T extends WorldSavedData> T getData(World world, Class<T> clz) {
        String name = "AW"+clz.getSimpleName();
        T data = (T) world.loadItemData(clz, name);
        if (data == null) {
            try {
                data = clz.getConstructor(String.class).newInstance(name);
                world.setItemData(name, data);
                return data;
            } catch (Exception e) {
                throw new RuntimeException("Attempt to load data class: " + clz + " for name: " + name + " failed !");
            }
        }
        return data;
    }

    @SuppressWarnings("unchecked")
    public <T extends WorldSavedData> T getPerWorldData(World world, Class<T> clz) {
        String name = "AW"+clz.getSimpleName();
        T data = (T) world.perWorldStorage.loadData(clz, name);
        if (data == null) {
            try {
                data = clz.getConstructor(String.class).newInstance(name);
                world.perWorldStorage.setData(name, data);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return data;
    }

}
