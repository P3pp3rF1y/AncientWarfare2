package net.shadowmage.ancientwarfare.structure.template.datafixes.fixers;

import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplate;

public class SoundBlockFixer extends TileRuleDataFixer {
	private static final String NAME = "SoundBlockFixer";
	private static final StructureTemplate.Version VERSION = new StructureTemplate.Version(2, 12);

	@Override
	protected String getFixerName() {
		return NAME;
	}

	@Override
	protected NBTTagCompound fixRuleCompoundTag(NBTTagCompound compoundTag) {
		String id = compoundTag.getString("id");
		if (!id.equals("ancientwarfarestructure:sound_block_tile")) {
			return compoundTag;
		}

		compoundTag.removeTag("playerSpecificValues");

		return compoundTag;
	}

	@Override
	public StructureTemplate.Version getVersion() {
		return VERSION;
	}

	@Override
	public boolean isForRule(String ruleName) {
		return ruleName.equals("blockTile");
	}
}
