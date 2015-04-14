package net.shadowmage.ancientwarfare.npc.entity.faction;

import net.minecraft.world.World;

public class NpcDesertMountedArcher extends NpcFactionMountedArcher {

    public NpcDesertMountedArcher(World par1World) {
        super(par1World);
        // TODO Auto-generated constructor stub
    }

    @Override
    public String getNpcType() {
        return "desert.mounted_archer";
    }

}
