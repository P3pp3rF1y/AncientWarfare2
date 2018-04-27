package net.shadowmage.ancientwarfare.automation.compat.agricraft;

import net.shadowmage.ancientwarfare.automation.compat.ICompat;
import net.shadowmage.ancientwarfare.automation.tile.worksite.cropfarm.HarvestableFactory;

public class AgricraftCompat implements ICompat {
	@Override
	public String getModId() {
		return "agricraft";
	}

	@Override
	public void init() {
		HarvestableFactory.registerHarvestable(new HarvestableAgricraftCrop());
	}
}
