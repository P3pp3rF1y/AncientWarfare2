package net.shadowmage.ancientwarfare.npc.entity.faction;

import net.minecraft.world.World;

public class NpcCustom_1MountedSoldier extends NpcFactionMountedSoldier {

	public NpcCustom_1MountedSoldier(World par1World) {
		super(par1World);
	}

	@Override
	public String getNpcType() {
		return "custom_1.cavalry";
	}

}
