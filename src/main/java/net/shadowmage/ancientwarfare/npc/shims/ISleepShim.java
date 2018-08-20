package net.shadowmage.ancientwarfare.npc.shims;

import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;

public interface ISleepShim {
    int shouldSleep(World world, NpcBase npc);
}
