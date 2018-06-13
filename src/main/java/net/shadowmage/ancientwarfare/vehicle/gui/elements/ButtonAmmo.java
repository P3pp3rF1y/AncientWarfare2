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

package net.shadowmage.ancientwarfare.vehicle.gui.elements;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleBase;
import net.shadowmage.ancientwarfare.vehicle.missiles.IAmmo;
import net.shadowmage.ancientwarfare.vehicle.registry.AmmoRegistry;

@SideOnly(Side.CLIENT)
public class ButtonAmmo extends Button {

	IAmmo ammo;
	ItemStack stack;
	VehicleBase vehicle;
	protected static RenderItem itemRenderer = Minecraft.getMinecraft().getRenderItem();

	public ButtonAmmo(int topLeftX, int topLeftY, int width, int height, IAmmo ammo, VehicleBase vehicle) {
		super(topLeftX, topLeftY, width, height, "");
		this.ammo = ammo;
		stack = new ItemStack(AmmoRegistry.getItemForAmmo(ammo));
		this.vehicle = vehicle;
		if (ammo != null) {
			setText(I18n.format("item." + ammo.getRegistryName().getResourcePath() + ".name"));
		}
	}

	@Override
	protected void onPressed() {
		vehicle.ammoHelper.handleClientAmmoSelection(ammo.getRegistryName());
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTick) {
		super.render(mouseX, mouseY, partialTick);
		if (visible) {
			//draw ammo icon on left
			//draw ammo name to the right of that
			//on the far right, ammo qty
			if (this.ammo != null) {
				String quantity = String.valueOf(vehicle.ammoHelper.getCountOf(ammo));
				itemRenderer.renderItemIntoGUI(stack, renderX + 3, renderY + 3);
				int quantityRenderX = renderX + this.width - 10 - Minecraft.getMinecraft().fontRenderer.getStringWidth(quantity);
				Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(String.valueOf(quantity), quantityRenderX, renderY + textY, 0xffffffff);
			}
			//TODO proper text rendering based on what was in GuiButtonVehicleAmmo
		}
	}
}
