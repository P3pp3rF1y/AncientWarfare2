package net.shadowmage.ancientwarfare.npc.entity.faction;

import net.minecraft.world.World;

public class NpcDesertLeaderElite extends NpcFactionLeader {

    public NpcDesertLeaderElite(World par1World) {
        super(par1World);
    }

    @Override
    public String getNpcType() {
        return "desert.leader.elite";
    }

}
