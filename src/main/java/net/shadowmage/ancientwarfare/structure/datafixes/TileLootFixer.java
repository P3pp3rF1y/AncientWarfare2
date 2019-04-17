package net.shadowmage.ancientwarfare.structure.datafixes;

import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.datafix.IFixableData;
import net.shadowmage.ancientwarfare.structure.AncientWarfareStructure;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplate;
import net.shadowmage.ancientwarfare.structure.template.datafixes.FixResult;
import net.shadowmage.ancientwarfare.structure.template.datafixes.fixers.RuleDataFixerBase;
import net.shadowmage.ancientwarfare.structure.tile.LootSettings;

import static net.shadowmage.ancientwarfare.structure.api.TemplateRule.JSON_PREFIX;

public class TileLootFixer extends RuleDataFixerBase implements IFixableData {
	private static final String TE_DATA_TAG = "teData";

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

	private static final StructureTemplate.Version VERSION = new StructureTemplate.Version(2, 9);

	@Override
	public StructureTemplate.Version getVersion() {
		return VERSION;
	}

	@Override
	public boolean isForRule(String ruleName) {
		return ruleName.equals("blockTile");
	}

	@Override
	protected FixResult<String> fixData(String ruleName, String data) {
		NBTTagCompound tag;
		try {
			tag = JsonToNBT.getTagFromJson(data.substring(JSON_PREFIX.length()));
		}
		catch (NBTException e) {
			AncientWarfareStructure.LOG.error("Error getting nbt from json string: ", e);
			return new FixResult.NotModified<>(data);
		}

		if (tag.hasKey(TE_DATA_TAG)) {
			tag.setTag(TE_DATA_TAG, fixTagCompound(tag.getCompoundTag(TE_DATA_TAG)));
			return new FixResult.Modified<>(JSON_PREFIX + tag.toString(), "TileLootFixer");
		}
		return new FixResult.NotModified<>(data);
	}
}
