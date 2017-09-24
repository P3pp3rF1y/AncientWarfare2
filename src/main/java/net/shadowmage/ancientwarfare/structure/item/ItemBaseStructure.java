package net.shadowmage.ancientwarfare.structure.item;

import net.shadowmage.ancientwarfare.core.item.ItemBase;
import net.shadowmage.ancientwarfare.structure.AncientWarfareStructures;

public class ItemBaseStructure extends ItemBase {
	public ItemBaseStructure(String regName) {
		super(AncientWarfareStructures.modID, regName);
		setCreativeTab(AWStructuresItemLoader.structureTab);
	}
}
