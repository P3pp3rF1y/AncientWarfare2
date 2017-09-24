package net.shadowmage.ancientwarfare.npc.entity.faction;

import net.minecraft.world.World;

public class NpcBanditSoldierElite extends NpcFactionSoldier {

    public NpcBanditSoldierElite(World par1World) {
        super(par1World);
    }

    @Override
    public String getNpcType() {
        return "bandit.soldier.elite";
    }

}
