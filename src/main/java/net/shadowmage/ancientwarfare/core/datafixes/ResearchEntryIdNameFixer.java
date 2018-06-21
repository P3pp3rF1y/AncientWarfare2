package net.shadowmage.ancientwarfare.core.datafixes;

import com.google.common.collect.ImmutableMap;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.common.util.Constants;
import net.shadowmage.ancientwarfare.core.util.StreamUtils;

import java.util.Arrays;
import java.util.Map;

public class ResearchEntryIdNameFixer {

	private ResearchEntryIdNameFixer() {}

	private static final Map<Integer, String> research = new ImmutableMap.Builder<Integer, String>()
			.put(0, "invention")
			.put(1, "agriculture")
			.put(2, "leadership")
			.put(3, "engineering")
			.put(4, "farming")
			.put(5, "animal_husbandry")
			.put(6, "fishing")
			.put(7, "conscription")
			.put(8, "command")
			.put(9, "mining")
			.put(10, "the_wheel")
			.put(11, "construction")
			.put(12, "mathematics")
			.put(13, "trade")
			.put(14, "tactics")
			.put(15, "combustion")
			.put(16, "chemistry")
			.put(17, "theory_of_gravity")
			.put(18, "seafaring")
			.put(19, "explosives")
			.put(20, "refining")
			.put(21, "machinery")
			.put(22, "navigation")
			.put(23, "siege_warfare")
			.put(24, "gunpowder")
			.put(25, "mass_production")
			.put(26, "flight")
			.put(27, "naval_warfare")
			.put(28, "advanced_siege_warfare")
			.put(29, "ballistics")
			.put(30, "rocketry")
			.build();

	public static NBTTagCompound fix(NBTTagCompound tag) {
		if (tag.hasKey("currentResearch", Constants.NBT.TAG_INT)) {
			String researchName = research.get(tag.getInteger("currentResearch"));
			if (researchName != null) {
				tag.setString("currentResearch", researchName);
			} else {
				tag.removeTag("currentResearch");
			}
		}
		if (tag.hasKey("completedResearch", Constants.NBT.TAG_INT_ARRAY)) {
			tag.setTag("completedResearch", fixArray(tag.getIntArray("completedResearch")));
		}
		if (tag.hasKey("queuedResearch", Constants.NBT.TAG_INT_ARRAY)) {
			tag.setTag("queuedResearch", fixArray(tag.getIntArray("queuedResearch")));
		}
		return tag;
	}

	private static NBTTagList fixArray(int[] researches) {
		return Arrays.stream(researches).mapToObj(i -> new NBTTagString(research.get(i))).collect(StreamUtils.toNBTTagList);
	}
}
