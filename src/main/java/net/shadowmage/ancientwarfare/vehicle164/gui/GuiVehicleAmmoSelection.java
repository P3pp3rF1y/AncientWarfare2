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
import net.minecraft.util.ResourceLocation;
import net.shadowmage.ancientwarfare.vehicle.AncientWarfareVehicles;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleBase;
import net.shadowmage.ancientwarfare.vehicle.missiles.IAmmo;
import shadowmage.ancient_warfare.client.gui.GuiContainerAdvanced;
import shadowmage.ancient_warfare.client.gui.elements.GuiButtonVehicleAmmo;
import shadowmage.ancient_warfare.client.gui.elements.GuiScrollableArea;
import shadowmage.ancient_warfare.client.gui.elements.IGuiElement;

import java.util.List;

public class GuiVehicleAmmoSelection extends GuiContainerAdvanced {

	VehicleBase vehicle;

	/**
	 * @param container
	 */
	public GuiVehicleAmmoSelection(Container container, VehicleBase vehicle) {
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
	public ResourceLocation getGuiBackGroundTexture() {
		return new ResourceLocation(AncientWarfareVehicles.modID, "gui/guiBackgroundLarge.png");
	}

	@Override
	public void renderExtraBackGround(int mouseX, int mouseY, float partialTime) {
	}

	@Override
	public void updateScreenContents() {
		area.updateGuiPos(guiLeft, guiTop);
	}

	@Override
	public void onElementActivated(IGuiElement element) {
		if (element.getElementNumber() == 1)//done button
		{
			this.closeGUI();
		} else if (element.getElementNumber() >= 10)//ammo select buttons
		{
			int ammoIndex = element.getElementNumber() - 10;
			vehicle.ammoHelper.handleClientAmmoSelection(ammoIndex);
			this.closeGUI();
		}
	}

	GuiScrollableArea area;

	@Override
	public void setupControls() {
		List<IAmmo> ammos = vehicle.vehicleType.getValidAmmoTypes();
		int totalHeight = ammos.size() * 22;
		area = new GuiScrollableArea(0, this, 10, 40, getXSize() - 20, getYSize() - 40 - 10, totalHeight);
		this.guiElements.put(0, area);

		this.addGuiButton(1, getXSize() - 35 - 5, 5, 35, 12, "Done");

		for (int i = 0; i < ammos.size(); i++) {
			area.elements.add(new GuiButtonVehicleAmmo(i + 10, area, 5, i * 22, getXSize() - 20 - 20 - 5, ammos.get(i), vehicle));
		}
		//add scrollable area
		//  add new button for each of
	}

	@Override
	public void updateControls() {
		List<IAmmo> ammos = vehicle.vehicleType.getValidAmmoTypes();
		int totalHeight = ammos.size() * 22;
		area.updateTotalHeight(totalHeight);
	}

}
