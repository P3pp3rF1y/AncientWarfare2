package net.shadowmage.ancientwarfare.structure.template.datafixes.fixers;

import com.google.common.collect.ImmutableSet;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import net.shadowmage.ancientwarfare.structure.AncientWarfareStructure;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplate;
import net.shadowmage.ancientwarfare.structure.template.datafixes.FixResult;

import java.util.Set;

import static net.shadowmage.ancientwarfare.structure.api.TemplateRule.JSON_PREFIX;

public class EntityEquipmentFixer extends RuleDataFixerBase {
	private static final StructureTemplate.Version VERSION = new StructureTemplate.Version(2, 8);

	private static final Set<String> APPLICABLE_TO_RULES = ImmutableSet.of("AWNpc", "vanillaLogicEntity");

	@Override
	public StructureTemplate.Version getVersion() {
		return VERSION;
	}

	@Override
	public boolean isForRule(String ruleName) {
		return APPLICABLE_TO_RULES.contains(ruleName);
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

		if (!tag.hasKey("equipmentData")) {
			return new FixResult.NotModified<>(data);
		}

		NBTTagList equipmentList = tag.getCompoundTag("equipmentData").getTagList("equipmentContents", Constants.NBT.TAG_COMPOUND);

		NBTTagList handItems = new NBTTagList();
		NBTTagList armorItems = new NBTTagList();
		for (int i = 0; i < equipmentList.tagCount(); i++) {
			NBTTagCompound stack = equipmentList.getCompoundTagAt(i);
			stack.removeTag("slot");
			if (i < 2) {
				handItems.appendTag(stack);
			} else {
				armorItems.appendTag(stack);
			}
		}
		NBTTagCompound entityData = tag.getCompoundTag("entityData");
		entityData.setTag("HandItems", handItems);
		entityData.setTag("ArmorItems", armorItems);
		tag.setTag("entityData", entityData);

		return new FixResult.Modified<>(JSON_PREFIX + tag.toString(), "EntityEquipmentFixer");
	}
}
