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

package net.shadowmage.ancientwarfare.vehicle.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import shadowmage.ancient_warfare.common.config.Config;
import shadowmage.ancient_warfare.common.tracker.TeamTracker;
import shadowmage.ancient_warfare.common.utils.BlockPosition;
import shadowmage.ancient_warfare.common.utils.BlockTools;
import shadowmage.ancient_warfare.common.vehicles.VehicleBase;
import shadowmage.ancient_warfare.common.vehicles.types.VehicleType;

import java.util.List;

public class ItemVehicleSpawner extends AWItemClickable {

	public ItemVehicleSpawner(int itemID) {
		super(itemID, true);
	}

	@Override
	public boolean onUsedFinal(World world, EntityPlayer player, ItemStack stack, BlockPosition hit, int side) {
		if (hit == null || world.isRemote || stack == null) {
			return false;
		}
		if (stack.hasTagCompound() && stack.getTagCompound().hasKey("AWVehSpawner")) {
			NBTTagCompound tag = stack.getTagCompound().getCompoundTag("AWVehSpawner");
			int level = tag.getInteger("lev");
			hit = BlockTools.offsetForSide(hit, side);
			VehicleBase vehicle = VehicleType.getVehicleForType(world, stack.getItemDamage(), level);
			if (tag.hasKey("health")) {
				vehicle.setHealth(tag.getFloat("health"));
			}
			vehicle.teamNum = TeamTracker.instance().getTeamForPlayer(player);
			vehicle.setPosition(hit.x + 0.5d, hit.y, hit.z + 0.5d);
			vehicle.prevRotationYaw = vehicle.rotationYaw = -player.rotationYaw + 180;
			vehicle.localTurretDestRot = vehicle.localTurretRotation = vehicle.localTurretRotationHome = vehicle.rotationYaw;
			if (Config.useVehicleSetupTime) {
				vehicle.setSetupState(true, 100);
			}
			world.spawnEntityInWorld(vehicle);
			if (!player.capabilities.isCreativeMode) {
				stack.stackSize--;
				if (stack.stackSize <= 0) {
					player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
				}
			}
			return true;
		}
		Config.logError("Vehicle spawner item was missing NBT data, something may have corrupted this item");
		return false;
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
		super.addInformation(stack, par2EntityPlayer, par3List, par4);
		if (stack != null) {
			if (stack.hasTagCompound() && stack.getTagCompound().hasKey("AWVehSpawner")) {
				NBTTagCompound tag = stack.getTagCompound().getCompoundTag("AWVehSpawner");
				par3List.add("Material Level: " + tag.getInteger("lev"));
				if (tag.hasKey("health")) {
					par3List.add("Vehicle Health: " + tag.getFloat("health"));
				}
			}
		}
	}

	@Override
	public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List) {
		List displayStacks = VehicleType.getCreativeDisplayItems();
		par3List.addAll(displayStacks);
	}

	public static int getVehicleLevelForStack(ItemStack stack) {
		if (stack.hasTagCompound() && stack.getTagCompound().hasKey("AWVehSpawner")) {
			return stack.getTagCompound().getCompoundTag("AWVehSpawner").getInteger("lev");
		}
		return 0;
	}

	@Override
	public boolean onUsedFinalLeft(World world, EntityPlayer player, ItemStack stack, BlockPosition hit, int side) {
		// TODO Auto-generated method stub
		return false;
	}

}
