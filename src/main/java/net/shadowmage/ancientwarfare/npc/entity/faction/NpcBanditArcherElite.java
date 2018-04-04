package net.shadowmage.ancientwarfare.npc.entity.faction;

import net.minecraft.world.World;

public class NpcBanditArcherElite extends NpcFactionArcher {

	public NpcBanditArcherElite(World par1World) {
		super(par1World);
	}

	@Override
	public String getNpcType() {
		return "bandit.archer.elite";
	}

}
