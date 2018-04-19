package net.shadowmage.ancientwarfare.npc.entity.faction;

import net.minecraft.world.World;

public class NpcCustom_2Soldier extends NpcFactionSoldier {

	public NpcCustom_2Soldier(World par1World) {
		super(par1World);
	}

	@Override
	public String getNpcType() {
		return "custom_2.soldier";
	}

}
