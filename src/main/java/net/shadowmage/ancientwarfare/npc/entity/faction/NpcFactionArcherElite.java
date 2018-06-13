package net.shadowmage.ancientwarfare.npc.entity.faction;

import net.minecraft.world.World;

public class NpcFactionArcherElite extends NpcFactionArcher {
	@SuppressWarnings("unused")
	public NpcFactionArcherElite(World world) {
		super(world);
	}

	@SuppressWarnings("unused")
	public NpcFactionArcherElite(World world, String factionName) {
		super(world, factionName);
	}

	@Override
	public String getNpcType() {
		return super.getNpcType() + ".elite";
	}
}
