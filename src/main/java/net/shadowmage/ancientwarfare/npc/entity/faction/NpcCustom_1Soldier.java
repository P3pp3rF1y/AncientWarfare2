package net.shadowmage.ancientwarfare.npc.entity.faction;

import net.minecraft.world.World;

public class NpcCustom_1Soldier extends NpcFactionSoldier {

	public NpcCustom_1Soldier(World par1World) {
		super(par1World);
	}

	@Override
	public String getNpcType() {
		return "custom_1.soldier";
	}

}
