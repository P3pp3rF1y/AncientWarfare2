package net.shadowmage.ancientwarfare.vehicle.input;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.settings.IKeyConflictContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleBase;

@SideOnly(Side.CLIENT)
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
