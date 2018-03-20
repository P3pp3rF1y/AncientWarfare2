/**
 * Copyright 2012 John Cummens (aka Shadowmage, Shadowmage4513)
 * This software is distributed under the terms of the GNU General Public License.
 * Please see COPYING for precise license information.
 * <p>
 * This file is part of Ancient Warfare.
 * <p>
 * Ancient Warfare is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * Ancient Warfare is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with Ancient Warfare.  If not, see <http://www.gnu.org/licenses/>.
 */

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

public class RenderOverlay extends Gui {

	public void renderOverlay() {

	}

	public void renderVehicleOverlay() {
		Minecraft mc = Minecraft.getMinecraft();
		FontRenderer fontRenderer = mc.fontRenderer;

		VehicleBase vehicle = (VehicleBase) mc.player.getRidingEntity();
		int white = 0xffffffff;
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
			this.drawString(fontRenderer, "Ammo: " + I18n.format(AmmoRegistry.getItemForAmmo(ammo).getUnlocalizedName()), 10, 50, white);
			this.drawString(fontRenderer, "Count: " + count, 10, 60, white);
		} else {
			this.drawString(fontRenderer, "No Ammo Selected", 10, 50, white);
		}
		if (AWVehicleStatics.renderAdvOverlay) {
			float velocity = Trig.getVelocity(vehicle.motionX, 0, vehicle.motionZ);
			this.drawString(fontRenderer, "Velocity: " + velocity * 20.f + "m/s  max: " + vehicle.currentForwardSpeedMax * 20, 10, 70, white);
			this.drawString(fontRenderer, "Yaw Rate: " + vehicle.moveHelper.getRotationSpeed() * 20.f, 10, 80, white);
		}
	}

	@SubscribeEvent
	public void tickEnd(TickEvent.RenderTickEvent event) {
		Minecraft mc = Minecraft.getMinecraft();
		if (event.phase == TickEvent.Phase.END && AWVehicleStatics.renderOverlay && mc.currentScreen == null && mc.player != null) {
			if (mc.player.getRidingEntity() instanceof VehicleBase) {
				this.renderVehicleOverlay();
			}
		}
	}
}
