package net.shadowmage.ancientwarfare.structure.datafixes;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.datafix.IFixableData;
import net.shadowmage.ancientwarfare.structure.tile.LootSettings;

public class TileLootFixer implements IFixableData {
	@Override
	public int getFixVersion() {
		return 8;
	}

	@Override
	public NBTTagCompound fixTagCompound(NBTTagCompound compound) {
		String id = compound.getString("id");

		if (id.equals("ancientwarfarestructure:loot_basket") || id.equals("ancientwarfarestructure:advanced_loot_chest_tile")) {
			String lootTableName = compound.getString("LootTable");
			int lootRolls = compound.getInteger("lootRolls");

			LootSettings lootSettings = new LootSettings();
			lootSettings.setHasLoot(true);
			lootSettings.setLootRolls(lootRolls);
			lootSettings.setLootTableName(new ResourceLocation(lootTableName));

			compound.setTag("lootSettings", lootSettings.serializeNBT());
		}
		return compound;
	}
}
