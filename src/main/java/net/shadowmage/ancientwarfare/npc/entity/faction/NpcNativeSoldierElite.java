package net.shadowmage.ancientwarfare.npc.entity.faction;

import net.minecraft.world.World;

public class NpcNativeSoldierElite extends NpcFactionSoldier {

    public NpcNativeSoldierElite(World par1World) {
        super(par1World);
    }

    @Override
    public String getNpcType() {
        return "native.soldier.elite";
    }

}
