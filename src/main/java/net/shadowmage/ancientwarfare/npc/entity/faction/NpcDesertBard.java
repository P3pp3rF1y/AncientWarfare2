package net.shadowmage.ancientwarfare.npc.entity.faction;

import net.minecraft.world.World;

public class NpcDesertBard extends NpcFactionBard {

	public NpcDesertBard(World par1World) {
		super(par1World);
	}

	@Override
	public String getNpcType() {
		return "desert.bard";
	}

}
