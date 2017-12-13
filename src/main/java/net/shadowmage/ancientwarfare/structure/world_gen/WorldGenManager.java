package net.shadowmage.ancientwarfare.structure.world_gen;

import com.google.common.collect.Maps;
import net.minecraft.world.WorldServer;

import java.util.Map;

public class WorldGenManager {
    private static final Map<Integer, NoGenWorld> preGenWorls = Maps.newHashMap();

    public static NoGenWorld getPreGenWorld(WorldServer world) {
        int dimId = world.provider.getDimension();
        if (!preGenWorls.containsKey(dimId)) {
            preGenWorls.put(dimId, new NoGenWorld(world));
        }
        return preGenWorls.get(dimId);
    }

}
