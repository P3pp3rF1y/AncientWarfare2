package net.shadowmage.ancientwarfare.npc.entity.faction;

import net.minecraft.world.World;

public class NpcFactionArcherElite extends NpcFactionArcher {
	public NpcFactionArcherElite(World world) {
		super(world);
	}

	public NpcFactionArcherElite(World world, String factionName) {
		super(world, factionName);
	}

	@Override
	public String getNpcType() {
		return super.getNpcType() + ".elite";
	}
}
