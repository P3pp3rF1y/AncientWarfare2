package net.shadowmage.ancientwarfare.npc.entity.faction;

import net.minecraft.world.World;

public class NpcPirateBard extends NpcFactionBard {

    public NpcPirateBard(World par1World) {
        super(par1World);
    }

    @Override
    public String getNpcType() {
        return "pirate.bard";
    }

}
