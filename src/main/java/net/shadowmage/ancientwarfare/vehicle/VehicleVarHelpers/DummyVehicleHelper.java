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

public class DummyVehicleHelper extends VehicleFiringVarsHelper {

	/**
	 * @param vehicle
	 */
	public DummyVehicleHelper(VehicleBase vehicle) {
		super(vehicle);
	}

	@Override
	public NBTTagCompound serializeNBT() {
		// TODO Auto-generated method stub
		return new NBTTagCompound();
	}

	@Override
	public void deserializeNBT(NBTTagCompound tag) {

	}

	@Override
	public void onFiringUpdate() {

	}

	@Override
	public void onReloadUpdate() {

	}

	@Override
	public void onLaunchingUpdate() {

	}

	@Override
	public void onReloadingFinished() {

	}

	@Override
	public float getVar1() {
		return 0;
	}

	@Override
	public float getVar2() {
		return 0;
	}

	@Override
	public float getVar3() {
		return 0;
	}

	@Override
	public float getVar4() {
		return 0;
	}

	@Override
	public float getVar5() {
		return 0;
	}

	@Override
	public float getVar6() {
		return 0;
	}

	@Override
	public float getVar7() {
		return 0;
	}

	@Override
	public float getVar8() {
		return 0;
	}

}
