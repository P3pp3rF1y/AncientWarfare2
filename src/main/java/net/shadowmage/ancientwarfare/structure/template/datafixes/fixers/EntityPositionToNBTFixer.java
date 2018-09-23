package net.shadowmage.ancientwarfare.structure.template.datafixes.fixers;

import com.google.common.collect.ImmutableSet;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.shadowmage.ancientwarfare.structure.AncientWarfareStructure;
import net.shadowmage.ancientwarfare.structure.api.NBTTools;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplate.Version;
import net.shadowmage.ancientwarfare.structure.template.datafixes.FixResult;
import net.shadowmage.ancientwarfare.structure.template.datafixes.IDataFixer;

import java.util.Set;

import static net.shadowmage.ancientwarfare.structure.api.TemplateRule.JSON_PREFIX;

public class EntityPositionToNBTFixer implements IDataFixer {
	private static final Version VERSION = new Version(2, 6);

	private static final Set<String> APPLICABLE_TO_RULES = ImmutableSet.of(
			"vanillaEntities",
			"vanillaHangingEntity",
			"vanillaLogicEntity",
			"AWNpc",
			"awGate");

	private BlockPos position; // a little hacky to use this field between lines fixed but the lines are processed in order so it shouldn't break

	@Override
	public FixResult<String> fix(String ruleName, String data) {
		if (data.contains("position=")) {
			int[] posParts = NBTTools.safeParseIntArray("=", data);
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
