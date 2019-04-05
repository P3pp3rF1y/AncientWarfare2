package net.shadowmage.ancientwarfare.structure.tile;

import net.minecraft.util.ResourceLocation;

public interface IUpdatableLootContainer {
	void setLootTable(ResourceLocation lootTable, long lootTableSeed);

	void setLootRolls(int lootRolls);
}
