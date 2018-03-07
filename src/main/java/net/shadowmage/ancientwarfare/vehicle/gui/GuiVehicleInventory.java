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

import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.Listener;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;
import net.shadowmage.ancientwarfare.core.gui.elements.GuiElement;
import net.shadowmage.ancientwarfare.core.gui.elements.Label;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.vehicle.container.ContainerVehicle;
import net.shadowmage.ancientwarfare.vehicle.network.PacketVehicle;

public class GuiVehicleInventory extends GuiContainerBase<ContainerVehicle> {
	ContainerVehicle container;

	public GuiVehicleInventory(ContainerVehicle container) {
		super(container);
		this.container = container;
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
		return container == null ? 240 : container.playerY + 4 * 18 + 8 + 4;
	}

	@Override
	public void initElements() {
		//TODO lang translations
		if (this.container.vehicle.inventory.storageInventory.getSlots() > 0) {
			addGuiElement(new Label(8, container.storageY - 10, "Storage"));
		}
		addGuiElement(new Label(8, container.extrasY - 10, "Ammo"));
		addGuiElement(new Label(8 + 3 * 18 + 4, container.extrasY - 10, "Upg."));
		addGuiElement(new Label(8 + 6 * 18 + 8, container.extrasY - 10, "Armor"));
		addGuiElement(new Label(8, container.playerY - 10, "Player Inventory"));
		Button done = new Button(8 + 90 + 8, 4, 45, 16, "Done");
		done.addNewListener(new Listener(Listener.MOUSE_UP) {
			@Override
			public boolean onEvent(GuiElement widget, ActivationEvent evt) {
				closeGui();
				return true;
			}
		});
		addGuiElement(done);

		Button stats = new Button(8, 4, 45, 16, "Stats");
		stats.addNewListener(new Listener(Listener.MOUSE_UP) {
			@Override
			public boolean onEvent(GuiElement widget, ActivationEvent evt) {
				mc.displayGuiScreen(new GuiVehicleStats(container, container.vehicle));
				return true;
			}
		});
		addGuiElement(stats);
		Button pack = new Button(8 + 45 + 4, 4, 45, 16, "Pack");
		pack.addNewListener(new Listener(Listener.MOUSE_UP) {
			@Override
			public boolean onEvent(GuiElement widget, ActivationEvent evt) {
				PacketVehicle pkt = new PacketVehicle();
				pkt.setParams(container.vehicle);
				pkt.setPackCommand();
				NetworkHandler.sendToServer(pkt);
				closeGui();
				return true;
			}
		});

		if (container.vehicle.inventory.storageInventory.getSlots() > 0) {
			Button minus = new Button(171, container.storageY - 1, 16, 16, "-");
			minus.addNewListener(new Listener(Listener.MOUSE_UP) {
				@Override
				public boolean onEvent(GuiElement widget, ActivationEvent evt) {
					container.prevRow();
					return true;
				}
			});
			addGuiElement(minus);
			Button plus = new Button(171, container.storageY + 3 * 18 - 16 - 1, 16, 16, "+");
			plus.addNewListener(new Listener(Listener.MOUSE_UP) {
				@Override
				public boolean onEvent(GuiElement widget, ActivationEvent evt) {
					container.nextRow();
					return true;
				}
			});
			addGuiElement(plus);
		}
	}

	@Override
	public void setupElements() {

	}
}
