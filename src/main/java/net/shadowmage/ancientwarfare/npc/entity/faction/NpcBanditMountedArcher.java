package net.shadowmage.ancientwarfare.npc.entity.faction;

import net.minecraft.world.World;

public class NpcBanditMountedArcher extends NpcFactionMountedArcher {

    public NpcBanditMountedArcher(World par1World) {
        super(par1World);
    }

    @Override
    public String getNpcType() {
        return "bandit.mounted_archer";
    }

}
