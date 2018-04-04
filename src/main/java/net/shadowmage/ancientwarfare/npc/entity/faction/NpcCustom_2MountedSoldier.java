package net.shadowmage.ancientwarfare.npc.entity.faction;

import net.minecraft.world.World;

public class NpcCustom_2MountedSoldier extends NpcFactionMountedSoldier {

	public NpcCustom_2MountedSoldier(World par1World) {
		super(par1World);
	}

	@Override
	public String getNpcType() {
		return "custom_2.cavalry";
	}

}
