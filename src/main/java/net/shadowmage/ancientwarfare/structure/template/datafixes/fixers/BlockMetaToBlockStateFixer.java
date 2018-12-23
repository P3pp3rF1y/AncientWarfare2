package net.shadowmage.ancientwarfare.structure.template.datafixes.fixers;

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.shadowmage.ancientwarfare.core.util.NBTHelper;
import net.shadowmage.ancientwarfare.structure.AncientWarfareStructure;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplate;
import net.shadowmage.ancientwarfare.structure.template.datafixes.FixResult;

import java.util.Map;

import static net.shadowmage.ancientwarfare.structure.api.TemplateRule.JSON_PREFIX;

public class BlockMetaToBlockStateFixer extends RuleDataFixerBase {
	private static final StructureTemplate.Version VERSION = new StructureTemplate.Version(2, 5);

	@SuppressWarnings("squid:S1192")
	private static final Map<String, String> ruleBlockNameMapping = new ImmutableMap.Builder<String, String>()
			.put("modBlockDefault", "blockName")
			.put("awTorqueMulti", "blockId")
			.put("rotatable", "blockId")
			.put("inventory", "blockName")
			.put("vanillaSign", "blockName")
			.put("vanillaSkull", "blockName")
			.put("blockTile", "blockName")
			.put("vanillaFlowerPot", "blockName")
			.put("doors", "blockName")
			.put("vanillaBlocks", "blockName")
			.build();

	@Override
	@SuppressWarnings("squid:CallToDeprecatedMethod")
	protected FixResult<String> fixData(String ruleName, String data) {
		NBTTagCompound tag;
		try {
			tag = JsonToNBT.getTagFromJson(data.substring(JSON_PREFIX.length()));
		}
		catch (NBTException e) {
			AncientWarfareStructure.LOG.error("Error getting nbt from json string: ", e);
			return new FixResult.NotModified<>(data);
		}

		String blockName = tag.getString(ruleBlockNameMapping.get(ruleName));
		Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(blockName));
		if (block == null) {
			AncientWarfareStructure.LOG.warn("block {} cannot be found in registry replacing with air", blockName);
			tag.setTag("blockState", NBTHelper.getBlockStateTag(Blocks.AIR.getDefaultState()));
		} else {
			//noinspection deprecation
			tag.setTag("blockState", NBTHelper.getBlockStateTag(block.getStateFromMeta(tag.getInteger("meta"))));
		}

		return new FixResult.Modified<>(JSON_PREFIX + tag.toString(), "BlockMetaToBlockStateFixer");
	}

	@Override
	public StructureTemplate.Version getVersion() {
		return VERSION;
	}

	@Override
	public boolean isForRule(String ruleName) {
		return ruleBlockNameMapping.containsKey(ruleName);
	}
}
