package net.shadowmage.ancientwarfare.automation.compat.agricraft;

import net.shadowmage.ancientwarfare.automation.registry.CropFarmRegistry;
import net.shadowmage.ancientwarfare.core.compat.ICompat;

public class AgricraftCompat implements ICompat {
	@Override
	public String getModId() {
		return "agricraft";
	}

	@Override
	public void init() {
		CropFarmRegistry.registerCrop(new CropAgricraftCrop());
	}
}
