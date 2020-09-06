package net.shadowmage.ancientwarfare.vehicle.entity.types;

import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.vehicle.AncientWarfareVehicles;
import net.shadowmage.ancientwarfare.vehicle.armors.IVehicleArmor;
import net.shadowmage.ancientwarfare.vehicle.entity.IVehicleType;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleBase;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleMovementType;
import net.shadowmage.ancientwarfare.vehicle.entity.materials.IVehicleMaterial;
import net.shadowmage.ancientwarfare.vehicle.init.AWVehicleItems;
import net.shadowmage.ancientwarfare.vehicle.missiles.IAmmo;
import net.shadowmage.ancientwarfare.vehicle.upgrades.IVehicleUpgradeType;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * basically, a first-tier data construct class describing a vehicle.  Each vehicle will
 * reference one of these, where it will get its base stats from.  Mostly implemented to
 * reduce the amount of info sent to client for each vehicle created, and also to help
 * organize the manner and order in which vehicles will be described
 * first-tier--the vehicle itself
 * second-tier--the vehicle materials
 * third-tier--on-board vehicle upgrades
 *
 * @author Shadowmage
 */
public abstract class VehicleType implements IVehicleType {

	/**
	 * SELF-REGISTRY STUFF.....
	 */
	public static final IVehicleType[] vehicleTypes = new IVehicleType[1024];
	//private static HashMap<Integer, IVehicleType> vehicleTypes = new HashMap<Integer, IVehicleType>();

	/**
	 * INSTANCE VARIABLES......
	 */
	public float width = 2;
	public float height = 2;
	public float weight = 1000;//kg

	private final int vehicleType;
	public IVehicleMaterial vehicleMaterial = null;
	public boolean mountable = false;
	public boolean drivable = false;
	public boolean combatEngine = false;
	public boolean yawAdjustable = false;
	public boolean pitchAdjustable = false;
	public boolean powerAdjustable = false;
	public boolean pilotableBySoldiers = true;

	public float turretForwardsOffset = 0.f;
	public float turretHorizontalOffset = 0.f;
	public float turretVerticalOffset = 0.f;
	public float missileForwardsOffset = 0.f;
	public float missileHorizontalOffset = 0.f;
	public float missileVerticalOffset = 0.f;
	public float riderForwardsOffset = 0.f;
	public float riderHorizontalOffset = 0.f;
	public float riderVerticalOffset = 0.f;
	public boolean riderSits = true;
	public boolean riderMovesWithTurret = false;

	public float minAttackDistance = 5.f;

	public float baseForwardSpeed;
	public float baseStrafeSpeed;

	public float basePitchMin;
	public float basePitchMax;

	public float turretRotationMax;
	public float baseMissileVelocityMax;
	public float baseHealth = 100;

	public float maxMissileWeight = 10;

	public float accuracy = 1.f;

	public String displayName = "AWVehicleBase";
	public List<String> displayTooltip = new ArrayList<>();

	public List<IAmmo> validAmmoTypes = new ArrayList<>();
	public List<IVehicleUpgradeType> validUpgrades = new ArrayList<>();
	public List<IVehicleArmor> validArmors = new ArrayList<>();
	public Map<Integer, IAmmo> ammoBySoldierRank = new HashMap<>();

	int storageBaySize = 0;
	int ammoBaySize = 6;
	int upgradeBaySize = 3;
	int armorBaySize = 3;

	public int materialCount = 1;
	String iconTexture = "foo.png";
	protected String configName = "none";
	protected boolean enabled = true;
	protected boolean enabledForCrafting = true;
	protected boolean enabledForLoot = true;

	protected VehicleMovementType movementType = VehicleMovementType.GROUND;

	public VehicleType(int typeNum) {
		this.vehicleType = typeNum;
		vehicleTypes[typeNum] = this;
	}

	@Override
	public VehicleMovementType getMovementType() {
		return this.movementType;
	}

	@Override
	public IAmmo getAmmoForSoldierRank(int rank) {
		if (this.ammoBySoldierRank.containsKey(rank)) {
			return this.ammoBySoldierRank.get(rank);
		} else {
			List<IAmmo> ammos = this.getValidAmmoTypes();
			if (!ammos.isEmpty()) {
				return ammos.get(0);
			}
		}
		return null;
	}

	@Override
	public boolean canSoldiersPilot() {
		return this.pilotableBySoldiers;
	}

	@Override
	public void setEnabled(boolean val) {
		this.enabled = val;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	@Override
	public String getConfigName() {
		return configName;
	}

	@Override
	public int getGlobalVehicleType() {
		return this.vehicleType;
	}

	@Override
	public IVehicleMaterial getMaterialType() {
		return this.vehicleMaterial;
	}

	@Override
	public boolean isMountable() {
		return this.mountable;
	}

	@Override
	public boolean isDrivable() {
		return this.drivable;
	}

	@Override
	public boolean isCombatEngine() {
		return this.combatEngine;
	}

	@Override
	public boolean canAdjustYaw() {
		return this.yawAdjustable;
	}

	@Override
	public boolean canAdjustPitch() {
		return pitchAdjustable;
	}

	@Override
	public boolean canAdjustPower() {
		return powerAdjustable;
	}

	@Override
	public float getRiderForwardsOffset() {
		return this.riderForwardsOffset;
	}

	@Override
	public float getRiderHorizontalOffset() {
		return this.riderHorizontalOffset;
	}

	@Override
	public float getRiderVerticalOffest() {
		return this.riderVerticalOffset;
	}

	@Override
	public float getMinAttackDistance() {
		return this.minAttackDistance;
	}

	@Override
	public float getBaseForwardSpeed() {
		return this.baseForwardSpeed;
	}

	@Override
	public float getBaseStrafeSpeed() {
		return this.baseStrafeSpeed;
	}

	@Override
	public float getBasePitchMin() {
		return this.basePitchMin;
	}

	@Override
	public float getBasePitchMax() {
		return this.basePitchMax;
	}

	@Override
	public float getBaseHealth() {
		return this.baseHealth;
	}

	@Override
	public float getWidth() {
		return this.width;
	}

	@Override
	public float getHeight() {
		return this.height;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getLocalizedName() {
		return I18n.format(this.getDisplayName() + ".name");
	}

	@Override
	public ResourceLocation getTextureForMaterialLevel(int level) {
		return new ResourceLocation(AncientWarfareVehicles.MOD_ID, "foo");
	}

	@Override
	public float getMissileForwardsOffset() {
		return this.missileForwardsOffset;
	}

	@Override
	public float getMissileHorizontalOffset() {
		return this.missileHorizontalOffset;
	}

	@Override
	public float getMissileVerticalOffset() {
		return this.missileVerticalOffset;
	}

	@Override
	public float getBaseWeight() {
		return this.weight;
	}

	@Override
	public float getBaseTurretRotationAmount() {
		return this.turretRotationMax;
	}

	@Override
	public float getBaseMissileVelocityMax() {
		return this.baseMissileVelocityMax;
	}

	@Override
	public boolean isAmmoValidForInventory(IAmmo ammo) {
		return this.validAmmoTypes.contains(ammo);
	}

	@Override
	public boolean isUpgradeValid(IVehicleUpgradeType upgrade) {
		return this.validUpgrades.contains(upgrade);
	}

	@Override
	public float getBaseAccuracy() {
		return this.accuracy;
	}

	@Override
	public List<IAmmo> getValidAmmoTypes() {
		return this.validAmmoTypes;
	}

	@Override
	public List<IVehicleUpgradeType> getValidUpgrades() {
		return this.validUpgrades;
	}

	@Override
	public boolean isArmorValid(IVehicleArmor armor) {
		return this.validArmors.contains(armor);
	}

	@Override
	public List<IVehicleArmor> getValidArmors() {
		return this.validArmors;
	}

	@Override
	public String getDisplayName() {
		return displayName;
	}

	@Override
	public List<String> getDisplayTooltip() {
		return displayTooltip;
	}

	@Override
	public ItemStack getStackForLevel(int level) {
		ItemStack stack = new ItemStack(AWVehicleItems.SPAWNER, 1, this.getGlobalVehicleType());
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("level", level);
		stack.setTagInfo("spawnData", tag);
		return stack;
	}

	@Override
	public int getStorageBaySize() {
		return this.storageBaySize;
	}

	@Override
	public int getAmmoBaySize() {
		return this.ammoBaySize;
	}

	@Override
	public int getArmorBaySize() {
		return this.armorBaySize;
	}

	@Override
	public int getUpgradeBaySize() {
		return this.upgradeBaySize;
	}

	@Override
	public float getMaxMissileWeight() {
		return this.maxMissileWeight;
	}

	@Override
	public boolean shouldRiderSit() {
		return this.riderSits;
	}

	/********************************REGISTRY METHODS********************************/

	public static IVehicleType getVehicleType(int num) {
		if (num >= 0 && num < vehicleTypes.length) {
			return vehicleTypes[num];
		}
		return null;
	}

	public static Optional<VehicleBase> getVehicleForType(@Nullable World world, int type, int level) {
		if (world != null && type >= 0 && type < vehicleTypes.length && vehicleTypes[type] != null && vehicleTypes[type].isEnabled()) {
			IVehicleType vehType = getVehicleType(type);
			VehicleBase vehicle = new VehicleBase(world);
			vehicle.setVehicleType(vehType, level);
			vehicle.setInitialHealth();
			return Optional.of(vehicle);
		}
		return Optional.empty();
	}

	private static List<ItemStack> displayItemCache = null;

	public static List<ItemStack> getCreativeDisplayItems() {
		if (displayItemCache != null) {
			return displayItemCache;
		}
		List<ItemStack> stacks = new ArrayList<ItemStack>();
		ItemStack stack;
		for (IVehicleType type : vehicleTypes) {
			if (type == null || type.getMaterialType() == null || !type.isEnabled()) {
				continue;
			}
			for (int i = 0; i < type.getMaterialType().getNumOfLevels(); i++) {
				stack = new ItemStack(AWVehicleItems.SPAWNER, 1, type.getGlobalVehicleType());
				NBTTagCompound tag = new NBTTagCompound();
				tag.setInteger("level", i);
				stack.setTagInfo("spawnData", tag);
				stacks.add(stack);
			}
		}
		displayItemCache = stacks;
		return stacks;
	}

	@Override
	public boolean moveRiderWithTurret() {
		return this.riderMovesWithTurret;
	}

	@Override
	public float getTurretPosX() {
		return turretHorizontalOffset;
	}

	@Override
	public float getTurretPosY() {
		return turretVerticalOffset;
	}

	@Override
	public float getTurretPosZ() {
		return turretForwardsOffset;
	}

	@Override
	public String getIconTexture() {
		return "ancientwarfare:vehicle/" + iconTexture;
	}

	@Override
	public String toString() {
		return "AWVehicleType: " + this.displayName;
	}

	@Override
	public void setBaseHealth(float val) {
		if (val < 0) {
			val = 0;
		}
		this.baseHealth = val;
	}

	@Override
	public void setBaseForwardSpeed(float val) {
		if (val < 0) {
			val = 0;
		}
		this.baseForwardSpeed = val;
	}

	@Override
	public void setBaseStrafeSpeed(float val) {
		if (val < 0) {
			val = 0;
		}
		this.baseStrafeSpeed = val;
	}

	@Override
	public void setBasePitchMin(float val) {
		if (val > 90) {
			val = 90;
		}
		if (val < 0) {
			val = 0;
		}
		this.basePitchMin = val;
	}

	@Override
	public void setBasePitchMax(float val) {
		if (val > 90) {
			val = 90;
		}
		if (val < 0) {
			val = 0;
		}
		this.basePitchMax = val;
	}

	@Override
	public void setBaseTurretRotationAmount(float val) {
		if (val > 180) {
			val = 180.f;
		}
		if (val < 0) {
			val = 0.f;
		}
		this.turretRotationMax = val;
	}

	@Override
	public void setBaseMissileVelocity(float val) {
		if (val < 0) {
			val = 0;
		}
		this.baseMissileVelocityMax = val;
	}

	@Override
	public void setBaseAccuracy(float val) {
		if (val < 0) {
			val = 0.f;
		}
		if (val > 1.f) {
			val = 1.f;
		}
		this.accuracy = val;
	}

	@Override
	public boolean isEnabledForLoot() {
		return this.enabledForLoot;
	}

	@Override
	public boolean isEnabledForCrafting() {
		return this.enabledForCrafting;
	}

	@Override
	public void setEnabledForLoot(boolean val) {
		this.enabledForLoot = val;
	}

	@Override
	public void playReloadSound(VehicleBase vehicleBase) {}

	@Override
	public void playFiringSound(VehicleBase vehicleBase) {}
}
