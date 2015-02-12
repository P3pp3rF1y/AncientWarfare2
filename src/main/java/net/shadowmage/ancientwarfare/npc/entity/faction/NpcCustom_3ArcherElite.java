package net.shadowmage.ancientwarfare.npc.entity.faction;

import net.minecraft.world.World;

public class NpcCustom_3ArcherElite extends NpcFactionArcher {

    public NpcCustom_3ArcherElite(World par1World) {
        super(par1World);
    }

    @Override
    public String getNpcType() {
        return "custom_3.archer.elite";
    }

}
