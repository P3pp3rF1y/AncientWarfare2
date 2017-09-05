//TODO world capability

package net.shadowmage.ancientwarfare.core.gamedata;

import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.storage.MapStorage;

/*
 * Helps building specific world data.
 */
public final class AWGameData {

    public static final AWGameData INSTANCE = new AWGameData();

    public <T extends WorldSavedData> T getData(World world, Class<T> clz) {
        return initData(world.mapStorage, clz);
    }

    public <T extends WorldSavedData> T getPerWorldData(World world, Class<T> clz) {
        return initData(world.perWorldStorage, clz);
    }

    @SuppressWarnings("unchecked")
    private <T extends WorldSavedData> T initData(MapStorage storage, Class<T> clz){
        String name = "AW"+clz.getSimpleName();
        T data = (T) storage.loadData(clz, name);
        if (data == null) {
            try {
                data = clz.getConstructor(String.class).newInstance(name);
                storage.setData(name, data);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return data;
    }

}
