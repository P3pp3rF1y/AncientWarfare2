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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.vehicle.AncientWarfareVehicles;
import net.shadowmage.ancientwarfare.vehicle.config.AWVehicleStatics;
import net.shadowmage.ancientwarfare.vehicle.entity.IVehicleType;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleBase;
import net.shadowmage.ancientwarfare.vehicle.entity.types.VehicleType;
import net.shadowmage.ancientwarfare.vehicle.render.RenderItemSpawner;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ItemSpawner extends ItemBaseVehicle {
	private static final String LEVEL_TAG = "level";
	private static final String HEALTH_TAG = "health";
	private static final String SPAWN_DATA_TAG = "spawnData";

	public ItemSpawner() {
		super("spawner");
		setHasSubtypes(true);
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		ItemStack stack = player.getHeldItem(hand);

		if (stack.isEmpty()) {
			return EnumActionResult.FAIL;
		} else if (world.isRemote) {
			return EnumActionResult.SUCCESS;
		}

		//noinspection ConstantConditions
		if (stack.hasTagCompound() && stack.getTagCompound().hasKey(SPAWN_DATA_TAG)) {
			NBTTagCompound tag = stack.getTagCompound().getCompoundTag(SPAWN_DATA_TAG);
			int level = tag.getInteger(LEVEL_TAG);
			BlockPos offsetPos = pos.offset(facing);
			Optional<VehicleBase> v = VehicleType.getVehicleForType(world, stack.getItemDamage(), level);
			if (!v.isPresent()) {
				return EnumActionResult.FAIL;
			}
			VehicleBase vehicle = v.get();
			if (tag.hasKey(HEALTH_TAG)) {
				vehicle.setHealth(tag.getFloat(HEALTH_TAG));
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
		AncientWarfareVehicles.LOG.error("Vehicle spawner item was missing NBT data, something may have corrupted this item");
		return EnumActionResult.FAIL;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flagIn) {
		super.addInformation(stack, world, tooltip, flagIn);
		//noinspection ConstantConditions
		if (stack.hasTagCompound() && stack.getTagCompound().hasKey(SPAWN_DATA_TAG)) {
			NBTTagCompound tag = stack.getTagCompound().getCompoundTag(SPAWN_DATA_TAG);
			int level = tag.getInteger(LEVEL_TAG);
			tooltip.add("Material Level: " + level);//TODO additional translations
			if (tag.hasKey(HEALTH_TAG)) {
				tooltip.add("Vehicle Health: " + tag.getFloat(HEALTH_TAG));
			}

			Optional<VehicleBase> v = VehicleType.getVehicleForType(world, stack.getItemDamage(), level);
			if (!v.isPresent()) {
				return;
			}
			tooltip.addAll(v.get().vehicleType.getDisplayTooltip().stream().map(I18n::format).collect(Collectors.toSet()));
		}
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		IVehicleType vehicle = VehicleType.vehicleTypes[stack.getItemDamage()];
		return vehicle == null ? "" : vehicle.getDisplayName();
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		if (!isInCreativeTab(tab)) {
			return;
		}
		items.addAll(VehicleType.getCreativeDisplayItems());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerClient() {
		ModelRegistryHelper.registerItemRenderer(this, new RenderItemSpawner());
	}
}
