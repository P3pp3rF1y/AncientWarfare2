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

import codechicken.lib.model.ModelRegistryHelper;
import codechicken.lib.render.item.IItemRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.vehicle.AncientWarfareVehicles;
import net.shadowmage.ancientwarfare.vehicle.config.AWVehicleStatics;
import net.shadowmage.ancientwarfare.vehicle.entity.IVehicleType;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleBase;
import net.shadowmage.ancientwarfare.vehicle.entity.types.VehicleType;
import net.shadowmage.ancientwarfare.vehicle.render.RenderItemSpawner;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;

public class ItemSpawner extends ItemBaseVehicle {
	public ItemSpawner() {
		super("spawner");
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		ItemStack stack = player.getHeldItem(hand);

		if (stack.isEmpty()) {
			return EnumActionResult.FAIL;
		} else if (world.isRemote) {
			return EnumActionResult.SUCCESS;
		}

		if (stack.hasTagCompound() && stack.getTagCompound().hasKey("spawnData")) {
			NBTTagCompound tag = stack.getTagCompound().getCompoundTag("spawnData");
			int level = tag.getInteger("level");
			BlockPos offsetPos = pos.offset(facing);
			VehicleBase vehicle = VehicleType.getVehicleForType(world, stack.getItemDamage(), level);
			if (tag.hasKey("health")) {
				vehicle.setHealth(tag.getFloat("health"));
			}
			vehicle.setPosition(offsetPos.getX() + 0.5d, offsetPos.getY(), offsetPos.getZ() + 0.5d);
			vehicle.prevRotationYaw = vehicle.rotationYaw = -player.rotationYaw + 180;
			vehicle.localTurretDestRot = vehicle.localTurretRotation = vehicle.localTurretRotationHome = vehicle.rotationYaw;
			if (AWVehicleStatics.useVehicleSetupTime) {
				vehicle.setSetupState(true, 100);
			}
			world.spawnEntity(vehicle);
			if (!player.capabilities.isCreativeMode) {
				stack.shrink(1);
				if (stack.getCount() <= 0) {
					player.setHeldItem(hand, ItemStack.EMPTY);
				}
			}
			return EnumActionResult.SUCCESS;
		}
		AncientWarfareVehicles.log.error("Vehicle spawner item was missing NBT data, something may have corrupted this item");
		return EnumActionResult.FAIL;
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flagIn) {
		super.addInformation(stack, world, tooltip, flagIn);
		if (stack != null) {
			if (stack.hasTagCompound() && stack.getTagCompound().hasKey("spawnData")) {
				NBTTagCompound tag = stack.getTagCompound().getCompoundTag("spawnData");
				int level = tag.getInteger("level");
				tooltip.add("Material Level: " + level);//TODO additional translations
				if (tag.hasKey("health")) {
					tooltip.add("Vehicle Health: " + tag.getFloat("health"));
				}

				VehicleBase vehicle = VehicleType.getVehicleForType(world, stack.getItemDamage(), level);
				if (vehicle == null) {
					return;
				}
				tooltip.addAll(vehicle.vehicleType.getDisplayTooltip().stream().map(I18n::format).collect(Collectors.toSet()));
			}
		}
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		if (stack.hasTagCompound() && stack.getTagCompound().hasKey("spawnData")) {
			NBTTagCompound tag = stack.getTagCompound().getCompoundTag("spawnData");
			IVehicleType vehicle = VehicleType.vehicleTypes[stack.getItemDamage()];
			return vehicle == null ? "" : vehicle.getDisplayName();
		}

		return "item.vehicleSpawner";
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		if (!isInCreativeTab(tab)) {
			return;
		}

		List displayStacks = VehicleType.getCreativeDisplayItems();
		items.addAll(displayStacks);
	}

	public static int getVehicleLevelForStack(ItemStack stack) {
		if (stack.hasTagCompound() && stack.getTagCompound().hasKey("spawnData")) {
			return stack.getTagCompound().getCompoundTag("spawnData").getInteger("level");
		}
		return 0;
	}

	private static final IItemRenderer ITEM_RENDERER = new RenderItemSpawner();

	@Override
	public void registerClient() {
		ModelRegistryHelper.registerItemRenderer(this, ITEM_RENDERER);
	}
}
