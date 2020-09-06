package net.shadowmage.ancientwarfare.structure.template.datafixes.fixers;

import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.structure.AncientWarfareStructure;
import net.shadowmage.ancientwarfare.structure.template.datafixes.FixResult;

import static net.shadowmage.ancientwarfare.structure.api.TemplateRule.JSON_PREFIX;

public abstract class TileRuleDataFixer extends RuleDataFixerBase {
	private static final String TE_DATA_TAG = "teData";

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

		return fixJSONData(data, tag);
	}

	protected FixResult<String> fixJSONData(String data, NBTTagCompound tag) {
		if (tag.hasKey(TE_DATA_TAG)) {
			tag.setTag(TE_DATA_TAG, fixRuleCompoundTag(tag.getCompoundTag(TE_DATA_TAG)));
			return new FixResult.Modified<>(JSON_PREFIX + tag.toString(), getFixerName());
		}
		return new FixResult.NotModified<>(data);
	}

	protected abstract String getFixerName();

	protected abstract NBTTagCompound fixRuleCompoundTag(NBTTagCompound compoundTag);
}
