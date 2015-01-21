package net.shadowmage.ancientwarfare.npc.entity.faction;

import net.minecraft.world.World;

public class NpcCustom_1CivilianFemale extends NpcFactionCivilian {

    public NpcCustom_1CivilianFemale(World par1World) {
        super(par1World);
    }

    @Override
    public String getNpcType() {
        return "custom_1.civilian.female";
    }

}
