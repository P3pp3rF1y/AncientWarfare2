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
import shadowmage.ancient_warfare.common.container.ContainerDummy;
import shadowmage.ancient_warfare.common.container.ContainerVehicle;
import shadowmage.ancient_warfare.common.network.Packet02Vehicle;

public class GuiVehicleInventory extends GuiContainerAdvanced {
	ContainerVehicle container;

	/**
	 * @param container
	 */
	public GuiVehicleInventory(Container container) {
		super(container);
		this.container = (ContainerVehicle) container;
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
	public String getGuiBackGroundTexture() {
		return Config.texturePath + "gui/guiBackgroundLarge.png";
	}

	@Override
	public void renderExtraBackGround(int mouseX, int mouseY, float partialTime) {
		if (this.container.vehicle.inventory.storageInventory.getSizeInventory() > 0) {
			this.drawStringGui("Storage", 8, container.storageY - 10, WHITE);
		}
		this.drawStringGui("Ammo", 8, container.extrasY - 10, WHITE);
		this.drawStringGui("Upg.", 8 + 3 * 18 + 4, container.extrasY - 10, WHITE);
		this.drawStringGui("Armor", 8 + 6 * 18 + 8, container.extrasY - 10, WHITE);
		this.drawStringGui("Player Inventory", 8, container.playerY - 10, WHITE);
	}

	@Override
	public void updateScreenContents() {

	}

	@Override
	public void onElementActivated(IGuiElement element) {
		switch (element.getElementNumber()) {
			case 0:
				this.closeGUI();
				break;
			case 1:
				mc.displayGuiScreen(new GuiVehicleStats(new ContainerDummy(), ((ContainerVehicle) this.inventorySlots).vehicle));
				break;
			case 2:
				Packet02Vehicle pkt = new Packet02Vehicle();
				pkt.setParams(((ContainerVehicle) this.inventorySlots).vehicle);
				pkt.setPackCommand();
				pkt.sendPacketToServer();
				this.closeGUI();
				break;

			case 3:
				((ContainerVehicle) inventorySlots).prevRow();
				break;

			case 4:
				((ContainerVehicle) inventorySlots).nextRow();
				break;

			default:
				break;
		}
	}

	@Override
	public void setupControls() {
		this.addGuiButton(0, 8 + 90 + 8, 4, 45, 16, "Done");
		this.addGuiButton(1, 8, 4, 45, 16, "Stats");
		this.addGuiButton(2, 8 + 45 + 4, 4, 45, 16, "Pack");

		if (container.vehicle.inventory.storageInventory.getSizeInventory() > 0) {
			this.addGuiButton(3, 171, container.storageY - 1, 16, 16, "-");
			this.addGuiButton(4, 171, container.storageY + 3 * 18 - 16 - 1, 16, 16, "+");
		}
	}

	@Override
	public void updateControls() {
		// TODO Auto-generated method stub

	}

}
