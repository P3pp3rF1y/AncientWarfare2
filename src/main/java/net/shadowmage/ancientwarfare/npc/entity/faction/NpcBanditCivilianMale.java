package net.shadowmage.ancientwarfare.npc.entity.faction;

import net.minecraft.world.World;

public class NpcBanditCivilianMale extends NpcFactionCivilian {

    public NpcBanditCivilianMale(World par1World) {
        super(par1World);
    }

    @Override
    public String getNpcType() {
        return "bandit.civilian.male";
    }
}
