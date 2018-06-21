package net.shadowmage.ancientwarfare.core.datafixes;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.datafix.IFixableData;

public class ResearchNoteFixer implements IFixableData {
	private static final String RESEARCH_PREFIX = "research.";
	private static final String RESEARCH_NAME_TAG = "researchName";

	@Override
	public int getFixVersion() {
		return 4;
	}

	@Override
	public NBTTagCompound fixTagCompound(NBTTagCompound compound) {
		String id = compound.getString("id");
		if (id.equals("ancientwarfare:research_note")) {
			NBTTagCompound tag = (NBTTagCompound) compound.getTag("tag");
			if (tag.getString(RESEARCH_NAME_TAG).startsWith(RESEARCH_PREFIX)) {
				tag.setString(RESEARCH_NAME_TAG, tag.getString(RESEARCH_NAME_TAG).substring(RESEARCH_PREFIX.length()));
			}
		}
		return compound;
	}
}
