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

import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.Listener;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;
import net.shadowmage.ancientwarfare.core.gui.elements.CompositeScrolled;
import net.shadowmage.ancientwarfare.core.gui.elements.GuiElement;
import net.shadowmage.ancientwarfare.vehicle.container.ContainerVehicle;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleBase;
import net.shadowmage.ancientwarfare.vehicle.gui.elements.ButtonAmmo;
import net.shadowmage.ancientwarfare.vehicle.missiles.IAmmo;

import java.util.List;

public class GuiVehicleAmmoSelection extends GuiContainerBase<ContainerVehicle> {

	VehicleBase vehicle;

	private CompositeScrolled area;

	public GuiVehicleAmmoSelection(ContainerBase container, VehicleBase vehicle) {
		super(container);
		this.vehicle = vehicle;
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
		area = new CompositeScrolled(this, 10, 40, xSize - 20, ySize - 40 - 10);
		addGuiElement(area);
	}

	@Override
	public void setupElements() {
		List<IAmmo> ammos = vehicle.vehicleType.getValidAmmoTypes();
		int totalHeight = ammos.size() * 22;

		Button done = new Button(getXSize() - 35 - 5, 5, 35, 12, "Done");
		done.addNewListener(new Listener(Listener.MOUSE_UP) {
			@Override
			public boolean onEvent(GuiElement widget, ActivationEvent evt) {
				closeGui();
				return true;
			}
		});
		addGuiElement(done);

		area.clearElements();
		for (int i = 0; i < ammos.size(); i++) {
			ButtonAmmo ammo = new ButtonAmmo(5, i + 10, getXSize() - 20 - 20 - 5, i * 22, ammos.get(i), vehicle);
			ammo.addNewListener(new Listener(Listener.MOUSE_UP) {
				@Override
				public boolean onEvent(GuiElement widget, ActivationEvent evt) {
					closeGui();
					return true;
				}
			});
			area.addGuiElement(ammo);
		}

	}
}
