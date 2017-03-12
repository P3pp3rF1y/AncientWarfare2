package net.shadowmage.ancientwarfare.core.interop;

import net.minecraft.world.World;

public interface InteropEnviromineInterface {
    public void schedulePhysUpdate(World world, int x, int y, int z, boolean updateSelf, String type);
}
