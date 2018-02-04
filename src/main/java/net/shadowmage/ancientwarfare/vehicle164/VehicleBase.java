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

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetServerHandler;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.util.Trig;
import shadowmage.ancient_warfare.common.config.Config;
import shadowmage.ancient_warfare.common.interfaces.IEntityContainerSynch;
import shadowmage.ancient_warfare.common.interfaces.IMissileHitCallback;
import shadowmage.ancient_warfare.common.interfaces.IPathableEntity;
import shadowmage.ancient_warfare.common.inventory.VehicleInventory;
import shadowmage.ancient_warfare.common.network.Packet02Vehicle;
import shadowmage.ancient_warfare.common.npcs.NpcBase;
import shadowmage.ancient_warfare.common.pathfinding.Node;
import shadowmage.ancient_warfare.common.pathfinding.PathWorldAccess;
import shadowmage.ancient_warfare.common.pathfinding.PathWorldAccessEntity;
import shadowmage.ancient_warfare.common.pathfinding.navigator.Navigator;
import shadowmage.ancient_warfare.common.registry.VehicleRegistry;
import shadowmage.ancient_warfare.common.utils.ByteTools;
import shadowmage.ancient_warfare.common.utils.InventoryTools;
import shadowmage.ancient_warfare.common.utils.Pos3f;
import shadowmage.ancient_warfare.common.utils.ServerPerformanceMonitor;
import shadowmage.ancient_warfare.common.vehicles.VehicleVarHelpers.DummyVehicleHelper;
import shadowmage.ancient_warfare.common.vehicles.armors.IVehicleArmorType;
import shadowmage.ancient_warfare.common.vehicles.helpers.VehicleAmmoHelper;
import shadowmage.ancient_warfare.common.vehicles.helpers.VehicleFiringHelper;
import shadowmage.ancient_warfare.common.vehicles.helpers.VehicleFiringVarsHelper;
import shadowmage.ancient_warfare.common.vehicles.helpers.VehicleMoveHelper;
import shadowmage.ancient_warfare.common.vehicles.helpers.VehicleUpgradeHelper;
import shadowmage.ancient_warfare.common.vehicles.materials.IVehicleMaterial;
import shadowmage.ancient_warfare.common.vehicles.missiles.IAmmoType;
import shadowmage.ancient_warfare.common.vehicles.types.VehicleType;
import shadowmage.ancient_warfare.common.vehicles.upgrades.IVehicleUpgradeType;

import java.util.List;
import java.util.Random;

public class VehicleBase extends Entity implements IEntityAdditionalSpawnData, IMissileHitCallback, IEntityContainerSynch, IPathableEntity, IInventory {

	/**
	 * these are the current max stats.  set from setVehicleType().
	 * these are local cached bases, after application of material factors
	 * should not be altered at all after vehicle is first initialized
	 */
	public float baseForwardSpeed;
	public float baseStrafeSpeed;
	public float basePitchMin;
	public float basePitchMax;
	public float baseTurretRotationMax;
	public float baseLaunchSpeedMax;
	public float baseHealth = 100;
	public float baseAccuracy = 1.f;
	public float baseWeight = 1000;//kg
	public int baseReloadTicks = 100;
	public float baseGenericResist = 0.f;
	public float baseFireResist = 0.f;
	public float baseExplosionResist = 0.f;

	/**
	 * local current stats, fully updated and modified from upgrades/etc. should not be altered aside from
	 * upgrades/armor
	 */
	public float currentForwardSpeedMax = 0.42f;
	public float currentPitchSpeedMax = 2.f;
	public float currentStrafeSpeedMax = 2.0f;

	/**
	 * how many ticks is a reloadCycle, at current upgrade status?
	 * the currentReload status is stored in firingHelper
	 */
	public int currentReloadTicks = 100;
	public float currentTurretPitchMin = 0.f;
	public float currentTurretPitchMax = 90.f;
	public float currentLaunchSpeedPowerMax = 32.321f;
	public float currentGenericResist = 0.f;
	public float currentFireResist = 0.f;
	public float currentExplosionResist = 0.f;
	public float currentWeight = 1000.f;
	public float currentTurretPitchSpeed = 0.f;
	public float currentTurretYawSpeed = 0.f;
	public float currentAccuracy = 1.f;
	public float currentTurretRotationMax = 45.f;

	/**
	 * local variables, may be altered by input/etc...
	 */
	private float localVehicleHealth = 100;
	public float localTurretRotationHome = 0.f;
	public float localTurretRotation = 0.f;
	public float localTurretDestRot = 0.f;
	public float localTurretRotInc = 1.f;
	public float localTurretPitch = 45.f;
	public float localTurretDestPitch = 45.f;
	public float localTurretPitchInc = 1.f;
	public float localLaunchPower = 31.321f;

	/**
	 * set by move helper on movement update. used during client rendering to update
	 * wheel rotation and other movement speed based animations (airplanes use for prop,
	 * helicopter uses for main and tail rotors).
	 */
	public float wheelRotation = 0.f;
	public float wheelRotationPrev = 0.f;

	/**
	 * the team number this vehicle was assigned to
	 */
	public int teamNum = 0;

	/**
	 * used to determine if it should allow interaction (setup time on vehicle placement)
	 */
	private boolean isSettingUp = false;

	/**
	 * set client-side when incoming damage is taken
	 */
	public int hitAnimationTicks = 0;

	/**
	 * how many ticks until next move packet should be sent? Used when Client-side movement is enabled.
	 */
	public int moveUpdateTicks = 0;

	public NpcBase assignedRider = null;

	/**
	 * complex stat tracking helpers, move, ammo, upgrades, general stats
	 */
	public VehicleAmmoHelper ammoHelper;
	public VehicleUpgradeHelper upgradeHelper;
	//public VehicleMovementHelper moveHelper;
	public VehicleMoveHelper moveHelper;
	public VehicleFiringHelper firingHelper;
	public VehicleFiringVarsHelper firingVarsHelper;
	public VehicleInventory inventory;
	public Navigator nav;
	public PathWorldAccessEntity worldAccess;
	public IVehicleType vehicleType = VehicleRegistry.CATAPULT_STAND_FIXED;//set to dummy vehicle so it is never null...
	public int vehicleMaterialLevel = 0;//the current material level of this vehicle. should be read/set prior to calling updateBaseStats

	public VehicleBase(World par1World) {
		super(par1World);
		this.upgradeHelper = new VehicleUpgradeHelper(this);
		this.moveHelper = new VehicleMoveHelper(this);
		this.ammoHelper = new VehicleAmmoHelper(this);
		this.firingHelper = new VehicleFiringHelper(this);
		this.firingVarsHelper = new DummyVehicleHelper(this);
		this.inventory = new VehicleInventory(this);
		this.worldAccess = new PathWorldAccessEntity(par1World, this);
		this.nav = new Navigator(this);
		this.nav.setStuckCheckTicks(100);
		this.stepHeight = 1.12f;
		this.entityCollisionReduction = 0.9f;
		this.onGround = false;
	}

	public Random getRNG() {
		return this.rand;
	}

	@Override
	protected void entityInit() {
		this.dataWatcher.addObject(5, Byte.valueOf((byte) 0));//f in
		this.dataWatcher.addObject(6, Byte.valueOf((byte) 0));//s in
		this.dataWatcher.addObject(7, Integer.valueOf(100));
	}

	@Override
	public ItemStack getPickedResult(MovingObjectPosition target) {
		return this.vehicleType.getStackForLevel(vehicleMaterialLevel);
	}

	/**
	 * overriden to help with vision checks for vehicles
	 */
	@Override
	public float getEyeHeight() {
		return 1.6F;
	}

	private int getHealthClient() {
		return this.dataWatcher.getWatchableObjectInt(7);
	}

	public void setHealth(float health) {
		if (health > this.baseHealth) {
			health = this.baseHealth;
		}
		this.localVehicleHealth = health;
		if (!worldObj.isRemote) {
			this.dataWatcher.updateObject(7, Integer.valueOf((int) health));
		}
	}

	private float getHealthServer() {
		return this.localVehicleHealth;
	}

	public float getHealth() {
		if (this.worldObj.isRemote) {
			return this.getHealthClient();
		} else {
			return this.getHealthServer();
		}
	}

	public byte getForwardInput() {
		return (byte) this.dataWatcher.getWatchableObjectByte(5);
	}

	public byte getStrafeInput() {
		return (byte) this.dataWatcher.getWatchableObjectByte(6);
	}

	public void setForwardInput(byte in) {
		this.dataWatcher.updateObject(5, Byte.valueOf(in));
	}

	public void setStrafeInput(byte in) {
		this.dataWatcher.updateObject(6, Byte.valueOf(in));
	}

	public void setVehicleType(IVehicleType vehicle, int materialLevel) {
		this.vehicleType = vehicle;
		this.vehicleMaterialLevel = materialLevel;
		VehicleFiringVarsHelper help = vehicle.getFiringVarsHelper(this);
		if (help != null) {
			this.firingVarsHelper = help;
		}
		float width = vehicleType.getWidth();
		float height = vehicleType.getHeight();
		this.setSize(width, height);
		for (IAmmoType ammo : vehicleType.getValidAmmoTypes()) {
			this.ammoHelper.addUseableAmmo(ammo);
		}
		for (IVehicleUpgradeType up : this.vehicleType.getValidUpgrades()) {
			this.upgradeHelper.addValidUpgrade(up);
		}
		for (IVehicleArmorType armor : this.vehicleType.getValidArmors()) {
			this.upgradeHelper.addValidArmor(armor);
		}
		this.inventory.setInventorySizes(vehicle.getUpgradeBaySize(), vehicle.getAmmoBaySize(), vehicle.getArmorBaySize(), vehicle.getStorageBaySize());
		this.updateBaseStats();
		this.resetCurrentStats();

		if (this.localTurretPitch < this.currentTurretPitchMin) {
			this.localTurretPitch = this.currentTurretPitchMin;
		} else if (this.localTurretPitch > this.currentTurretPitchMax) {
			this.localTurretPitch = this.currentTurretPitchMax;
		}
		this.localLaunchPower = this.firingHelper.getAdjustedMaxMissileVelocity();
		if (!this.canAimRotate()) {
			this.localTurretRotation = this.rotationYaw;
		}
		this.nav.setCanGoOnLand(vehicleType.getMovementType() == VehicleMovementType.GROUND);
	}

	private int setupTicks = 0;

	public void setSetupState(boolean state, int ticks) {
		this.isSettingUp = state;
		if (state) {
			setupTicks = ticks;
		} else {
			setupTicks = 0;
		}
	}

	public void setInitialHealth() {
		this.setHealth(this.baseHealth);
	}

	public void updateBaseStats() {
		//  Config.logDebug("updating base stats. server"+!worldObj.isRemote);
		IVehicleMaterial material = vehicleType.getMaterialType();
		int level = this.vehicleMaterialLevel;
		baseForwardSpeed = vehicleType.getBaseForwardSpeed() * material.getSpeedForwardFactor(level);
		baseStrafeSpeed = vehicleType.getBaseStrafeSpeed() * material.getSpeedStrafeFactor(level);
		basePitchMin = vehicleType.getBasePitchMin();
		basePitchMax = vehicleType.getBasePitchMax();
		baseTurretRotationMax = vehicleType.getBaseTurretRotationAmount();
		baseLaunchSpeedMax = vehicleType.getBaseMissileVelocityMax();
		baseHealth = vehicleType.getBaseHealth() * material.getHPFactor(level);
		baseAccuracy = vehicleType.getBaseAccuracy() * material.getAccuracyFactor(level);
		baseWeight = vehicleType.getBaseWeight() * material.getWeightFactor(level);
		baseExplosionResist = 0.f;
		baseFireResist = 0.f;
		baseGenericResist = 0.f;
		if (getHealth() > baseHealth) {
			this.setHealth(baseHealth);
		}
	}

	@Override
	public void onCollideWithPlayer(EntityPlayer par1EntityPlayer) {
		super.onCollideWithPlayer(par1EntityPlayer);
		if (!worldObj.isRemote && par1EntityPlayer instanceof EntityPlayerMP && par1EntityPlayer.posY > posY && par1EntityPlayer.isCollidedVertically) {
			EntityPlayerMP player = (EntityPlayerMP) par1EntityPlayer;
			NetServerHandler serv = player.playerNetServerHandler;
			serv.ticksForFloatKick = 0;
		}
	}

	/**
	 * return an itemStack tagged appropriately for this vehicle
	 *
	 * @return
	 */
	public ItemStack getItemForVehicle() {
		ItemStack stack = this.vehicleType.getStackForLevel(vehicleMaterialLevel);
		stack.getTagCompound().getCompoundTag("AWVehSpawner").setFloat("health", getHealth());
		return stack;
	}

	/**
	 * used by soldiers to determine if they should try and 'drive' the engine anywhere
	 * (so that they won't try and turn stand-fixed varieties of vehicles)
	 *
	 * @return
	 */
	public boolean isMoveable() {
		return !this.isSettingUp && this.isDrivable() && this.currentForwardSpeedMax > 0;
	}

	public float getHorizontalMissileOffset() {
		return this.vehicleType.getMissileHorizontalOffset();
	}

	public float getVerticalMissileOffset() {
		return this.vehicleType.getMissileVerticalOffset();
	}

	public float getForwardsMissileOffset() {
		return this.vehicleType.getMissileForwardsOffset();
	}

	public boolean isAimable() {
		return !this.isSettingUp && vehicleType.isCombatEngine();
	}

	public boolean canAimRotate() {
		return !this.isSettingUp && vehicleType.canAdjustYaw();
	}

	public boolean canAimPitch() {
		return !this.isSettingUp && vehicleType.canAdjustPitch();
	}

	public boolean canAimPower() {
		return !this.isSettingUp && vehicleType.canAdjustPower();
	}

	/**
	 * used by inputHelper to determine if it should check movement input keys and send info to server..
	 *
	 * @return
	 */
	public boolean isDrivable() {
		return !this.isSettingUp && vehicleType.isDrivable();
	}

	public boolean isMountable() {
		return !this.isSettingUp && vehicleType.isMountable();
	}

	public float getRiderForwardOffset() {
		return vehicleType.getRiderForwardsOffset();
	}

	public float getRiderVerticalOffset() {
		return vehicleType.getRiderVerticalOffest();
	}

	public float getRiderHorizontalOffset() {
		return vehicleType.getRiderHorizontalOffset();
	}

	/**
	 * should return the maximum range allowed in order to hit a point at a given vertical offset
	 * will vary by vehicle type (power/angle/missile offset) and current ammo selection
	 * need to figure out....yah....
	 *
	 * @param verticalOffset
	 * @return
	 */
	public float getEffectiveRange(float verticalOffset) {
		if (vehicleType == VehicleRegistry.BATTERING_RAM)//TODO ugly hack...
		{
			return 5;
		}
		float angle;
		if (currentTurretPitchMin < 45 && currentTurretPitchMax > 45)//if the angle stradles 45, return 45
		{
			angle = 45;
		} else if (currentTurretPitchMin < 45 && currentTurretPitchMax < 45)//else if both are below 45, get the largest
		{
			angle = currentTurretPitchMax;
		} else {
			angle = currentTurretPitchMin;//else get the lowest
		}
		return Trig.getEffectiveRange(verticalOffset, angle, firingHelper.getAdjustedMaxMissileVelocity(), 0,
				ammoHelper.getCurrentAmmoType() != null && ammoHelper.getCurrentAmmoType().isRocket());
	}

	/**
	 * get a fully translated offset position for missile spawn for the current aim and vehicle params
	 *
	 * @return
	 */
	public Pos3f getMissileOffset() {
		Pos3f off = new Pos3f();
		float x1 = this.vehicleType.getTurretPosX();
		float y1 = this.vehicleType.getTurretPosY();
		float z1 = this.vehicleType.getTurretPosZ();
		float angle = 0;
		float len = 0;
		if (x1 != 0 || z1 != 0) {
			angle = Trig.toDegrees((float) Math.atan2(z1, x1));
			len = MathHelper.sqrt_float(x1 * x1 + z1 * z1);
			angle += this.rotationYaw;
			x1 = Trig.cosDegrees(angle) * len;
			z1 = -Trig.sinDegrees(angle) * len;
		}

		float x = this.getHorizontalMissileOffset();
		float y = this.getVerticalMissileOffset();
		float z = this.getForwardsMissileOffset();
		if (x != 0 || z != 0 || y != 0) {
			angle = Trig.toDegrees((float) Math.atan2(z, x));
			len = MathHelper.sqrt_float(x * x + z * z + y * y);
			angle += this.localTurretRotation;
			x = Trig.cosDegrees(angle) * Trig.sinDegrees(localTurretPitch + rotationPitch) * len;
			z = -Trig.sinDegrees(angle) * Trig.sinDegrees(localTurretPitch + rotationPitch) * len;
			y = Trig.cosDegrees(localTurretPitch + rotationPitch) * len;
		}
		x += x1;
		z += z1;
		y += y1;
		off.x = x;
		off.y = y;
		off.z = z;
		return off;
	}

	/**
	 * called on every tick that the vehicle is 'firing' to update the firing animation and to call
	 * launchMissile when animation has reached launch point
	 */
	public void onFiringUpdate() {
		this.firingVarsHelper.onFiringUpdate();
	}

	/**
	 * called every tick after the vehicle has fired, until reload timer is complete, to update animations
	 */
	public void onReloadUpdate() {
		this.firingVarsHelper.onReloadUpdate();
	}

	/**
	 * called every tick after startLaunching() is called, until setFinishedLaunching() is called...
	 */
	public void onLaunchingUpdate() {
		this.firingVarsHelper.onLaunchingUpdate();
	}

	/**
	 * reset all upgradeable stats back to the base for this vehicle
	 */
	public void resetCurrentStats() {
		this.firingHelper.resetUpgradeStats();
		this.currentForwardSpeedMax = this.baseForwardSpeed;
		this.currentStrafeSpeedMax = this.baseStrafeSpeed;
		this.currentTurretPitchMin = this.basePitchMin;
		this.currentTurretPitchMax = this.basePitchMax;
		this.currentTurretRotationMax = this.baseTurretRotationMax;
		this.currentReloadTicks = this.baseReloadTicks;
		this.currentLaunchSpeedPowerMax = this.baseLaunchSpeedMax;
		this.currentExplosionResist = this.baseExplosionResist;
		this.currentFireResist = this.baseFireResist;
		this.currentGenericResist = this.baseGenericResist;
		this.currentWeight = this.baseWeight;
		this.currentAccuracy = this.baseAccuracy;
	}

	@Override
	public void setDead() {
		if (!this.worldObj.isRemote && !this.isDead && this.getHealth() <= 0) {
			InventoryTools.dropInventoryInWorld(worldObj, inventory.ammoInventory, posX, posY, posZ);
			InventoryTools.dropInventoryInWorld(worldObj, inventory.armorInventory, posX, posY, posZ);
			InventoryTools.dropInventoryInWorld(worldObj, inventory.upgradeInventory, posX, posY, posZ);
			InventoryTools.dropInventoryInWorld(worldObj, inventory.storageInventory, posX, posY, posZ);
		}
		super.setDead();
	}

	@Override
	public void onUpdate() {
		long t1 = System.nanoTime();
		super.onUpdate();
		if (this.worldObj.isRemote) {
			this.onUpdateClient();
		} else {
			this.onUpdateServer();
		}
		this.updateTurretPitch();
		this.updateTurretRotation();
		this.moveHelper.onUpdate();
		this.firingHelper.onTick();
		this.firingVarsHelper.onTick();
		if (this.hitAnimationTicks > 0) {
			this.hitAnimationTicks--;
		}
		if (this.isSettingUp) {
			this.setupTicks--;
			if (this.setupTicks <= 0) {
				this.isSettingUp = false;
			}
		}
		if (this.assignedRider != null) {
			if (this.assignedRider.isDead || this.assignedRider.ridingEntity != this || this.assignedRider.wayNav
					.getMountTarget() == null || this.assignedRider.wayNav.getMountTarget() != this || this
					.getDistanceToEntity(assignedRider) > Config.npcAISearchRange) {
				this.assignedRider = null;
			}
		}
		ServerPerformanceMonitor.addVehicleTickTime(System.nanoTime() - t1);
	}

	/**
	 * client-side updates
	 */
	public void onUpdateClient() {
		if (this.localVehicleHealth != this.getHealth()) {
			if (localVehicleHealth > this.getHealth())//only play hit animation when attacked
			{
				this.hitAnimationTicks = 20;
			}
			this.localVehicleHealth = this.getHealth();
		}
		if (this.riddenByEntity instanceof NpcBase) {
			this.updateRiderPosition();
		}
	}

	/**
	 * server-side updates...
	 */
	public void onUpdateServer() {
		if (this.riddenByEntity instanceof EntityPlayerMP) {
			EntityPlayerMP player = (EntityPlayerMP) this.riddenByEntity;
			NetServerHandler serv = player.playerNetServerHandler;
			serv.ticksForFloatKick = 0;
			if (player.isSneaking()) {
				this.handleDismount(player);
				player.setSneaking(false);
			}
		}
	}

	public void handleDismount(EntityLivingBase rider) {
		int xMin = MathHelper.floor_double(this.posX - this.width / 2);
		int zMin = MathHelper.floor_double(this.posZ - this.width / 2);
		int yMin = MathHelper.floor_double(posY) - 2;
		boolean foundTarget = false;
		rider.mountEntity(null);
		searchLabel:
		for (int y = yMin; y <= yMin + 3; y++) {
			for (int x = xMin; x <= xMin + (int) width; x++) {
				for (int z = zMin; z <= zMin + (int) width; z++) {
					if (worldObj.doesBlockHaveSolidTopSurface(x, y, z) || this.worldObj.getBlockMaterial(x, y, z) == Material.water) {
						if (worldObj.isAirBlock(x, y + 1, z) && worldObj.isAirBlock(x, y + 2, z)) {
							rider.setPositionAndUpdate(x + 0.5d, y + 1, z + 0.5d);
							foundTarget = true;
							break searchLabel;
						}
					}
				}
			}
		}
	}

	public void updateTurretPitch() {
		float prevPitch = this.localTurretPitch;
		if (localTurretPitch < currentTurretPitchMin) {
			localTurretPitch = currentTurretPitchMin;
		} else if (localTurretPitch > currentTurretPitchMax) {
			localTurretPitch = currentTurretPitchMax;
		}
		if (localTurretDestPitch < currentTurretPitchMin) {
			localTurretDestPitch = currentTurretPitchMin;
		} else if (localTurretDestPitch > currentTurretPitchMax) {
			localTurretDestPitch = currentTurretPitchMax;
		}
		if (!canAimPitch()) {
			localTurretDestPitch = localTurretPitch;
		}
		if (localTurretPitch != localTurretDestPitch) {
			if (Trig.getAbsDiff(localTurretDestPitch, localTurretPitch) < localTurretPitchInc) {
				localTurretPitch = localTurretDestPitch;
			}
			if (localTurretPitch > localTurretDestPitch) {
				localTurretPitch -= localTurretPitchInc;
			} else if (localTurretPitch < localTurretDestPitch) {
				localTurretPitch += localTurretPitchInc;
			}
		}
		this.currentTurretPitchSpeed = prevPitch - this.localTurretPitch;
	}

	public void updateTurretRotation() {
		float prevYaw = this.localTurretRotation;
		this.localTurretRotationHome = Trig.wrapTo360(this.rotationYaw);
		if (!canAimRotate()) {
			localTurretRotation = Trig.wrapTo360(this.rotationYaw);
			localTurretDestRot = localTurretRotation;
		} else {
			//    localTurretRotation += moveHelper.strafeMotion;
		}
		if (Trig.getAbsDiff(localTurretDestRot, localTurretRotation) > localTurretRotInc) {
			while (localTurretRotation < 0) {
				localTurretRotation += 360;
				prevYaw += 360;
			}
			while (localTurretRotation >= 360) {
				localTurretRotation -= 360;
				prevYaw -= 360;
			}
			localTurretDestRot = Trig.wrapTo360(localTurretDestRot);
			byte turnDirection = 0;
			float curMod = localTurretRotation;
			float destMod = localTurretDestRot;
			float diff = curMod > destMod ? curMod - destMod : destMod - curMod;
			float turnDir = 0;
			if (curMod > destMod) {
				if (diff < 180) {
					turnDir = -1;
				} else {
					turnDir = 1;
				}
			} else if (curMod < destMod) {
				if (diff < 180) {
					turnDir = 1;
				} else {
					turnDir = -1;
				}
			}
			localTurretRotation += localTurretRotInc * turnDir;
		} else {
			localTurretRotation = localTurretDestRot;
		}
		if (Trig.getAbsDiff(localTurretDestRot, localTurretRotation) < localTurretRotInc) {
			localTurretRotation = localTurretDestRot;
		}
		this.currentTurretYawSpeed = this.localTurretRotation - prevYaw;
		if (this.currentTurretYawSpeed > 180) {
			this.currentTurretYawSpeed -= 360.f;
		}
		if (this.currentTurretYawSpeed < -180) {
			this.currentTurretYawSpeed += 360.f;
		}
	}

	/**
	 * Called from Packet02Vehicle
	 * Generic update method for client-server coms
	 *
	 * @param tag
	 */
	public void handlePacketUpdate(NBTTagCompound tag) {
		if (tag.hasKey("input")) {
			this.handleInputData(tag.getCompoundTag("input"));
		}
		if (tag.hasKey("upgrade")) {
			this.upgradeHelper.handleUpgradePacketData(tag.getCompoundTag("upgrade"));
		}
		if (tag.hasKey("ammo")) {
			this.ammoHelper.handleAmmoUpdatePacket(tag.getCompoundTag("ammo"));
		}
		if (tag.hasKey("ammoSel")) {
			this.ammoHelper.handleAmmoSelectPacket(tag.getCompoundTag("ammoSel"));
		}
		if (tag.hasKey("ammoUpd")) {
			this.ammoHelper.handleAmmoCountUpdate(tag.getCompoundTag("ammoUpd"));
		}
		if (tag.hasKey("pack")) {
			this.packVehicle();
		}
		if (tag.hasKey("turret")) {
			this.handleTurretPacket(tag.getCompoundTag("turret"));
		}
		if (tag.hasKey("moveData")) {
			this.moveHelper.handleMoveData(tag);
		}
	}

	public void sendCompleteTurretPacket() {
		if (this.worldObj.isRemote) {
			return;
		}
		NBTTagCompound tag = new NBTTagCompound();
		tag.setFloat("p", localTurretPitch);
		tag.setFloat("r", localTurretRotation);
		Packet02Vehicle pkt = new Packet02Vehicle();
		pkt.setTurretParams(tag);
		pkt.sendPacketToAllTrackingClients(this);
	}

	protected void handleTurretPacket(NBTTagCompound tag) {
		this.localTurretPitch = tag.getFloat("p");
		this.localTurretRotation = tag.getFloat("r");
		this.localTurretDestPitch = this.localTurretPitch;
		this.localTurretDestRot = this.localTurretRotation;
	}

	public void handleInputData(NBTTagCompound tag) {
		this.moveHelper.handleInputData(tag);
		this.firingHelper.handleInputData(tag);
	}

	/**
	 * spits out inventory into world, and packs the vehicle into an item, also spat into the world
	 */
	public void packVehicle() {
		if (!this.worldObj.isRemote) {
			worldObj.spawnEntityInWorld(new EntityItem(this.worldObj, posX, posY + 0.5d, posZ, this.getItemForVehicle()));
			InventoryTools.dropInventoryInWorld(worldObj, inventory.ammoInventory, posX, posY, posZ);
			InventoryTools.dropInventoryInWorld(worldObj, inventory.armorInventory, posX, posY, posZ);
			InventoryTools.dropInventoryInWorld(worldObj, inventory.upgradeInventory, posX, posY, posZ);
			InventoryTools.dropInventoryInWorld(worldObj, inventory.storageInventory, posX, posY, posZ);
			this.setDead();
		}
	}

	@Override
	public boolean attackEntityFrom(DamageSource par1DamageSource, float par2) {
		if (this.worldObj.isRemote) {
			return false;
		}
		super.attackEntityFrom(par1DamageSource, par2);
		float adjDmg = upgradeHelper.getScaledDamage(par1DamageSource, par2);
		this.setHealth(getHealth() - adjDmg);
		//  Config.logDebug("Vehicle hit by attack.  RawDamage: "+par2+" : adjustedDmg: "+adjDmg+"  New health: "+localVehicleHealth);
		if (this.getHealth() <= 0) {
			this.setDead();
			return false;
		}
		return true;
	}

	@Override
	public void applyEntityCollision(Entity par1Entity) {
		if (par1Entity != this.riddenByEntity && !(par1Entity instanceof NpcBase))//skip if it if it is the rider
		{
			double xDiff = par1Entity.posX - this.posX;
			double zDiff = par1Entity.posZ - this.posZ;
			double entityDistance = MathHelper.abs_max(xDiff, zDiff);

			if (entityDistance >= 0.009999999776482582D) {
				entityDistance = (double) MathHelper.sqrt_double(entityDistance);
				xDiff /= entityDistance;
				zDiff /= entityDistance;
				double normalizeToDistance = 1.0D / entityDistance;

				if (normalizeToDistance > 1.0D) {
					normalizeToDistance = 1.0D;
				}

				xDiff *= normalizeToDistance;
				zDiff *= normalizeToDistance;
				xDiff *= 0.05000000074505806D;//wtf..normalize to ticks?
				zDiff *= 0.05000000074505806D;
				xDiff *= (double) (1.0F - this.entityCollisionReduction);
				zDiff *= (double) (1.0F - this.entityCollisionReduction);
				this.addVelocity(-xDiff, 0.0D, -zDiff);
				par1Entity.addVelocity(xDiff, 0.0D, zDiff);
			}
		}
	}

	public String getTexture() {
		return vehicleType.getTextureForMaterialLevel(vehicleMaterialLevel);
	}

	@Override
	public void updateRiderPosition() {
		double posX = this.posX;
		double posY = this.posY + this.getRiderVerticalOffset();
		double posZ = this.posZ;
		if (this.riddenByEntity instanceof NpcBase) {
			posY -= 0.5f;
		}
		float yaw = this.vehicleType.moveRiderWithTurret() ? localTurretRotation : rotationYaw;
		posX += Trig.sinDegrees(yaw) * -this.getRiderForwardOffset();
		posX += Trig.sinDegrees(yaw + 90) * this.getRiderHorizontalOffset();
		posZ += Trig.cosDegrees(yaw) * -this.getRiderForwardOffset();
		posZ += Trig.cosDegrees(yaw + 90) * this.getRiderHorizontalOffset();
		this.riddenByEntity.setPosition(posX, posY + this.riddenByEntity.getYOffset(), posZ);
		this.riddenByEntity.rotationYaw -= this.moveHelper.getRotationSpeed();
	}

	@Override
	public boolean interactFirst(EntityPlayer player) {
		if (this.isSettingUp) {
			if (!player.worldObj.isRemote) {
				player.addChatMessage("Vehicle is currently being set-up.  It has " + setupTicks + " ticks remaining.");
			}
			return false;
		}
		return this.firingVarsHelper.interact(player);
	}

	@Override
	public String toString() {
		return String.format("%s::%s @ %.2f, %.2f, %.2f  -- y:%.2f p:%.2f -- m: %.2f, %.2f, %.2f", this.vehicleType.getDisplayName(), this.entityId, this.posX,
				this.posY, this.posZ, this.rotationYaw, this.rotationPitch, this.motionX, this.motionY, this.motionZ);
	}

	@Override
	public void setPositionAndRotation2(double par1, double par3, double par5, float yaw, float par8, int par9) {

	}

	@Override
	public void addVelocity(double par1, double par3, double par5) {
		super.addVelocity(par1, par3, par5);
	}

	@Override
	public void setVelocity(double par1, double par3, double par5) {

	}

	@Override
	public boolean shouldRiderSit() {
		return this.vehicleType.shouldRiderSit();
	}

	@Override
	public AxisAlignedBB getBoundingBox() {
		return this.boundingBox;
	}

	@Override
	public AxisAlignedBB getCollisionBox(Entity par1Entity) {
		//  return par1Entity.canBePushed() ? par1Entity.boundingBox : null;
		return par1Entity.getBoundingBox();
	}

	@Override
	public boolean canBeCollidedWith() {
		return true;
	}

	@Override
	public void writeSpawnData(ByteArrayDataOutput data) {
		data.writeFloat(this.getHealth());
		data.writeInt(this.vehicleType.getGlobalVehicleType());
		data.writeInt(this.vehicleMaterialLevel);
		ByteTools.writeNBTTagCompound(upgradeHelper.getNBTTag(), data);
		ByteTools.writeNBTTagCompound(ammoHelper.getNBTTag(), data);
		ByteTools.writeNBTTagCompound(moveHelper.getNBTTag(), data);
		ByteTools.writeNBTTagCompound(firingHelper.getNBTTag(), data);
		ByteTools.writeNBTTagCompound(firingVarsHelper.getNBTTag(), data);
		data.writeFloat(localLaunchPower);
		data.writeFloat(localTurretPitch);
		data.writeFloat(localTurretRotation);
		data.writeFloat(localTurretDestPitch);
		data.writeFloat(localTurretDestRot);
		data.writeInt(teamNum);
		data.writeFloat(localTurretRotationHome);
		data.writeBoolean(this.isSettingUp);
		if (this.isSettingUp) {
			data.writeInt(this.setupTicks);
		}
	}

	@Override
	public void readSpawnData(ByteArrayDataInput data) {
		this.setHealth(data.readFloat());
		IVehicleType type = VehicleType.getVehicleType(data.readInt());
		this.setVehicleType(type, data.readInt());
		this.upgradeHelper.readFromNBT(ByteTools.readNBTTagCompound(data));
		this.ammoHelper.readFromNBT(ByteTools.readNBTTagCompound(data));
		this.moveHelper.readFromNBT(ByteTools.readNBTTagCompound(data));
		this.firingHelper.readFromNBT(ByteTools.readNBTTagCompound(data));
		this.firingVarsHelper.readFromNBT(ByteTools.readNBTTagCompound(data));
		this.localLaunchPower = data.readFloat();
		this.localTurretPitch = data.readFloat();
		this.localTurretRotation = data.readFloat();
		this.localTurretDestPitch = data.readFloat();
		this.localTurretDestRot = data.readFloat();
		this.firingHelper.clientLaunchSpeed = localLaunchPower;
		this.firingHelper.clientTurretPitch = localTurretPitch;
		this.firingHelper.clientTurretYaw = localTurretRotation;
		this.upgradeHelper.updateUpgradeStats();
		this.teamNum = data.readInt();
		this.localTurretRotationHome = data.readFloat();
		this.isSettingUp = data.readBoolean();
		if (this.isSettingUp) {
			this.setupTicks = data.readInt();
		}
		this.setPosition(posX, posY, posZ);//this is to reset the bounding box, because the size of the entity changed during vehicleType setup
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound tag) {
		IVehicleType vehType = VehicleType.getVehicleType(tag.getInteger("vehType"));
		int level = tag.getInteger("matLvl");
		this.setVehicleType(vehType, level);
		this.setHealth(tag.getFloat("health"));
		this.localTurretRotationHome = tag.getFloat("turHome");
		this.inventory.readFromNBT(tag);
		this.upgradeHelper.readFromNBT(tag.getCompoundTag("upgrades"));
		this.ammoHelper.readFromNBT(tag.getCompoundTag("ammo"));
		this.moveHelper.readFromNBT(tag.getCompoundTag("move"));
		this.firingHelper.readFromNBT(tag.getCompoundTag("fire"));
		this.firingVarsHelper.readFromNBT(tag.getCompoundTag("vars"));
		this.localLaunchPower = tag.getFloat("lc");
		this.localTurretPitch = tag.getFloat("tp");
		this.localTurretDestPitch = tag.getFloat("tpd");
		this.localTurretRotation = tag.getFloat("tr");
		this.localTurretDestRot = tag.getFloat("trd");
		this.upgradeHelper.updateUpgrades();
		this.ammoHelper.updateAmmoCounts();
		this.teamNum = tag.getInteger("team");
		this.isSettingUp = tag.getBoolean("setup");
		if (this.isSettingUp) {
			this.setupTicks = tag.getInteger("sTick");
		}
		this.setPosition(posX, posY, posZ);//this is to reset the bounding box, because the size of the entity changed during vehicleType setup
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound tag) {
		tag.setInteger("vehType", this.vehicleType.getGlobalVehicleType());
		tag.setInteger("matLvl", this.vehicleMaterialLevel);
		tag.setFloat("health", this.getHealth());
		tag.setFloat("turHome", this.localTurretRotationHome);
		this.inventory.writeToNBT(tag);//yah..I wrote this one a long time ago, is why it is different.....
		tag.setCompoundTag("upgrades", this.upgradeHelper.getNBTTag());
		tag.setCompoundTag("ammo", this.ammoHelper.getNBTTag());
		tag.setCompoundTag("move", this.moveHelper.getNBTTag());
		tag.setCompoundTag("fire", this.firingHelper.getNBTTag());
		tag.setCompoundTag("vars", this.firingVarsHelper.getNBTTag());
		tag.setFloat("lc", localLaunchPower);
		tag.setFloat("tp", localTurretPitch);
		tag.setFloat("tpd", localTurretDestPitch);
		tag.setFloat("tr", localTurretRotation);
		tag.setFloat("trd", localTurretDestRot);
		tag.setInteger("team", this.teamNum);
		tag.setBoolean("setup", this.isSettingUp);
		if (this.isSettingUp) {
			tag.setInteger("sTick", this.setupTicks);
		}
	}

	/**
	 * missile callback methods...
	 */
	@Override
	public void onMissileImpact(World world, double x, double y, double z) {
		if (this.ridingEntity instanceof IMissileHitCallback) {
			((IMissileHitCallback) this.ridingEntity).onMissileImpact(world, x, y, z);
		}
	}

	@Override
	public void onMissileImpactEntity(World world, Entity entity) {
		if (this.ridingEntity instanceof IMissileHitCallback) {
			((IMissileHitCallback) this.ridingEntity).onMissileImpactEntity(world, entity);
		}
	}

	/**
	 * container sych methods
	 */
	@Override
	public void handleClientInput(NBTTagCompound tag) {

	}

	@Override
	public void addPlayer(EntityPlayer player) {

	}

	@Override
	public void removePlayer(EntityPlayer player) {

	}

	@Override
	public boolean canInteract(EntityPlayer player) {
		return true;
	}

	@Override
	public void setMoveTo(double x, double y, double z, float moveSpeed) {
		this.moveHelper.setMoveTo(x, y, z);
	}

	@Override
	public boolean isPathableEntityOnLadder() {
		return false;
	}

	@Override
	public Entity getEntity() {
		return this;
	}

	@Override
	public void setPath(List<Node> path) {
		this.nav.forcePath(path);
	}

	public void clearPath() {
		this.nav.clearPath();
	}

	@Override
	public PathWorldAccess getWorldAccess() {
		return worldAccess;
	}

	@Override
	public float getDefaultMoveSpeed() {
		return this.currentForwardSpeedMax;
	}

	@Override
	public void onStuckDetected() {
		if (this.riddenByEntity instanceof NpcBase) {
			((NpcBase) this.riddenByEntity).onStuckDetected();
		}
	}

	@Override
	public int getSizeInventory() {
		return inventory.storageInventory.getSizeInventory();
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		return inventory.storageInventory.getStackInSlot(i);
	}

	@Override
	public ItemStack decrStackSize(int i, int j) {
		return inventory.storageInventory.decrStackSize(i, j);
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int i) {
		return inventory.storageInventory.getStackInSlotOnClosing(i);
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		inventory.storageInventory.setInventorySlotContents(i, itemstack);
	}

	@Override
	public String getInvName() {
		return inventory.storageInventory.getInvName();
	}

	@Override
	public boolean isInvNameLocalized() {
		return inventory.storageInventory.isInvNameLocalized();
	}

	@Override
	public int getInventoryStackLimit() {
		return inventory.storageInventory.getInventoryStackLimit();
	}

	@Override
	public void onInventoryChanged() {
		inventory.storageInventory.onInventoryChanged();
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer) {
		return inventory.storageInventory.isUseableByPlayer(entityplayer);
	}

	@Override
	public void openChest() {

	}

	@Override
	public void closeChest() {

	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		return inventory.storageInventory.isItemValidForSlot(i, itemstack);
	}
}
