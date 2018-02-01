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

package shadowmage.ancient_warfare.common.vehicles;

import net.minecraft.item.ItemStack;
import shadowmage.ancient_warfare.common.crafting.ResourceListRecipe;
import shadowmage.ancient_warfare.common.research.IResearchGoal;
import shadowmage.ancient_warfare.common.utils.ItemStackWrapperCrafting;
import shadowmage.ancient_warfare.common.vehicles.armors.IVehicleArmorType;
import shadowmage.ancient_warfare.common.vehicles.helpers.VehicleFiringVarsHelper;
import shadowmage.ancient_warfare.common.vehicles.materials.IVehicleMaterial;
import shadowmage.ancient_warfare.common.vehicles.missiles.IAmmoType;
import shadowmage.ancient_warfare.common.vehicles.upgrades.IVehicleUpgradeType;

import java.util.HashSet;
import java.util.List;

public interface IVehicleType {

	public abstract float getWidth();

	public abstract float getHeight();

	public abstract float getBaseWeight();

	/*adjustable stats, set at time of vehicle registration DO NOT CHANGE DURING RUN-TIME--CHANGES WILL NOT PROPOGATE PROPERLY*/
	public abstract float getBaseHealth();//base max health, before any materials adjustments

	public abstract void setBaseHealth(float val);

	public abstract float getBaseForwardSpeed();

	public abstract void setBaseForwardSpeed(float val);

	public abstract float getBaseStrafeSpeed();

	public abstract void setBaseStrafeSpeed(float val);

	public abstract float getBasePitchMin();

	public abstract void setBasePitchMin(float val);

	public abstract float getBasePitchMax();

	public abstract void setBasePitchMax(float val);

	public abstract float getBaseTurretRotationAmount();//max rotation from a center point. >=180 means the turret can spin around completely

	public abstract void setBaseTurretRotationAmount(float val);

	public abstract float getBaseMissileVelocityMax();//base missile velocity, before materials or upgrades

	public abstract void setBaseMissileVelocity(float val);

	public abstract float getBaseAccuracy();

	public abstract void setBaseAccuracy(float val);

	public abstract String getTextureForMaterialLevel(int level);//get the texture for the input material quality level

	public abstract String getDisplayName();

	public abstract String getLocalizedName();

	public abstract List<String> getDisplayTooltip();

	public abstract int getGlobalVehicleType();//by number, registry num...

	public abstract IVehicleMaterial getMaterialType();//wood, iron...?? material type will apply adjustments to base stats, before upgrades/etc are applied

	public abstract boolean isMountable();//should allow mounting

	public abstract boolean isDrivable();//should check movement input params?

	public abstract boolean isCombatEngine();//should check non-movement input params?

	public abstract boolean canSoldiersPilot();//can npcs pilot this vehicle (e.g. normal ground vehicle)

	public abstract boolean canAdjustYaw();//can aim yaw be adjusted independently of vehicle yaw?

	public abstract boolean canAdjustPitch();//can aim pitch be adjusted? (should be EITHER pitch OR power)

	public abstract boolean canAdjustPower();//can shot velocity be adjusted? (should be EITHER pitch OR power)

	public abstract float getMissileForwardsOffset();//the offset in the turretYaw direction from the turretPosition

	public abstract float getMissileHorizontalOffset();//the offset in the turretYaw+90 direction from the turretPosition

	public abstract float getMissileVerticalOffset();//the offset in the y+ direction from the turretPosition

	public abstract float getTurretPosX();//the offset of the turret from 0,0 at vehicle rotation 0

	public abstract float getTurretPosY();//the offset of the turret from 0,0 at vehicle rotation 0

	public abstract float getTurretPosZ();//the offset of the turret from 0,0 at vehicle rotation 0

	public abstract float getRiderForwardsOffset();//the offset from 0,0 or turretPos of the rider

	public abstract float getRiderHorizontalOffset();//the offset from 0,0 or turretPos of the rider

	public abstract float getRiderVerticalOffest();//the offset from 0,0 or turretPos of the rider

	public abstract float getMinAttackDistance();//used by soldiers to determine when to get off a vehicle

	public abstract boolean shouldRiderSit();//should rider be seated while riding?

	public abstract boolean moveRiderWithTurret();//should position of rider update with the position of the turret, rather than vehicle?

	public abstract boolean isAmmoValidForInventory(IAmmoType ammo);//does not determine if it can be fired, only if it can be placed into inventory

	public abstract boolean isUpgradeValid(IVehicleUpgradeType upgrade);

	public abstract boolean isArmorValid(IVehicleArmorType armor);

	public abstract List<IAmmoType> getValidAmmoTypes();

	public abstract List<IVehicleUpgradeType> getValidUpgrades();

	public abstract List<IVehicleArmorType> getValidArmors();

	public abstract int getStorageBaySize();

	public abstract int getAmmoBaySize();

	public abstract int getArmorBaySize();

	public abstract int getUpgradeBaySize();

	public abstract float getMaxMissileWeight();//in KG--will be adjusted by material... any additional missile weight over this will reduce max launch speed by a ratio

	public abstract int getMaterialQuantity();//get the quantity of the main material to construct this vehicle

	public abstract List<ItemStackWrapperCrafting> getAdditionalMaterials();//get a list of additional materials needed to construct this vehicle

	public abstract ItemStack getStackForLevel(int level);

	public abstract IAmmoType getAmmoForSoldierRank(int rank);//what ammo type should soldiers use if !Config.soldiersUseAmmo

	public abstract String getIconTexture();

	public abstract VehicleFiringVarsHelper getFiringVarsHelper(VehicleBase veh);

	public abstract ResourceListRecipe constructRecipe(int level);

	public abstract HashSet<IResearchGoal> getNeededResearchFor(int level);

	public abstract VehicleMovementType getMovementType();

	public abstract void setEnabled(boolean val);

	public abstract boolean isEnabled();//determined via config, used to add recipes and to loot tables

	public abstract boolean isEnabledForLoot();

	public abstract boolean isEnabledForCrafting();

	public abstract void setEnabledForLoot(boolean val);

	public abstract void setEnabledForCrafting(boolean val);

	public abstract String getConfigName();//get the name used to load config settings for this vehicle

}
