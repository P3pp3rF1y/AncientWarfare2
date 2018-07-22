package net.shadowmage.ancientwarfare.core.block;

import net.minecraft.block.material.Material;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;

public abstract class BlockBaseCore extends BlockBase {
	public BlockBaseCore(Material material, String regName) {
		super(material, AncientWarfareCore.MOD_ID, regName);
		setCreativeTab(AWCoreBlockLoader.coreTab);

	}
}
