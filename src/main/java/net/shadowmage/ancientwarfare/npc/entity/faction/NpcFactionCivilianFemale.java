package net.shadowmage.ancientwarfare.npc.entity.faction;

import net.minecraft.world.World;

public class NpcFactionCivilianFemale extends NpcFactionCivilian {
	@SuppressWarnings("unused")
	public NpcFactionCivilianFemale(World world) {
		super(world);
	}

	@SuppressWarnings("unused")
	public NpcFactionCivilianFemale(World world, String factionName) {
		super(world, factionName);
	}

	@Override
	public String getNpcType() {
		return "civilian.female";
	}

	@Override
	public boolean isFemale() {
		return true;
	}
}
