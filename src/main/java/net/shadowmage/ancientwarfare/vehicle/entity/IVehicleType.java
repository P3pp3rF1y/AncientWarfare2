package net.shadowmage.ancientwarfare.vehicle.entity;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.vehicle.armors.IVehicleArmor;
import net.shadowmage.ancientwarfare.vehicle.entity.materials.IVehicleMaterial;
import net.shadowmage.ancientwarfare.vehicle.entity.types.VehicleType;
import net.shadowmage.ancientwarfare.vehicle.helpers.VehicleFiringVarsHelper;
import net.shadowmage.ancientwarfare.vehicle.missiles.IAmmo;
import net.shadowmage.ancientwarfare.vehicle.upgrades.IVehicleUpgradeType;

import java.util.List;

public interface IVehicleType {

	//TODO REFACTOR really 200 lines in interface? break this down into logical groups

	float getWidth();

	float getHeight();

	float getBaseWeight();

	/*adjustable stats, set at time of vehicle registration DO NOT CHANGE DURING RUN-TIME--CHANGES WILL NOT PROPOGATE PROPERLY*/
	float getBaseHealth();//base max health, before any materials adjustments

	void setBaseHealth(float val);

	float getBaseForwardSpeed();

	void setBaseForwardSpeed(float val);

	float getBaseStrafeSpeed();

	void setBaseStrafeSpeed(float val);

	float getBasePitchMin();

	void setBasePitchMin(float val);

	float getBasePitchMax();

	void setBasePitchMax(float val);

	float getBaseTurretRotationAmount();//max rotation from a center point. >=180 means the turret can spin around completely

	void setBaseTurretRotationAmount(float val);

	float getBaseMissileVelocityMax();//base missile velocity, before materials or upgrades

	void setBaseMissileVelocity(float val);

	float getBaseAccuracy();

	void setBaseAccuracy(float val);

	ResourceLocation getTextureForMaterialLevel(int level);//get the texture for the input material quality level

	String getDisplayName();

	@SideOnly(Side.CLIENT)
	String getLocalizedName();

	List<String> getDisplayTooltip();

	int getGlobalVehicleType();//by number, registry num...

	IVehicleMaterial getMaterialType();//wood, iron...?? material type will apply adjustments to base stats, before upgrades/etc are applied

	boolean isMountable();//should allow mounting

	boolean isDrivable();//should check movement input params?

	boolean isCombatEngine();//should check non-movement input params?

	boolean canSoldiersPilot();//can npcs pilot this vehicle (e.g. normal ground vehicle)

	boolean canAdjustYaw();//can aim yaw be adjusted independently of vehicle yaw?

	boolean canAdjustPitch();//can aim pitch be adjusted? (should be EITHER pitch OR power)

	boolean canAdjustPower();//can shot velocity be adjusted? (should be EITHER pitch OR power)

	float getMissileForwardsOffset();//the offset in the turretYaw direction from the turretPosition

	float getMissileHorizontalOffset();//the offset in the turretYaw+90 direction from the turretPosition

	float getMissileVerticalOffset();//the offset in the y+ direction from the turretPosition

	float getTurretPosX();//the offset of the turret from 0,0 at vehicle rotation 0

	float getTurretPosY();//the offset of the turret from 0,0 at vehicle rotation 0

	float getTurretPosZ();//the offset of the turret from 0,0 at vehicle rotation 0

	float getRiderForwardsOffset();//the offset from 0,0 or turretPos of the rider

	float getRiderHorizontalOffset();//the offset from 0,0 or turretPos of the rider

	float getRiderVerticalOffest();//the offset from 0,0 or turretPos of the rider

	float getMinAttackDistance();//used by soldiers to determine when to get off a vehicle

	boolean shouldRiderSit();//should rider be seated while riding?

	boolean moveRiderWithTurret();//should position of rider update with the position of the turret, rather than vehicle?

	boolean isAmmoValidForInventory(IAmmo ammo);//does not determine if it can be fired, only if it can be placed into inventory

	boolean isUpgradeValid(IVehicleUpgradeType upgrade);

	boolean isArmorValid(IVehicleArmor armor);

	List<IAmmo> getValidAmmoTypes();

	List<IVehicleUpgradeType> getValidUpgrades();

	List<IVehicleArmor> getValidArmors();

	int getStorageBaySize();

	int getAmmoBaySize();

	int getArmorBaySize();

	int getUpgradeBaySize();

	float getMaxMissileWeight();//in KG--will be adjusted by material... any additional missile weight over this will reduce max launch speed by a ratio

	ItemStack getStackForLevel(int level);

	IAmmo getAmmoForSoldierRank(int rank);//what ammo type should soldiers use if !Config.soldiersUseAmmo

	String getIconTexture();

	VehicleFiringVarsHelper getFiringVarsHelper(VehicleBase veh);

	VehicleMovementType getMovementType();

	void setEnabled(boolean val);

	boolean isEnabled();//determined via config, used to add recipes and to loot tables

	void playReloadSound(VehicleBase vehicleBase);

	void playFiringSound(VehicleBase vehicleBase);

	boolean isEnabledForLoot();

	boolean isEnabledForCrafting();

	void setEnabledForLoot(boolean val);

	String getConfigName();//get the name used to load config settings for this vehicle
}
