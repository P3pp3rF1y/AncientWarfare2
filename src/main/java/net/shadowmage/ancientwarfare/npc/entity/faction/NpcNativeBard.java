package net.shadowmage.ancientwarfare.npc.entity.faction;

import net.minecraft.world.World;

public class NpcNativeBard extends NpcFactionBard {

    public NpcNativeBard(World par1World) {
        super(par1World);
    }

    @Override
    public String getNpcType() {
        return "native.bard";
    }

}
