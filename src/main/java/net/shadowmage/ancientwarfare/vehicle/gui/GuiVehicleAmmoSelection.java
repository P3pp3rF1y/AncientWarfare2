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
import net.shadowmage.ancientwarfare.core.gui.elements.Button;
import net.shadowmage.ancientwarfare.core.gui.elements.CompositeScrolled;
import net.shadowmage.ancientwarfare.vehicle.container.ContainerVehicle;
import net.shadowmage.ancientwarfare.vehicle.gui.elements.ButtonAmmo;
import net.shadowmage.ancientwarfare.vehicle.missiles.IAmmo;

import java.util.List;
import java.util.stream.Collectors;

public class GuiVehicleAmmoSelection extends GuiContainerBase<ContainerVehicle> {

	private CompositeScrolled area;

	public GuiVehicleAmmoSelection(ContainerBase container) {
		super(container);
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
		area = new CompositeScrolled(this, 10, 40, xSize - 20, ySize - 40 - 10);
		addGuiElement(area);

		Button done = new Button(getXSize() - 35 - 5, 5, 35, 12, "Done") {
			@Override
			protected void onPressed() {
				closeGui();
			}
		};
		addGuiElement(done);
	}

	@Override
	public void setupElements() {
		List<IAmmo> ammos = getContainer().vehicle.vehicleType.getValidAmmoTypes().stream().filter(a -> getContainer().vehicle.ammoHelper.getCountOf(a) > 0)
				.collect(Collectors.toList());

		area.clearElements();

		int currentY = 10;

		for (int i = 0; i < ammos.size(); i++) {
			ButtonAmmo ammo = new ButtonAmmo(5, currentY, getXSize() - 20 - 20 - 5, 22, ammos.get(i), getContainer().vehicle) {
				@Override
				protected void onPressed() {
					super.onPressed();
					closeGui();
				}
			};
			area.addGuiElement(ammo);
			currentY += 25;
		}

		area.setAreaSize(currentY);
	}
}
