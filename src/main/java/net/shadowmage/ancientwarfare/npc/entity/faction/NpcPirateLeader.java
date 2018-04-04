package net.shadowmage.ancientwarfare.npc.entity.faction;

import net.minecraft.world.World;

public class NpcPirateLeader extends NpcFactionLeader {

	public NpcPirateLeader(World par1World) {
		super(par1World);
	}

	@Override
	public String getNpcType() {
		return "pirate.leader";
	}

}
