package net.shadowmage.ancientwarfare.npc.entity.faction;

import net.minecraft.world.World;

public class NpcCustom_1MountedArcher extends NpcFactionMountedArcher {

	public NpcCustom_1MountedArcher(World par1World) {
		super(par1World);
	}

	@Override
	public String getNpcType() {
		return "custom_1.mounted_archer";
	}

}
