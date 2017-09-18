package net.shadowmage.ancientwarfare.core.item;

import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.core.block.AWCoreBlockLoader;

public abstract class ItemBaseCore extends ItemBase {
    public ItemBaseCore(String regName) {
        super(AncientWarfareCore.modID, regName);
        setCreativeTab(AWCoreBlockLoader.coreTab);
    }
}
