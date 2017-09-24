package net.shadowmage.ancientwarfare.npc.entity.faction;

import net.minecraft.world.World;

public class NpcCustom_2MountedArcher extends NpcFactionMountedArcher {

    public NpcCustom_2MountedArcher(World par1World) {
        super(par1World);
    }

    @Override
    public String getNpcType() {
        return "custom_2.mounted_archer";
    }

}
