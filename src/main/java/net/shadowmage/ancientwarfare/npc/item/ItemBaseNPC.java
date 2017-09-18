package net.shadowmage.ancientwarfare.npc.item;

import net.shadowmage.ancientwarfare.core.item.ItemBase;
import net.shadowmage.ancientwarfare.npc.AncientWarfareNPC;

public abstract class ItemBaseNPC extends ItemBase {
    public ItemBaseNPC(String regName) {
        super(AncientWarfareNPC.modID, regName);
        setCreativeTab(AWNPCItemLoader.npcTab);
    }
}
