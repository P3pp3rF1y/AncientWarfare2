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

package net.shadowmage.ancientwarfare.vehicle.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.Label;
import net.shadowmage.ancientwarfare.core.util.Trig;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleBase;

public class GuiVehicleStats extends GuiContainerBase<GuiVehicleStats.DummyContainer> {
	private VehicleBase vehicle;
	private GuiVehicleInventory parent;

	private static final DummyContainer DUMMY_CONTAINER = new DummyContainer(Minecraft.getMinecraft().player);

	public GuiVehicleStats(GuiVehicleInventory parent, VehicleBase vehicle) {
		super(DUMMY_CONTAINER);
		this.vehicle = vehicle;
		this.parent = parent;
		this.shouldCloseOnVanillaKeys = true;
	}

	@Override
	public int getXSize() {
		return 256;
	}

	@Override
	public int getYSize() {
		return 240;
	}

	@Override
	public void initElements() {
		//TODO lang translations
		addGuiElement(new Label(10, 4, "Vehicle Type: " + vehicle.vehicleType.getLocalizedName()));
		addGuiElement(new Label(10, 14, "Material Level: " + vehicle.vehicleMaterialLevel));
		addGuiElement(new Label(10, 24, "Health: " + vehicle.getHealth() + "/" + vehicle.baseHealth));
		addGuiElement(new Label(10, 34, "Weight: " + vehicle.currentWeight + "/" + vehicle.baseWeight));
		addGuiElement(new Label(10, 44, "Speed: " + (Trig.getVelocity(vehicle.motionX, vehicle.motionY, vehicle.motionZ) * 20) + "/" + (vehicle.currentForwardSpeedMax * 20)));
		addGuiElement(new Label(10, 54, "Missile Velocity: " + vehicle.localLaunchPower + "/" + vehicle.currentLaunchSpeedPowerMax));
		addGuiElement(new Label(10, 64, "Resists: F: " + vehicle.currentFireResist + " E: " + vehicle.currentExplosionResist + " G: " + vehicle.currentGenericResist));
		addGuiElement(new Label(10, 74, "Mountable: " + vehicle.isMountable()));
		addGuiElement(new Label(10, 84, "Drivable: " + vehicle.isDrivable()));
		addGuiElement(new Label(10, 94, "Combat Vehicle: " + vehicle.isAimable()));
		addGuiElement(new Label(10, 104, "Rider Sits: " + vehicle.shouldRiderSit()));
		addGuiElement(new Label(10, 114, "Rider On Turret: " + vehicle.vehicleType.moveRiderWithTurret()));
		addGuiElement(new Label(10, 124, "Adjustable Yaw: " + vehicle.canAimRotate()));
		addGuiElement(new Label(10, 134, "Adjustable Pitch: " + vehicle.canAimPitch()));
		addGuiElement(new Label(10, 144, "Adjustable Power: " + vehicle.canAimPower()));
		addGuiElement(new Label(10, 154, "Pitch Min: " + vehicle.currentTurretPitchMin));
		addGuiElement(new Label(10, 164, "Pitch Max: " + vehicle.currentTurretPitchMax));
		addGuiElement(new Label(10, 174, "Yaw Min: " + (vehicle.localTurretRotationHome - vehicle.currentTurretRotationMax)));
		addGuiElement(new Label(10, 184, "Yaw Max: " + (vehicle.localTurretRotationHome + vehicle.currentTurretRotationMax)));
		//TODO are empty labels required here? try removing and see if it still works correctly
		addGuiElement(new Label(10, 194, ""));
		addGuiElement(new Label(10, 204, "TeamNum: " + (vehicle.getTeam() == null ? "" : vehicle.getTeam().getName())));
		addGuiElement(new Label(10, 214, ""));
		addGuiElement(new Label(10, 224, ""));
	}

	@Override
	public void setupElements() {

	}

	public static class DummyContainer extends ContainerBase {
		public DummyContainer(EntityPlayer player) {
			super(player);
		}
	}

	@Override
	protected boolean onGuiCloseRequested() {
		Minecraft.getMinecraft().displayGuiScreen(parent);
		return false;
	}
}
