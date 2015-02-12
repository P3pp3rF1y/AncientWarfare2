package net.shadowmage.ancientwarfare.npc.entity.faction;

import net.minecraft.world.World;

public class NpcCustom_1Archer extends NpcFactionArcher {

    public NpcCustom_1Archer(World par1World) {
        super(par1World);
    }

    @Override
    public String getNpcType() {
        return "custom_1.archer";
    }

}
