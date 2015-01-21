package net.shadowmage.ancientwarfare.npc.entity.faction;

import net.minecraft.world.World;

public class NpcNativeMountedSoldier extends NpcFactionMountedSoldier {

    public NpcNativeMountedSoldier(World par1World) {
        super(par1World);
    }

    @Override
    public String getNpcType() {
        return "native.cavalry";
    }

}
