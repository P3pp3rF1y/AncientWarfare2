package net.shadowmage.ancientwarfare.npc.entity.faction;

import net.minecraft.world.World;

public class NpcCustom_2Trader extends NpcFactionTrader {

	public NpcCustom_2Trader(World par1World) {
		super(par1World);
	}

	@Override
	public String getNpcType() {
		return "custom_2.trader";
	}

}
