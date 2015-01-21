package net.shadowmage.ancientwarfare.core.gamedata;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraftforge.event.world.WorldEvent;

import java.util.HashMap;

/**
 * Holds run-time references to world-saved data.
 * Auto-loads data instances upon world-load.  Data classes must be pre-registered for this mechanism to work.
 */
public class AWGameData {

    public static final AWGameData INSTANCE = new AWGameData();

    private HashMap<String, Class<? extends WorldSavedData>> dataClasses = new HashMap<String, Class<? extends WorldSavedData>>();

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load evt) {
        World world = evt.world;
        if (world.isRemote) {
            return;
        }
        for (String name : dataClasses.keySet()) {
            world.mapStorage.loadData(dataClasses.get(name), name);
        }
    }

    public void registerSaveData(String name, Class<? extends WorldSavedData> clz) {
        dataClasses.put(name, clz);
    }

    @SuppressWarnings("unchecked")
    public <T extends WorldSavedData> T getData(String name, World world, Class<T> clz) {
        if (!WorldSavedData.class.isAssignableFrom(clz)) {
            throw new RuntimeException("Attempt to load data class: " + clz + " for name: " + name + " failed because it is not an instance of WorldSavedData.");
        }
        if (!dataClasses.containsKey(name)) {
            throw new RuntimeException("Attempt to load unregistered data class: " + clz + " for name: " + name + ".  Data classes must be registered during mod initialization.");
        }
        T data = (T) world.mapStorage.loadData(clz, name);
        if (data == null) {
            try {
                data = clz.newInstance();
                world.mapStorage.setData(name, data);
                return data;
            } catch (InstantiationException e) {
                throw new RuntimeException("Attempt to load data class: " + clz + " for name: " + name + " failed because class needs a no-param constructor!");
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return data;
    }

    @SuppressWarnings("unchecked")
    public <T extends WorldSavedData> T getPerWorldData(String name, World world, Class<? extends WorldSavedData> clz) {
        T data = (T) world.perWorldStorage.loadData(clz, name);
        if (data == null) {
            try {
                data = (T) clz.newInstance();
                world.perWorldStorage.setData(name, data);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return data;
    }

}
