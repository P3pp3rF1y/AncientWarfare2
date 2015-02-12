package net.shadowmage.ancientwarfare.npc.entity.faction;

import net.minecraft.world.World;

public class NpcCustom_3Priest extends NpcFactionPriest {

    public NpcCustom_3Priest(World par1World) {
        super(par1World);
    }

    @Override
    public String getNpcType() {
        return "custom_3.priest";
    }

}
