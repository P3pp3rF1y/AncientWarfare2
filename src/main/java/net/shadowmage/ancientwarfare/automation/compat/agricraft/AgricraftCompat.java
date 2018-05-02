package net.shadowmage.ancientwarfare.automation.compat.agricraft;

import net.shadowmage.ancientwarfare.automation.compat.ICompat;
import net.shadowmage.ancientwarfare.automation.registry.CropFarmRegistry;

public class AgricraftCompat implements ICompat {
	@Override
	public String getModId() {
		return "agricraft";
	}

	@Override
	public void init() {
		CropFarmRegistry.registerHarvestable(new HarvestableAgricraftCrop());
	}
}
