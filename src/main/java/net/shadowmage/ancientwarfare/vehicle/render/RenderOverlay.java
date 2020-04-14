package net.shadowmage.ancientwarfare.vehicle.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.shadowmage.ancientwarfare.core.util.Trig;
import net.shadowmage.ancientwarfare.vehicle.config.AWVehicleStatics;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleBase;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleMovementType;
import net.shadowmage.ancientwarfare.vehicle.missiles.IAmmo;
import net.shadowmage.ancientwarfare.vehicle.registry.AmmoRegistry;

import java.awt.*;

public class RenderOverlay extends Gui {
	private void renderVehicleOverlay() {
		Minecraft mc = Minecraft.getMinecraft();
		FontRenderer fontRenderer = mc.fontRenderer;

		VehicleBase vehicle = (VehicleBase) mc.player.getRidingEntity();
		int white = Color.WHITE.getRGB();
		int red = Color.RED.getRGB();
		//noinspection ConstantConditions
		if (vehicle.vehicleType.getMovementType() == VehicleMovementType.AIR1 || vehicle.vehicleType.getMovementType() == VehicleMovementType.AIR2) {
			this.drawString(fontRenderer, "Throttle: " + vehicle.moveHelper.throttle, 10, 10, white);
			this.drawString(fontRenderer, "Pitch: " + vehicle.rotationPitch, 10, 20, white);
			this.drawString(fontRenderer, "Climb Rate: " + vehicle.motionY * 20, 10, 30, white);
			this.drawString(fontRenderer, "Elevation: " + vehicle.posY, 10, 40, white);
		} else {
			this.drawString(fontRenderer, "Range: " + vehicle.firingHelper.clientHitRange, 10, 10, white);
			this.drawString(fontRenderer, "Pitch: " + vehicle.firingHelper.clientTurretPitch, 10, 20, white);
			this.drawString(fontRenderer, "Yaw: " + vehicle.firingHelper.clientTurretYaw, 10, 30, white);
			this.drawString(fontRenderer, "Velocity: " + vehicle.firingHelper.clientLaunchSpeed, 10, 40, white);
		}
		IAmmo ammo = vehicle.ammoHelper.getCurrentAmmoType();
		if (ammo != null) {
			int count = vehicle.ammoHelper.getCurrentAmmoCount();
			this.drawString(fontRenderer, "Ammo: " + I18n.format(AmmoRegistry.getItemForAmmo(ammo).getUnlocalizedName() + ".name"), 10, 50,
					count > 0 ? white : red);
			this.drawString(fontRenderer, "Count: " + count, 10, 60, count > 0 ? white : red);
		} else {
			this.drawString(fontRenderer, "No Ammo Selected", 10, 50, red);
		}
		if (AWVehicleStatics.clientSettings.renderAdvOverlay) {
			float velocity = Trig.getVelocity(vehicle.motionX, 0, vehicle.motionZ);
			this.drawString(fontRenderer, "Velocity: " + velocity * 20.f + "m/s  max: " + vehicle.currentForwardSpeedMax * 20, 10, 70, white);
			this.drawString(fontRenderer, "Yaw Rate: " + vehicle.moveHelper.getRotationSpeed() * 20.f, 10, 80, white);
		}
	}

	@SubscribeEvent
	public void tickEnd(TickEvent.RenderTickEvent event) {
		if (!AWVehicleStatics.clientSettings.renderOverlay) {
			return;
		}
		Minecraft mc = Minecraft.getMinecraft();
		if (event.phase == TickEvent.Phase.END && mc.currentScreen == null && mc.player != null && mc.player.getRidingEntity() instanceof VehicleBase) {
			this.renderVehicleOverlay();
		}
	}
}
