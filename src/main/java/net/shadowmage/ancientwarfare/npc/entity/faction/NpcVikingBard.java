package net.shadowmage.ancientwarfare.npc.entity.faction;

import net.minecraft.world.World;

public class NpcVikingBard extends NpcFactionBard {

    public NpcVikingBard(World par1World) {
        super(par1World);
    }

    @Override
    public String getNpcType() {
        return "viking.bard";
    }

}
