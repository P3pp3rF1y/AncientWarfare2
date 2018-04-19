package net.shadowmage.ancientwarfare.npc.entity.faction;

import net.minecraft.world.World;

public class NpcVikingCivilianMale extends NpcFactionCivilian {

	public NpcVikingCivilianMale(World par1World) {
		super(par1World);
	}

	@Override
	public String getNpcType() {
		return "viking.civilian.male";
	}

}
