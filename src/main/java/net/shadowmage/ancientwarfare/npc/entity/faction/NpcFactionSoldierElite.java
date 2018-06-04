package net.shadowmage.ancientwarfare.npc.entity.faction;

import net.minecraft.world.World;

public class NpcFactionSoldierElite extends NpcFactionSoldier {
	public NpcFactionSoldierElite(World world) {
		super(world);
	}

	public NpcFactionSoldierElite(World world, String factionName) {
		super(world, factionName);
	}

	@Override
	public String getNpcType() {
		return super.getNpcType() + ".elite";
	}
}
