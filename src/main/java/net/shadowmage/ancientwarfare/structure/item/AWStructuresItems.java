package net.shadowmage.ancientwarfare.structure.item;

import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.shadowmage.ancientwarfare.structure.AncientWarfareStructures;

@ObjectHolder(AncientWarfareStructures.MOD_ID)
public class AWStructuresItems {

	private AWStructuresItems() {}

	@SuppressWarnings("squid:S1444")
	@ObjectHolder("structure_scanner")
	public static Item structureScanner;

	@SuppressWarnings("squid:S1444")
	@ObjectHolder("loot_chest_placer")
	public static Item lootChestPlacer;
}
