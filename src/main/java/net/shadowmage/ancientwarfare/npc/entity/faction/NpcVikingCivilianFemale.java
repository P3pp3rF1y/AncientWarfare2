package net.shadowmage.ancientwarfare.npc.entity.faction;

import net.minecraft.world.World;

public class NpcVikingCivilianFemale extends NpcFactionCivilian {

    public NpcVikingCivilianFemale(World par1World) {
        super(par1World);
    }

    @Override
    public String getNpcType() {
        return "viking.civilian.female";
    }

}
