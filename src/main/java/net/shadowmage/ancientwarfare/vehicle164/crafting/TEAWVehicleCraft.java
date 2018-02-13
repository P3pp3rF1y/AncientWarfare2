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

package shadowmage.ancient_warfare.common.crafting;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.vehicle.entity.IVehicleType;
import net.shadowmage.ancientwarfare.vehicle.entity.types.VehicleType;
import shadowmage.ancient_warfare.common.civics.CivicWorkType;
import shadowmage.ancient_warfare.common.inventory.AWInventoryBasic;
import shadowmage.ancient_warfare.common.network.GUIHandler;

public class TEAWVehicleCraft extends TEAWCraftingWorkSite {

	int progressPerWork = 20;

	public int vehicleType = -1;
	public int vehicleLevel = -1;

	/**
	 *
	 */
	public TEAWVehicleCraft() {
		this.modelID = 3;
		this.craftMatrix = new int[] {0, 1, 2, 3, 4, 5, 6, 7, 8};
		this.resultSlot = new int[] {9};
		this.bookSlot = new int[] {10};
		this.inventory = new AWInventoryBasic(11);
		this.workType = CivicWorkType.CRAFT;
		this.shouldBroadcast = true;
	}

	@Override
	public void setRecipe(ResourceListRecipe recipe) {
		super.setRecipe(recipe);
		if (recipe != null && recipe.type == RecipeType.VEHICLE) {
			ItemStack result = recipe.getResult();
			if (result != null) {
				int type = result.getItemDamage();
				int level = 0;
				IVehicleType t = VehicleType.getVehicleType(type);
				if (result.hasTagCompound() && result.getTagCompound().hasKey("spawnData")) {
					level = result.getTagCompound().getCompoundTag("spawnData").getInteger("level");
				}
				this.vehicleType = type;
				this.vehicleLevel = level;
			}
		}
		this.recipeStartCheckDelayTicks = 0;
		this.world.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	@Override
	public void stopAndClear() {
		super.stopAndClear();
		this.vehicleLevel = -1;
		this.vehicleType = -1;
		this.world.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	@Override
	public void readDescriptionPacket(NBTTagCompound tag) {
		if (tag.hasKey("type")) {
			this.vehicleType = tag.getInteger("type");
			this.vehicleLevel = tag.getInteger("level");
		}
	}

	@Override
	public void writeDescriptionData(NBTTagCompound tag) {
		tag.setInteger("type", this.vehicleType);
		tag.setInteger("level", this.vehicleLevel);
	}

	@Override
	public void writeExtraNBT(NBTTagCompound tag) {
		tag.setInteger("type", this.vehicleType);
		tag.setInteger("level", this.vehicleLevel);
	}

	@Override
	public void readExtraNBT(NBTTagCompound tag) {
		this.vehicleLevel = tag.getInteger("level");
		this.vehicleType = tag.getInteger("type");
	}

	@Override
	public void onBlockClicked(EntityPlayer player) {
		if (!player.world.isRemote) {
			GUIHandler.instance().openGUI(GUIHandler.VEHICLE_CRAFT, player, player.world, xCoord, yCoord, zCoord);
		}
	}

}
