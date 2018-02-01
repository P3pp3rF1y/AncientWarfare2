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

package shadowmage.ancient_warfare.client.gui.vehicle;

import net.minecraft.inventory.Container;
import shadowmage.ancient_warfare.client.gui.GuiContainerAdvanced;
import shadowmage.ancient_warfare.client.gui.elements.IGuiElement;
import shadowmage.ancient_warfare.common.config.Config;
import shadowmage.ancient_warfare.common.utils.Trig;
import shadowmage.ancient_warfare.common.vehicles.VehicleBase;

public class GuiVehicleStats extends GuiContainerAdvanced {

	VehicleBase vehicle;

	/**
	 * @param container
	 */
	public GuiVehicleStats(Container container, VehicleBase vehicle) {
		super(container);
		this.vehicle = vehicle;
		this.shouldCloseOnVanillaKeys = true;
	}

	@Override
	public void onElementActivated(IGuiElement element) {
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
	public String getGuiBackGroundTexture() {
		return Config.texturePath + "gui/guiBackgroundLarge.png";
	}

	@Override
	public void renderExtraBackGround(int mouseX, int mouseY, float partialTime) {
		int color = 0xffffffff;
		this.drawStringGui("Vehicle Type: " + vehicle.vehicleType.getLocalizedName(), 10, 4, color);
		this.drawStringGui("Material Level: " + vehicle.vehicleMaterialLevel, 10, 14, color);
		this.drawStringGui("Health: " + vehicle.getHealth() + "/" + vehicle.baseHealth, 10, 24, color);
		this.drawStringGui("Weight: " + vehicle.currentWeight + "/" + vehicle.baseWeight, 10, 34, color);
		this.drawStringGui("Speed: " + (Trig.getVelocity(vehicle.motionX, vehicle.motionY, vehicle.motionZ) * 20) + "/" + (vehicle.currentForwardSpeedMax * 20),
				10, 44, color);
		this.drawStringGui("Missile Velocity: " + vehicle.localLaunchPower + "/" + vehicle.currentLaunchSpeedPowerMax, 10, 54, color);
		this.drawStringGui("Resists: F: " + vehicle.currentFireResist + " E: " + vehicle.currentExplosionResist + " G: " + vehicle.currentGenericResist, 10, 64,
				color);
		this.drawStringGui("Mountable: " + vehicle.isMountable(), 10, 74, color);
		this.drawStringGui("Drivable: " + vehicle.isDrivable(), 10, 84, color);
		this.drawStringGui("Combat Vehicle: " + vehicle.isAimable(), 10, 94, color);
		this.drawStringGui("Rider Sits: " + vehicle.shouldRiderSit(), 10, 104, color);
		this.drawStringGui("Rider On Turret: " + vehicle.vehicleType.moveRiderWithTurret(), 10, 114, color);
		this.drawStringGui("Adjustable Yaw: " + vehicle.canAimRotate(), 10, 124, color);
		this.drawStringGui("Adjustable Pitch: " + vehicle.canAimPitch(), 10, 134, color);
		this.drawStringGui("Adjustable Power: " + vehicle.canAimPower(), 10, 144, color);
		this.drawStringGui("Pitch Min: " + vehicle.currentTurretPitchMin, 10, 154, color);
		this.drawStringGui("Pitch Max: " + vehicle.currentTurretPitchMax, 10, 164, color);
		this.drawStringGui("Yaw Min: " + (vehicle.localTurretRotationHome - vehicle.currentTurretRotationMax), 10, 174, color);
		this.drawStringGui("Yaw Max: " + (vehicle.localTurretRotationHome + vehicle.currentTurretRotationMax), 10, 184, color);
		this.drawStringGui("", 10, 194, color);
		this.drawStringGui("TeamNum: " + vehicle.teamNum, 10, 204, color);
		this.drawStringGui("", 10, 214, color);
		this.drawStringGui("", 10, 224, color);
	}

	@Override
	public void updateScreenContents() {
	}

	@Override
	public void setupControls() {

	}

	@Override
	public void updateControls() {

	}

}
