package net.shadowmage.ancientwarfare.automation.tile.worksite.cropfarm;

import net.minecraft.block.state.IBlockState;

import java.util.ArrayList;
import java.util.List;

public class HarvestableFactory {
	private static final IHarvestable DEFAULT_HARVESTABLE = new HarvestableDefault();
	private static List<IHarvestable> harvestables = new ArrayList<>();

	static {
		registerHarvestable(new HarvestableDefault());
		registerHarvestable(new HarvestableGourd());
	}

	public static void registerHarvestable(IHarvestable harvestable) {
		//adding to start of the list so that the last registered is always the first one processed, allowing for compatibility harvestables to be processed before default one
		harvestables.add(0, harvestable);
	}

	public static IHarvestable getHarvestable(IBlockState state) {
		return harvestables.stream().filter(h -> h.matches(state)).findFirst().orElse(DEFAULT_HARVESTABLE);
	}
}
