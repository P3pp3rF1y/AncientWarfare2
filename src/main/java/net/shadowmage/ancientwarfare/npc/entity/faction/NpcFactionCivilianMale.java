package net.shadowmage.ancientwarfare.npc.entity.faction;

import net.minecraft.world.World;

public class NpcFactionCivilianMale extends NpcFactionCivilian {
	@SuppressWarnings("unused")
	public NpcFactionCivilianMale(World world) {
		super(world);
	}

	@SuppressWarnings("unused")
	public NpcFactionCivilianMale(World world, String factionName) {
		super(world, factionName);
	}

	@Override
	public String getNpcType() {
		return "civilian.male";
	}
}
