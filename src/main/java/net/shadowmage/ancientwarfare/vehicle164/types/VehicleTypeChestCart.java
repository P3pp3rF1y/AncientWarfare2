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

package shadowmage.ancient_warfare.common.vehicles.types;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import shadowmage.ancient_warfare.common.config.Config;
import shadowmage.ancient_warfare.common.item.ItemLoader;
import shadowmage.ancient_warfare.common.registry.ArmorRegistry;
import shadowmage.ancient_warfare.common.registry.VehicleUpgradeRegistry;
import shadowmage.ancient_warfare.common.research.ResearchGoal;
import shadowmage.ancient_warfare.common.utils.ItemStackWrapperCrafting;
import shadowmage.ancient_warfare.common.vehicles.VehicleBase;
import shadowmage.ancient_warfare.common.vehicles.helpers.VehicleFiringVarsHelper;
import shadowmage.ancient_warfare.common.vehicles.materials.VehicleMaterial;

public class VehicleTypeChestCart extends VehicleType {

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
		this.isMountable = true;
		this.isDrivable = true;
		this.isCombatEngine = false;
		this.shouldRiderSit = false;
		this.canSoldiersPilot = false;
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
				return Config.texturePath + "models/chestCart1.png";
			case 1:
				return Config.texturePath + "models/chestCart2.png";
			case 2:
				return Config.texturePath + "models/chestCart3.png";
			case 3:
				return Config.texturePath + "models/chestCart4.png";
			case 4:
				return Config.texturePath + "models/chestCart5.png";
			default:
				return Config.texturePath + "models/chestCart1.png";
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
		//  if(vehicle.isMountable() && !player.worldObj.isRemote && !control && (vehicle.riddenByEntity==null || vehicle.riddenByEntity==player))
		//    {
		//    player.mountEntity(vehicle);
		//    return true;
		//    }
		//  else if(!player.worldObj.isRemote && control)
		//    {
		//    GUIHandler.instance().openGUI(GUIHandler.VEHICLE_DEBUG, player, vehicle.worldObj, vehicle.entityId, 0, 0);
		//    }
		//  return true;
		//  }

		@Override
		public NBTTagCompound getNBTTag() {
			return new NBTTagCompound();
		}

		@Override
		public void readFromNBT(NBTTagCompound tag) {
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
