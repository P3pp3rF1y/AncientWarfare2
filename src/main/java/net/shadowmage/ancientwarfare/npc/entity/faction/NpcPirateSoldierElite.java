package net.shadowmage.ancientwarfare.npc.entity.faction;

import net.minecraft.world.World;

public class NpcPirateSoldierElite extends NpcFactionSoldier {

	public NpcPirateSoldierElite(World par1World) {
		super(par1World);
	}

	@Override
	public String getNpcType() {
		return "pirate.soldier.elite";
	}

}
