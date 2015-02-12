package net.shadowmage.ancientwarfare.npc.entity.faction;

import net.minecraft.world.World;

public class NpcCustom_1LeaderElite extends NpcFactionLeader {

    public NpcCustom_1LeaderElite(World par1World) {
        super(par1World);
    }

    @Override
    public String getNpcType() {
        return "custom_1.leader.elite";
    }

}
