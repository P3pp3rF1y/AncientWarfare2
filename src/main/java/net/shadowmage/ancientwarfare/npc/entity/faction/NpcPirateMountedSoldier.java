package net.shadowmage.ancientwarfare.npc.entity.faction;

import net.minecraft.world.World;

public class NpcPirateMountedSoldier extends NpcFactionMountedSoldier {

    public NpcPirateMountedSoldier(World par1World) {
        super(par1World);
        // TODO Auto-generated constructor stub
    }

    @Override
    public String getNpcType() {
        return "pirate.cavalry";
    }

}
