package net.shadowmage.ancientwarfare.npc.entity.faction;

import net.minecraft.world.World;

public class NpcCustom_1Leader extends NpcFactionLeader {

	public NpcCustom_1Leader(World par1World) {
		super(par1World);
	}

	@Override
	public String getNpcType() {
		return "custom_1.leader";
	}

}
