package net.shadowmage.ancientwarfare.npc.entity.faction;

import net.minecraft.world.World;

public class NpcFactionCivilianMale extends NpcFactionCivilian {
	public NpcFactionCivilianMale(World world) {
		super(world);
	}

	public NpcFactionCivilianMale(World world, String factionName) {
		super(world, factionName);
	}

	@Override
	public String getNpcType() {
		return factionName + ".civilian.male";
	}
}
