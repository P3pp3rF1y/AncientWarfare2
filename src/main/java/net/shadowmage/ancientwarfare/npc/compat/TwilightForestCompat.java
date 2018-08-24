package net.shadowmage.ancientwarfare.npc.compat;

import net.minecraft.entity.Entity;
import net.shadowmage.ancientwarfare.core.compat.ICompat;
import twilightforest.TFConfig;

public class TwilightForestCompat implements ICompat {
    @Override
    public String getModId() {
        return "twilightforest";
    }

    @Override
    public void init() {
    }

    public boolean isEntityInTF(Entity entity){
        return entity.dimension == TFConfig.dimension.dimensionID;
    }
}
