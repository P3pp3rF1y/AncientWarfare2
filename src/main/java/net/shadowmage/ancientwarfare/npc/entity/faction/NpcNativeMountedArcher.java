package net.shadowmage.ancientwarfare.npc.entity.faction;

import net.minecraft.world.World;

public class NpcNativeMountedArcher extends NpcFactionMountedArcher {

    public NpcNativeMountedArcher(World par1World) {
        super(par1World);
    }

    @Override
    public String getNpcType() {
        return "native.mounted_archer";
    }

}
