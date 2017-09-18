package net.shadowmage.ancientwarfare.core.block;

import net.minecraft.block.material.Material;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;

public abstract class BlockBaseCore extends BlockBase {
    public BlockBaseCore(Material material, String regName) {
        super(material, AncientWarfareCore.modID, regName);
        setCreativeTab(AWCoreBlockLoader.coreTab);

    }
}
