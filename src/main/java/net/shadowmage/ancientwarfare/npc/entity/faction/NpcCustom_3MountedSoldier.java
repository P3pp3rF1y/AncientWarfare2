package net.shadowmage.ancientwarfare.npc.entity.faction;

import net.minecraft.world.World;

public class NpcCustom_3MountedSoldier extends NpcFactionMountedSoldier {

	public NpcCustom_3MountedSoldier(World par1World) {
		super(par1World);
	}

	@Override
	public String getNpcType() {
		return "custom_3.cavalry";
	}

}
