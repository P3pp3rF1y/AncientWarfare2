package net.shadowmage.ancientwarfare.npc.entity.faction;

import net.minecraft.world.World;

public class NpcCustom_3LeaderElite extends NpcFactionLeader {

    public NpcCustom_3LeaderElite(World par1World) {
        super(par1World);
    }

    @Override
    public String getNpcType() {
        return "custom_3.leader.elite";
    }

}
