package net.shadowmage.ancientwarfare.npc.entity.faction;

import net.minecraft.world.World;

public class NpcFactionCivilianFemale extends NpcFactionCivilian {
	public NpcFactionCivilianFemale(World world) {
		super(world);
	}

	public NpcFactionCivilianFemale(World world, String factionName) {
		super(world, factionName);
	}

	@Override
	public String getNpcType() {
		return factionName + ".civilian.female";
	}
}
