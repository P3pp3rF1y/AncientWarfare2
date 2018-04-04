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

package net.shadowmage.ancientwarfare.vehicle.VehicleVarHelpers;

import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleBase;
import net.shadowmage.ancientwarfare.vehicle.helpers.VehicleFiringVarsHelper;

public class BallistaVarHelper extends VehicleFiringVarsHelper {

	public float crankAngle = 0.f;
	public float crankSpeed = 0.f;

	public float bowAngle = 67.5f;
	public float bowSpeed = 0.f;

	public float stringAngle = getStringAngle(bowAngle);
	public float stringSpeed = 0.f;

	public float triggerAngle = 0.f;

	/**
	 * @param vehicle
	 */
	public BallistaVarHelper(VehicleBase vehicle) {
		super(vehicle);
	}

	@Override
	public void onFiringUpdate() {
		vehicle.firingHelper.startLaunching();
		vehicle.firingHelper.spawnMissile(0, 0, 0);
		triggerAngle = 0.f;
	}

	@Override
	public void onReloadUpdate() {
		float prevAngle = bowAngle;
		bowAngle += (float) ((float) 37.5f / (float) vehicle.currentReloadTicks);
		if (bowAngle >= 67.5f) {
			bowAngle = 67.5f;
			triggerAngle = -70.f;
		}
		bowSpeed = bowAngle - prevAngle;
		prevAngle = stringAngle;
		stringAngle = getStringAngle(bowAngle);
		stringSpeed = stringAngle - prevAngle;
		this.crankAngle -= 4;
		this.crankSpeed = -4;
	}

	@Override
	public void onLaunchingUpdate() {
		float prevAngle = bowAngle;
		bowAngle -= 37.5 / 5;
		if (bowAngle < 30) {
			bowAngle = 30;
			vehicle.firingHelper.setFinishedLaunching();
		}
		bowSpeed = bowAngle - prevAngle;
		prevAngle = stringAngle;
		stringAngle = getStringAngle(bowAngle);
		stringSpeed = stringAngle - prevAngle;
	}

	@Override
	public void onReloadingFinished() {
		this.bowAngle = 67.5f;
		this.stringAngle = getStringAngle(bowAngle);
		this.bowSpeed = 0.f;
		this.stringSpeed = 0.f;
		this.crankSpeed = 0.f;
		triggerAngle = -70.f;
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setFloat("cA", crankAngle);
		tag.setFloat("cS", crankSpeed);
		tag.setFloat("bA", bowAngle);
		tag.setFloat("bS", bowSpeed);
		tag.setFloat("sA", stringAngle);
		tag.setFloat("sS", stringSpeed);
		tag.setFloat("tA", triggerAngle);
		return tag;
	}

	public float getStringAngle(float bowAngle) {
		float percentTravel = (bowAngle - 30.f) / 37.5f;
		float adj = percentTravel * 1.305f;
		return -30 - adj * bowAngle;
	}

	@Override
	public void deserializeNBT(NBTTagCompound tag) {
		this.crankAngle = tag.getFloat("cA");
		this.crankSpeed = tag.getFloat("cS");
		this.bowAngle = tag.getFloat("bA");
		this.bowSpeed = tag.getFloat("bS");
		this.stringAngle = tag.getFloat("sA");
		this.stringSpeed = tag.getFloat("sS");
		this.triggerAngle = tag.getFloat("tA");
	}

	@Override
	public float getVar1() {
		return crankAngle;
	}

	@Override
	public float getVar2() {
		return crankSpeed;
	}

	@Override
	public float getVar3() {
		return bowAngle;
	}

	@Override
	public float getVar4() {
		return bowSpeed;
	}

	@Override
	public float getVar5() {
		return stringAngle;
	}

	@Override
	public float getVar6() {
		return stringSpeed;
	}

	@Override
	public float getVar7() {
		return triggerAngle;
	}

	@Override
	public float getVar8() {
		return 0;
	}

}
