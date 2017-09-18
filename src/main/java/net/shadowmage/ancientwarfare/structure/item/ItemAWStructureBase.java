package net.shadowmage.ancientwarfare.structure.item;

import net.shadowmage.ancientwarfare.core.item.ItemAWBase;
import net.shadowmage.ancientwarfare.structure.AncientWarfareStructures;

public class ItemAWStructureBase extends ItemAWBase {
	public ItemAWStructureBase(String regName) {
		super(AncientWarfareStructures.modID, regName);
		setCreativeTab(AWStructuresItemLoader.structureTab);
	}
}
