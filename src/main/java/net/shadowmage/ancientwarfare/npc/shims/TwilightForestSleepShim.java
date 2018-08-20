package net.shadowmage.ancientwarfare.npc.shims;

import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;
import twilightforest.TFConfig;

public class TwilightForestSleepShim implements ISleepShim{
    @Override
    public int shouldSleep(World world, NpcBase npc) {
        if(npc.dimension == TFConfig.dimension.dimensionID && (world.getWorldTime() < 1000 || world.getWorldTime() > 13000)){
            return 0;
        }else{
            return -1;
        }
    }
}
