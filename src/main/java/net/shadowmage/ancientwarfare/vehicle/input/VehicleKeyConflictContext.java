package net.shadowmage.ancientwarfare.vehicle.input;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.settings.IKeyConflictContext;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleBase;

public class VehicleKeyConflictContext implements IKeyConflictContext {
	public static final VehicleKeyConflictContext INSTANCE = new VehicleKeyConflictContext();

	private VehicleKeyConflictContext() {
	}

	@Override
	public boolean isActive() {
		Minecraft mc = Minecraft.getMinecraft();
		return mc.currentScreen == null && mc.player != null && mc.world != null && mc.player.getRidingEntity() instanceof VehicleBase;
	}

	@Override
	public boolean conflicts(IKeyConflictContext other) {
		return this == other;
	}
}
