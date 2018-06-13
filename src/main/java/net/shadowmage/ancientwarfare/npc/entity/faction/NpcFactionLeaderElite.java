package net.shadowmage.ancientwarfare.npc.entity.faction;

import net.minecraft.world.World;

public class NpcFactionLeaderElite extends NpcFactionLeader {
	@SuppressWarnings("unused")
	public NpcFactionLeaderElite(World world) {
		super(world);
	}

	@SuppressWarnings("unused")
	public NpcFactionLeaderElite(World world, String factionName) {
		super(world, factionName);
	}

	@Override
	public String getNpcType() {
		return super.getNpcType() + ".elite";
	}
}
