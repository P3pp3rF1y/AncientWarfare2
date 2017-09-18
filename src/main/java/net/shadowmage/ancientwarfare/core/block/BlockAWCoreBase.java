package net.shadowmage.ancientwarfare.core.block;

import net.minecraft.block.material.Material;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;

public abstract class BlockAWCoreBase extends BlockAWBase {
    public BlockAWCoreBase(Material material, String regName) {
        super(material, AncientWarfareCore.modID, regName);
        setCreativeTab(AWCoreBlockLoader.coreTab);

    }
}
