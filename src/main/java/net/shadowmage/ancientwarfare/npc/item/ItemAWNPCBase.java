package net.shadowmage.ancientwarfare.npc.item;

import net.shadowmage.ancientwarfare.core.item.ItemAWBase;
import net.shadowmage.ancientwarfare.npc.AncientWarfareNPC;

public abstract class ItemAWNPCBase extends ItemAWBase {
    public ItemAWNPCBase(String regName) {
        super(AncientWarfareNPC.modID, regName);
        setCreativeTab(AWNPCItemLoader.npcTab);
    }
}
