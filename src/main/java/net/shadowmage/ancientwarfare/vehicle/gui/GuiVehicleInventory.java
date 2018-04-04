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
import net.shadowmage.ancientwarfare.core.gui.elements.Label;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.vehicle.container.ContainerVehicleInventory;
import net.shadowmage.ancientwarfare.vehicle.network.PacketPackCommand;

public class GuiVehicleInventory extends GuiContainerBase<ContainerVehicleInventory> {
	public GuiVehicleInventory(ContainerBase container) {
		super(container);
		this.shouldCloseOnVanillaKeys = true;
		this.ySize = this.getYSize();
		this.xSize = this.getXSize();
	}

	@Override
	public int getXSize() {
		return 171 + 16 + 8;
	}

	@Override
	public int getYSize() {
		return getContainer() == null ? 240 : getContainer().playerY + 4 * 18 + 8 + 4;
	}

	@Override
	public void initElements() {
		//TODO lang translations
		if (getContainer().vehicle.inventory.storageInventory.getSlots() > 0) {
			addGuiElement(new Label(8, getContainer().storageY - 10, "Storage"));
		}
		addGuiElement(new Label(8, getContainer().extrasY - 10, "Ammo"));
		addGuiElement(new Label(8 + 3 * 18 + 4, getContainer().extrasY - 10, "Upg."));
		addGuiElement(new Label(8 + 6 * 18 + 8, getContainer().extrasY - 10, "Armor"));
		addGuiElement(new Label(8, getContainer().playerY - 10, "Player Inventory"));
		Button stats = new Button(8, 4, 45, 16, "Stats") {
			@Override
			protected void onPressed() {
				mc.displayGuiScreen(new GuiVehicleStats(GuiVehicleInventory.this, getContainer().vehicle));
			}
		};
		addGuiElement(stats);
		Button pack = new Button(8 + 45 + 4, 4, 45, 16, "Pack") {
			@Override
			protected void onPressed() {
				NetworkHandler.sendToServer(new PacketPackCommand(getContainer().vehicle));
				closeGui();
			}
		};
		addGuiElement(pack);

		if (getContainer().vehicle.inventory.storageInventory.getSlots() > 0) {
			Button minus = new Button(171, getContainer().storageY - 1, 16, 16, "-") {
				@Override
				protected void onPressed() {
					getContainer().prevRow();
				}
			};
			addGuiElement(minus);
			Button plus = new Button(171, getContainer().storageY + 3 * 18 - 16 - 1, 16, 16, "+") {
				@Override
				protected void onPressed() {
					getContainer().nextRow();
				}
			};
			addGuiElement(plus);
		}
	}

	@Override
	public void setupElements() {

	}
}
