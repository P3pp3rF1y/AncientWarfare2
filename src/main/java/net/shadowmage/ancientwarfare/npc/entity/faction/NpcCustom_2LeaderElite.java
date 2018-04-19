package net.shadowmage.ancientwarfare.npc.entity.faction;

import net.minecraft.world.World;

public class NpcCustom_2LeaderElite extends NpcFactionLeader {

	public NpcCustom_2LeaderElite(World par1World) {
		super(par1World);
	}

	@Override
	public String getNpcType() {
		return "custom_2.leader.elite";
	}

}
