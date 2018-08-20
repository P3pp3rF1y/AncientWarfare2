package net.shadowmage.ancientwarfare.npc.compat;

import net.shadowmage.ancientwarfare.core.compat.ICompat;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;
import net.shadowmage.ancientwarfare.npc.shims.TwilightForestSleepShim;

public class TwilightForestCompat implements ICompat {
    @Override
    public String getModId() {
        return "twilightforest";
    }
//1000
//13000


    @Override
    public void init() {
        NpcBase.addSleepShim(new TwilightForestSleepShim());
    }
}
