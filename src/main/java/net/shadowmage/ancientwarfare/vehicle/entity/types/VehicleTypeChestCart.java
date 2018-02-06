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

package net.shadowmage.ancientwarfare.vehicle.entity.types;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleBase;
import net.shadowmage.ancientwarfare.vehicle.entity.materials.VehicleMaterial;
import net.shadowmage.ancientwarfare.vehicle.helpers.VehicleFiringVarsHelper;
import net.shadowmage.ancientwarfare.vehicle.registry.ArmorRegistry;
import net.shadowmage.ancientwarfare.vehicle.registry.VehicleUpgradeRegistry;
import shadowmage.ancient_warfare.common.item.ItemLoader;
import shadowmage.ancient_warfare.common.research.ResearchGoal;
import shadowmage.ancient_warfare.common.utils.ItemStackWrapperCrafting;

public class VehicleTypeChestCart extends VehicleType {

	//TODO implement chest cart
	/**
	 * @param typeNum
	 */
	public VehicleTypeChestCart(int typeNum) {
		super(typeNum);
		this.configName = "chest_cart";
		this.vehicleMaterial = VehicleMaterial.materialWood;
		this.materialCount = 3;
		this.validArmors.add(ArmorRegistry.armorStone);
		this.validArmors.add(ArmorRegistry.armorObsidian);
		this.validArmors.add(ArmorRegistry.armorIron);
		this.validUpgrades.add(VehicleUpgradeRegistry.speedUpgrade);
		this.width = 2.7f;
		this.height = 1.8f;
		this.mountable = true;
		this.drivable = true;
		this.combatEngine = false;
		this.riderSits = false;
		this.pilotableBySoldiers = false;
		this.riderVerticalOffset = 0.5f;
		this.riderForwardsOffset = 2.85f;
		this.baseForwardSpeed = 3.7f * 0.05f;
		this.baseStrafeSpeed = 1.75f;
		this.ammoBaySize = 0;
		this.upgradeBaySize = 6;
		this.armorBaySize = 6;
		this.storageBaySize = 54 * 4;
		this.displayName = "item.vehicleSpawner.17";
		this.displayTooltip.add("item.vehicleSpawner.tooltip.noweapon");
		this.displayTooltip.add("item.vehicleSpawner.tooltip.mobile");
		this.displayTooltip.add("item.vehicleSpawner.tooltip.noturret");
		this.displayTooltip.add("item.vehicleSpawner.tooltip.storage");
		this.addNeededResearchForMaterials();
		this.addNeededResearch(0, ResearchGoal.vehicleMobility1);
		this.addNeededResearch(1, ResearchGoal.vehicleMobility2);
		this.addNeededResearch(2, ResearchGoal.vehicleMobility3);
		this.addNeededResearch(3, ResearchGoal.vehicleMobility4);
		this.addNeededResearch(4, ResearchGoal.vehicleMobility5);
		this.additionalMaterials.add(new ItemStackWrapperCrafting(Block.chest, 8, false, false));
		this.additionalMaterials.add(new ItemStackWrapperCrafting(ItemLoader.equipmentBay, 1, false, false));
	}

	@Override
	public VehicleFiringVarsHelper getFiringVarsHelper(VehicleBase veh) {
		return new ChestCartVarHelper(veh);
	}

	@Override
	public String getTextureForMaterialLevel(int level) {
		switch (level) {
			case 0:
				return new ResourceLocation(AncientWarfareCore.modID, "model/vehicle/chestCart1");
			case 1:
				return new ResourceLocation(AncientWarfareCore.modID, "model/vehicle/chestCart2");
			case 2:
				return new ResourceLocation(AncientWarfareCore.modID, "model/vehicle/chestCart3");
			case 3:
				return new ResourceLocation(AncientWarfareCore.modID, "model/vehicle/chestCart4");
			case 4:
				return new ResourceLocation(AncientWarfareCore.modID, "model/vehicle/chestCart5");
			default:
				return new ResourceLocation(AncientWarfareCore.modID, "model/vehicle/chestCart1");
		}
	}

	public class ChestCartVarHelper extends VehicleFiringVarsHelper {

		/**
		 * @param vehicle
		 */
		public ChestCartVarHelper(VehicleBase vehicle) {
			super(vehicle);
		}

		//@Override
		//public boolean interact(EntityPlayer player)
		//  {
		//  boolean control = PlayerTracker.instance().isControlPressed(player);
		//  if(vehicle.mountable() && !player.worldObj.isRemote && !control && (vehicle.riddenByEntity==null || vehicle.riddenByEntity==player))
		//    {
		//    player.mountEntity(vehicle);
		//    return true;
		//    }
		//  else if(!player.worldObj.isRemote && control)
		//    {
		//    GUIHandler.instance().openGUI(GUIHandler.VEHICLE_DEBUG, player, vehicle.world, vehicle.entityId, 0, 0);
		//    }
		//  return true;
		//  }

		@Override
		public NBTTagCompound serializeNBT() {
			return new NBTTagCompound();
		}

		@Override
		public void deserializeNBT(NBTTagCompound tag) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onFiringUpdate() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onReloadUpdate() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onLaunchingUpdate() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onReloadingFinished() {
			// TODO Auto-generated method stub

		}

		@Override
		public float getVar1() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public float getVar2() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public float getVar3() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public float getVar4() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public float getVar5() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public float getVar6() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public float getVar7() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public float getVar8() {
			// TODO Auto-generated method stub
			return 0;
		}
	}
}
