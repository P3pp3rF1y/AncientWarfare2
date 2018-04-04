package net.shadowmage.ancientwarfare.npc.entity.faction;

import net.minecraft.world.World;

public class NpcVikingMountedSoldier extends NpcFactionMountedSoldier {

	public NpcVikingMountedSoldier(World par1World) {
		super(par1World);
	}

	@Override
	public String getNpcType() {
		return "viking.cavalry";
	}

}
