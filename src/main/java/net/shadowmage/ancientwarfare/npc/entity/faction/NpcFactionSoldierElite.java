package net.shadowmage.ancientwarfare.npc.entity.faction;

import net.minecraft.world.World;

public class NpcFactionSoldierElite extends NpcFactionSoldier {
	@SuppressWarnings("unused")
	public NpcFactionSoldierElite(World world) {
		super(world);
	}

	@SuppressWarnings("unused")
	public NpcFactionSoldierElite(World world, String factionName) {
		super(world, factionName);
	}

	@Override
	public String getNpcType() {
		return super.getNpcType() + ".elite";
	}
}
