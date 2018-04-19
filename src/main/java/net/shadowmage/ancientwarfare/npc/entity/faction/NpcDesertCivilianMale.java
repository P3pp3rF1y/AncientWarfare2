package net.shadowmage.ancientwarfare.npc.entity.faction;

import net.minecraft.world.World;

public class NpcDesertCivilianMale extends NpcFactionCivilian {

	public NpcDesertCivilianMale(World par1World) {
		super(par1World);
	}

	@Override
	public String getNpcType() {
		return "desert.civilian.male";
	}

}
