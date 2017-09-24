package net.shadowmage.ancientwarfare.npc.entity.faction;

import net.minecraft.world.World;

public class NpcCustom_3MountedArcher extends NpcFactionMountedArcher {

    public NpcCustom_3MountedArcher(World par1World) {
        super(par1World);
    }

    @Override
    public String getNpcType() {
        return "custom_3.mounted_archer";
    }

}
