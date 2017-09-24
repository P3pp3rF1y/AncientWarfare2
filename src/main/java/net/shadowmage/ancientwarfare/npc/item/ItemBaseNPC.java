package net.shadowmage.ancientwarfare.npc.item;

import net.shadowmage.ancientwarfare.core.item.ItemBase;
import net.shadowmage.ancientwarfare.core.proxy.IClientRegistrar;
import net.shadowmage.ancientwarfare.core.util.ModelLoaderHelper;
import net.shadowmage.ancientwarfare.npc.AncientWarfareNPC;

public abstract class ItemBaseNPC extends ItemBase implements IClientRegistrar {
    public ItemBaseNPC(String regName) {
        super(AncientWarfareNPC.modID, regName);
        setCreativeTab(AWNPCItemLoader.npcTab);

        AncientWarfareNPC.proxy.addClientRegistrar(this);
    }

    @Override
    public void registerClient() {
        ModelLoaderHelper.registerItem(this, "npc");
    }
}
