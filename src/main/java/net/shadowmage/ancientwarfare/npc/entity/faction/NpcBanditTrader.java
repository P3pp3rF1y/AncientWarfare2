package net.shadowmage.ancientwarfare.npc.entity.faction;

import net.minecraft.world.World;

public class NpcBanditTrader extends NpcFactionTrader {

    public NpcBanditTrader(World par1World) {
        super(par1World);
    }

    @Override
    public String getNpcType() {
        return "bandit.trader";
    }


}
