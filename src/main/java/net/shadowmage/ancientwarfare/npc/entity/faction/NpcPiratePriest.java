package net.shadowmage.ancientwarfare.npc.entity.faction;

import net.minecraft.world.World;

public class NpcPiratePriest extends NpcFactionPriest {

    public NpcPiratePriest(World par1World) {
        super(par1World);
    }

    @Override
    public String getNpcType() {
        return "pirate.priest";
    }

}
