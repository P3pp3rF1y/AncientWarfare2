package net.shadowmage.ancientwarfare.structure.template.datafixes.fixers;

import com.google.common.collect.ImmutableSet;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.shadowmage.ancientwarfare.structure.AncientWarfareStructure;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplate.Version;
import net.shadowmage.ancientwarfare.structure.template.datafixes.FixResult;

import java.util.Set;

import static net.shadowmage.ancientwarfare.structure.api.TemplateRule.JSON_PREFIX;

public class EntityPositionToNBTFixer extends RuleDataFixerBase {
	private static final Version VERSION = new Version(2, 6);

	private static final Set<String> APPLICABLE_TO_RULES = ImmutableSet.of(
			"vanillaEntities",
			"vanillaHangingEntity",
			"vanillaLogicEntity",
			"AWNpc",
			"awGate");

	private BlockPos position; // a little hacky to use this field between lines fixed but the lines are processed in order so it shouldn't break

	private static int[] parseIntArray(String csv) {
		String[] splits = csv.split(",");
		int[] array = new int[splits.length];
		for (int i = 0; i < splits.length; i++) {
			array[i] = Integer.parseInt(splits[i].trim());
		}
		return array;
	}

	/*
		 * splits test at regex, returns parsed int array from csv value of remaining string
		 * returns size 1 int array if no valid split is found
		 */
	private static int[] safeParseIntArray(String test) {
		String[] splits = test.split("=");
		if (splits.length > 1) {
			return parseIntArray(splits[1]);
		}
		return new int[0];
	}

	@Override
	public FixResult<String> fixData(String ruleName, String data) {
		if (data.contains("position=")) {
			int[] posParts = safeParseIntArray(data);
			position = new BlockPos(posParts[0], posParts[1], posParts[2]);
			return new FixResult.NotModified<>(data);
		} else if (data.startsWith(JSON_PREFIX)) {
			NBTTagCompound tag;
			try {
				tag = JsonToNBT.getTagFromJson(data.substring(JSON_PREFIX.length()));
			}
			catch (NBTException e) {
				AncientWarfareStructure.LOG.error("Error getting nbt from json string: ", e);
				return new FixResult.NotModified<>(data);
			}
			tag.setLong("position", position.toLong());
			return new FixResult.Modified<>(JSON_PREFIX + tag.toString(), "EntityPositionToNBTFixer");
		}
		return new FixResult.NotModified<>(data);
	}

	@Override
	public Version getVersion() {
		return VERSION;
	}

	@Override
	public boolean isForRule(String ruleName) {
		return APPLICABLE_TO_RULES.contains(ruleName);
	}
}
