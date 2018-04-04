package net.shadowmage.ancientwarfare.npc.entity.faction;

import net.minecraft.world.World;

public class NpcCustom_2Leader extends NpcFactionLeader {

	public NpcCustom_2Leader(World par1World) {
		super(par1World);
	}

	@Override
	public String getNpcType() {
		return "custom_2.leader";
	}

}
