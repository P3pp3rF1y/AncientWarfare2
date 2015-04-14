package net.shadowmage.ancientwarfare.npc.entity.faction;

import net.minecraft.world.World;

public class NpcVikingLeaderElite extends NpcFactionLeader {

    public NpcVikingLeaderElite(World par1World) {
        super(par1World);
    }

    @Override
    public String getNpcType() {
        return "viking.leader.elite";
    }

}
