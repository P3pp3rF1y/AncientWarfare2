package net.shadowmage.ancientwarfare.structure.datafixes;

import com.google.common.collect.ImmutableSet;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.util.datafix.IFixableData;
import net.minecraftforge.common.util.Constants;
import net.shadowmage.ancientwarfare.structure.AncientWarfareStructure;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplate;
import net.shadowmage.ancientwarfare.structure.template.datafixes.FixResult;
import net.shadowmage.ancientwarfare.structure.template.datafixes.fixers.RuleDataFixerBase;

import static net.shadowmage.ancientwarfare.structure.api.TemplateRule.JSON_PREFIX;

public class LootSettingsPotionRegistryNameFixer extends RuleDataFixerBase implements IFixableData {
	private static final String TE_DATA_TAG = "teData";

	private static final ImmutableSet LOOT_TILES = ImmutableSet.of(
			"ancientwarfarestructure:loot_basket",
			"ancientwarfarestructure:advanced_loot_chest_tile",
			"ancientwarfarestructure:coffin",
			"ancientwarfarestructure:urn_tile"
	);

	@Override
	public int getFixVersion() {
		return 10;
	}

	@Override
	public NBTTagCompound fixTagCompound(NBTTagCompound compound) {
		String id = compound.getString("id");

		if (LOOT_TILES.contains(id)) {
			NBTTagCompound lootNbt = compound.getCompoundTag("lootSettings");
			NBTTagList effectList = lootNbt.getTagList("effects", Constants.NBT.TAG_COMPOUND);
			for (int i = 0; i < effectList.tagCount(); i++) {
				NBTTagCompound effectNbt = effectList.getCompoundTagAt(i);
				int effectId = effectNbt.getByte("Id") & 0xFF;
				Potion potion = Potion.getPotionById(effectId);
				//noinspection ConstantConditions
				effectNbt.setString("RegistryName", potion.getRegistryName().toString());
				effectNbt.removeTag("Id");
			}
		}

		return compound;
	}

	private static final StructureTemplate.Version VERSION = new StructureTemplate.Version(2, 10);

	@Override
	public StructureTemplate.Version getVersion() {
		return VERSION;
	}

	@Override
	public boolean isForRule(String ruleName) {
		return ruleName.equals("blockTile") || ruleName.equals("inventory") || ruleName.equals("coffin");
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
