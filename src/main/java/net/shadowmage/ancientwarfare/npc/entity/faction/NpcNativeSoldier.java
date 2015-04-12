package net.shadowmage.ancientwarfare.npc.entity.faction;

import net.minecraft.world.World;

public class NpcNativeSoldier extends NpcFactionSoldier {

    public NpcNativeSoldier(World par1World) {
        super(par1World);
    }

    @Override
    public String getNpcType() {
        return "native.soldier";
    }

}
