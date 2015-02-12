package net.shadowmage.ancientwarfare.npc.entity.faction;

import net.minecraft.world.World;

public class NpcNativeCivilianFemale extends NpcFactionCivilian {

    public NpcNativeCivilianFemale(World par1World) {
        super(par1World);
    }

    @Override
    public String getNpcType() {
        return "native.civilian.female";
    }

}
