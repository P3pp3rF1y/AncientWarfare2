package net.shadowmage.ancientwarfare.npc.entity.faction;

import net.minecraft.world.World;

public class NpcFactionLeaderElite extends NpcFactionLeader {
	public NpcFactionLeaderElite(World world) {
		super(world);
	}

	public NpcFactionLeaderElite(World world, String factionName) {
		super(world, factionName);
	}

	@Override
	public String getNpcType() {
		return super.getNpcType() + ".elite";
	}
}
