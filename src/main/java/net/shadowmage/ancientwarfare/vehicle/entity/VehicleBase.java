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

package net.shadowmage.ancientwarfare.vehicle.entity;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.items.CapabilityItemHandler;
import net.shadowmage.ancientwarfare.core.interfaces.IOwnable;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;
import net.shadowmage.ancientwarfare.core.util.Trig;
import net.shadowmage.ancientwarfare.npc.config.AWNPCStatics;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;
import net.shadowmage.ancientwarfare.vehicle.AncientWarfareVehicles;
import net.shadowmage.ancientwarfare.vehicle.VehicleVarHelpers.DummyVehicleHelper;
import net.shadowmage.ancientwarfare.vehicle.armors.IVehicleArmor;
import net.shadowmage.ancientwarfare.vehicle.entity.materials.IVehicleMaterial;
import net.shadowmage.ancientwarfare.vehicle.entity.types.VehicleType;
import net.shadowmage.ancientwarfare.vehicle.helpers.VehicleAmmoHelper;
import net.shadowmage.ancientwarfare.vehicle.helpers.VehicleFiringHelper;
import net.shadowmage.ancientwarfare.vehicle.helpers.VehicleFiringVarsHelper;
import net.shadowmage.ancientwarfare.vehicle.helpers.VehicleMoveHelper;
import net.shadowmage.ancientwarfare.vehicle.helpers.VehicleUpgradeHelper;
import net.shadowmage.ancientwarfare.vehicle.inventory.VehicleInventory;
import net.shadowmage.ancientwarfare.vehicle.missiles.AmmoHwachaRocket;
import net.shadowmage.ancientwarfare.vehicle.missiles.IAmmo;
import net.shadowmage.ancientwarfare.vehicle.network.PacketTurretAnglesUpdate;
import net.shadowmage.ancientwarfare.vehicle.pathing.Navigator;
import net.shadowmage.ancientwarfare.vehicle.pathing.Node;
import net.shadowmage.ancientwarfare.vehicle.pathing.PathWorldAccessEntity;
import net.shadowmage.ancientwarfare.vehicle.registry.VehicleRegistry;
import net.shadowmage.ancientwarfare.vehicle.upgrades.IVehicleUpgradeType;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class VehicleBase extends Entity implements IEntityAdditionalSpawnData, IMissileHitCallback, IEntityContainerSynch, IPathableEntity, IOwnable {

	private static final DataParameter<Float> VEHICLE_HEALTH = EntityDataManager.createKey(VehicleBase.class, DataSerializers.FLOAT);
	private static final DataParameter<Byte> FORWARD_INPUT = EntityDataManager.createKey(VehicleBase.class, DataSerializers.BYTE);
	private static final DataParameter<Byte> STRAFE_INPUT = EntityDataManager.createKey(VehicleBase.class, DataSerializers.BYTE);

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
	private String ownerName = "";
	private UUID ownerUuid = new UUID(0, 0);

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
		dataManager.register(VEHICLE_HEALTH, 100f);
		dataManager.register(FORWARD_INPUT, (byte) 0);
		dataManager.register(STRAFE_INPUT, (byte) 0);
	}

	@Override
	public ItemStack getPickedResult(RayTraceResult target) {
		return this.vehicleType.getStackForLevel(vehicleMaterialLevel);
	}

	/**
	 * overriden to help with vision checks for vehicles
	 */
	@Override
	public float getEyeHeight() {
		return 1.6F;
	}

	public void setHealth(float health) {
		if (health > this.baseHealth) {
			health = this.baseHealth;
		}
		dataManager.set(VEHICLE_HEALTH, health);
	}

	public float getHealth() {
		return dataManager.get(VEHICLE_HEALTH);
	}

	public byte getForwardInput() {
		return dataManager.get(FORWARD_INPUT);
	}

	public byte getStrafeInput() {
		return dataManager.get(STRAFE_INPUT);
	}

	public void setForwardInput(byte in) {
		dataManager.set(FORWARD_INPUT, in);
	}

	public void setStrafeInput(byte in) {
		dataManager.set(STRAFE_INPUT, in);
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
		for (IAmmo ammo : vehicleType.getValidAmmoTypes()) {
			this.ammoHelper.addUseableAmmo(ammo);
		}
		for (IVehicleUpgradeType up : this.vehicleType.getValidUpgrades()) {
			this.upgradeHelper.addValidUpgrade(up);
		}
		for (IVehicleArmor armor : this.vehicleType.getValidArmors()) {
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
		//  Config.logDebug("updating base stats. server"+!world.isRemote);
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
		if (!world.isRemote && par1EntityPlayer instanceof EntityPlayerMP && par1EntityPlayer.posY > posY && par1EntityPlayer.isCollidedVertically) {
			EntityPlayerMP player = (EntityPlayerMP) par1EntityPlayer;
/*			TODO handle collision with players to allow them flying and disallow once they stop colliding
			probably a collection of players in collision with this entity, on update check if they have collided in the last 10? ticks and if not disallow flying
			player.capabilities.allowFlying = true;
*/
		}
	}

	/**
	 * return an itemStack tagged appropriately for this vehicle
	 *
	 * @return
	 */
	public ItemStack getItemForVehicle() {
		ItemStack stack = this.vehicleType.getStackForLevel(vehicleMaterialLevel);
		stack.getTagCompound().getCompoundTag("spawnData").setFloat("health", getHealth());
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
		return getEffectiveRange(verticalOffset, angle, firingHelper.getAdjustedMaxMissileVelocity(), 0, ammoHelper.getCurrentAmmoType() != null && ammoHelper.getCurrentAmmoType().isRocket());
	}

	private float getEffectiveRange(float y, float angle, float velocity, int maxIterations, boolean rocket) {
		float motX = Trig.sinDegrees(angle) * velocity * 0.05f;
		float motY = Trig.cosDegrees(angle) * velocity * 0.05f;
		float rocketX = 0;
		float rocketY = 0;
		if (rocket) {
			int rocketBurnTime = (int) (velocity * AmmoHwachaRocket.burnTimeFactor);
			float motX0 = (motX / (velocity * 0.05f)) * AmmoHwachaRocket.accelerationFactor;
			float motY0 = (motY / (velocity * 0.05f)) * AmmoHwachaRocket.accelerationFactor;
			motX = motX0;
			motY = motY0;
			while (rocketBurnTime > 0) {
				rocketX += motX;
				rocketY += motY;
				rocketBurnTime--;
				motX += motX0;
				motY += motY0;
			}
			y -= rocketY;
		}
		motX *= 20.f;
		motY *= 20.f;
		float gravity = 9.81f;
		float t = motY / gravity;
		float tQ = MathHelper.sqrt(((motY * motY) / (gravity * gravity)) - ((2 * y) / gravity));
		float tPlus = t + tQ;
		float tMinus = t - tQ;
		t = tPlus > tMinus ? tPlus : tMinus;
		return (motX * t) + rocketX;
	}

	/**
	 * get a fully translated offset position for missile spawn for the current aim and vehicle params
	 *
	 * @return
	 */
	public Vec3d getMissileOffset() {
		float x1 = this.vehicleType.getTurretPosX();
		float y1 = this.vehicleType.getTurretPosY();
		float z1 = this.vehicleType.getTurretPosZ();
		float angle = 0;
		float len = 0;
		if (x1 != 0 || z1 != 0) {
			angle = Trig.toDegrees((float) Math.atan2(z1, x1));
			len = MathHelper.sqrt(x1 * x1 + z1 * z1);
			angle += this.rotationYaw;
			x1 = Trig.cosDegrees(angle) * len;
			z1 = -Trig.sinDegrees(angle) * len;
		}

		float x = this.getHorizontalMissileOffset();
		float y = this.getVerticalMissileOffset();
		float z = this.getForwardsMissileOffset();
		if (x != 0 || z != 0 || y != 0) {
			angle = Trig.toDegrees((float) Math.atan2(z, x));
			len = MathHelper.sqrt(x * x + z * z + y * y);
			angle += this.localTurretRotation;
			x = Trig.cosDegrees(angle) * Trig.sinDegrees(localTurretPitch + rotationPitch) * len;
			z = -Trig.sinDegrees(angle) * Trig.sinDegrees(localTurretPitch + rotationPitch) * len;
			y = Trig.cosDegrees(localTurretPitch + rotationPitch) * len;
		}
		x += x1;
		z += z1;
		y += y1;
		Vec3d off = new Vec3d(x, y, z);
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
		if (!this.world.isRemote && !this.isDead && this.getHealth() <= 0) {
			InventoryTools.dropItemsInWorld(world, inventory.ammoInventory, posX, posY, posZ);
			InventoryTools.dropItemsInWorld(world, inventory.armorInventory, posX, posY, posZ);
			InventoryTools.dropItemsInWorld(world, inventory.upgradeInventory, posX, posY, posZ);
			InventoryTools.dropItemsInWorld(world, inventory.storageInventory, posX, posY, posZ);
		}
		super.setDead();
	}

	@Nullable
	@Override
	public Entity getControllingPassenger() {
		return getPassengers().isEmpty() ? null : getPassengers().get(0);
	}

	@Override
	public void onUpdate() {
		long t1 = System.nanoTime();
		super.onUpdate();
		if (this.world.isRemote) {
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
			if (assignedRider.isDead || assignedRider.getRidingEntity() != this || !assignedRider.isRiding() || assignedRider.getRidingEntity() != this || (this.getDistanceToEntity(assignedRider) > (AWNPCStatics.npcActionRange * AWNPCStatics.npcActionRange))) {
				//TODO config setting for vehicle search range
				this.assignedRider = null;
			}
		}
/* TODO perf test vehicles
		ServerPerformanceMonitor.addVehicleTickTime(System.nanoTime() - t1);
*/
	}

	/**
	 * client-side updates
	 */
	public void onUpdateClient() {
		if (getControllingPassenger() instanceof NpcBase) {
			this.updatePassenger(getControllingPassenger());
		}
	}

	@Override
	public boolean startRiding(Entity entity, boolean force) {
		if (super.startRiding(entity, force)) {
			if (entity instanceof EntityPlayerMP) {
				EntityPlayerMP player = (EntityPlayerMP) entity;
				player.capabilities.allowFlying = true;
			}
			return true;
		}
		return false;
	}

	/**
	 * server-side updates...
	 */
	public void onUpdateServer() {
		if (this.getControllingPassenger() instanceof EntityPlayerMP) {
			EntityPlayerMP player = (EntityPlayerMP) this.getControllingPassenger();
			if (player.isSneaking()) {
				this.handleDismount(player);
				player.setSneaking(false);
			}
		}
	}

	public void handleDismount(EntityLivingBase rider) {
		int xMin = MathHelper.floor(this.posX - this.width / 2);
		int zMin = MathHelper.floor(this.posZ - this.width / 2);
		int yMin = MathHelper.floor(posY) - 2;
		boolean foundTarget = false;

		if (rider instanceof EntityPlayerMP) {
			((EntityPlayerMP) rider).capabilities.allowFlying = false;
		}
		rider.dismountRidingEntity();

		searchLabel:
		for (int y = yMin; y <= yMin + 3; y++) {
			for (int x = xMin; x <= xMin + (int) width; x++) {
				for (int z = zMin; z <= zMin + (int) width; z++) {
					BlockPos pos = new BlockPos(x, y, z);
					IBlockState state = world.getBlockState(pos);
					if (state.isSideSolid(world, pos, EnumFacing.UP) || state.getMaterial() == Material.WATER) {
						if (world.isAirBlock(pos.up()) && world.isAirBlock(pos.up().up())) {
							rider.setPositionAndUpdate(x + 0.5d, y + 1, z + 0.5d);
							foundTarget = true; //TODO what is this supposed to do?
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
			if (Math.abs(localTurretDestPitch - localTurretPitch) < localTurretPitchInc) {
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
		if (Math.abs(localTurretDestRot - localTurretRotation) > localTurretRotInc) {
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
		if (Math.abs(localTurretDestRot - localTurretRotation) < localTurretRotInc) {
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

	public void sendCompleteTurretPacket() {
		if (this.world.isRemote) {
			return;
		}
		NetworkHandler.sendToAllTracking(this, new PacketTurretAnglesUpdate(this, localTurretPitch, localTurretRotation));
	}

	public void updateTurretAngles(float pitch, float rotation) {
		this.localTurretPitch = pitch;
		this.localTurretRotation = rotation;
		this.localTurretDestPitch = this.localTurretPitch;
		this.localTurretDestRot = this.localTurretRotation;
	}

	/**
	 * spits out inventory into world, and packs the vehicle into an item, also spat into the world
	 */
	public void packVehicle() {
		if (!this.world.isRemote) {
			InventoryTools.dropItemInWorld(world, getItemForVehicle(), posX, posY, posZ);
			InventoryTools.dropItemsInWorld(world, inventory.ammoInventory, posX, posY, posZ);
			InventoryTools.dropItemsInWorld(world, inventory.armorInventory, posX, posY, posZ);
			InventoryTools.dropItemsInWorld(world, inventory.upgradeInventory, posX, posY, posZ);
			InventoryTools.dropItemsInWorld(world, inventory.storageInventory, posX, posY, posZ);
			this.setDead();
		}
	}

	@Override
	public boolean attackEntityFrom(DamageSource damageSource, float amount) {
		if (this.world.isRemote) {
			hitAnimationTicks = 20;
			return false;
		}
		super.attackEntityFrom(damageSource, amount);
		float adjDmg = upgradeHelper.getScaledDamage(damageSource, amount);
		this.setHealth(getHealth() - adjDmg);
		if (getHealth() <= 0) {
			setDead();
			return false;
		}
		return true;
	}

	@Override
	public void applyEntityCollision(Entity entity) {
		if (entity != getControllingPassenger() && !(entity instanceof NpcBase))//skip if it if it is the rider
		{
			double xDiff = entity.posX - this.posX;
			double zDiff = entity.posZ - this.posZ;
			double entityDistance = MathHelper.absMax(xDiff, zDiff);

			if (entityDistance >= 0.009999999776482582D) {
				entityDistance = Math.sqrt(entityDistance);
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
				entity.addVelocity(xDiff, 0.0D, zDiff);
			}
		}
	}

	public ResourceLocation getTexture() {
		return vehicleType.getTextureForMaterialLevel(vehicleMaterialLevel);
	}

	@Override
	public void updatePassenger(Entity passenger) {
		double posX = this.posX;
		double posY = this.posY + this.getRiderVerticalOffset();
		double posZ = this.posZ;

		float yaw = this.vehicleType.moveRiderWithTurret() ? localTurretRotation : rotationYaw;
		posX += Trig.sinDegrees(yaw) * -this.getRiderForwardOffset();
		posX += Trig.sinDegrees(yaw + 90) * this.getRiderHorizontalOffset();
		posZ += Trig.cosDegrees(yaw) * -this.getRiderForwardOffset();
		posZ += Trig.cosDegrees(yaw + 90) * this.getRiderHorizontalOffset();
		passenger.setPosition(posX, posY + passenger.getYOffset(), posZ);
		passenger.rotationYaw -= this.moveHelper.getRotationSpeed();
	}

	@Override
	public boolean processInitialInteract(EntityPlayer player, EnumHand hand) {
		if (this.isSettingUp) {
			if (!player.world.isRemote) {
				player.sendMessage(new TextComponentString("Vehicle is currently being set-up.  It has " + setupTicks + " ticks remaining."));
			}
			return false;
		}
		return this.firingVarsHelper.interact(player);
	}

	@Override
	public String toString() {
		return String.format("%s::%s @ %.2f, %.2f, %.2f  -- y:%.2f p:%.2f -- m: %.2f, %.2f, %.2f", this.vehicleType.getDisplayName(), this.getEntityId(), this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch, this.motionX, this.motionY, this.motionZ);
	}

	@Override
	public void setPositionAndRotationDirect(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean teleport) {

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

	@Nullable
	@Override
	public AxisAlignedBB getCollisionBoundingBox() {
		return getEntityBoundingBox();
	}

	@Override
	public boolean canBeCollidedWith() {
		return true;
	}

	@Override
	public void writeSpawnData(ByteBuf buffer) {
		PacketBuffer pb = new PacketBuffer(buffer);
		pb.writeFloat(this.getHealth());
		pb.writeInt(this.vehicleType.getGlobalVehicleType());
		pb.writeInt(this.vehicleMaterialLevel);
		pb.writeCompoundTag(upgradeHelper.serializeNBT());
		pb.writeCompoundTag(ammoHelper.serializeNBT());
		pb.writeCompoundTag(moveHelper.serializeNBT());
		pb.writeCompoundTag(firingHelper.serializeNBT());
		pb.writeCompoundTag(firingVarsHelper.serializeNBT());
		pb.writeFloat(localLaunchPower);
		pb.writeFloat(localTurretPitch);
		pb.writeFloat(localTurretRotation);
		pb.writeFloat(localTurretDestPitch);
		pb.writeFloat(localTurretDestRot);
		pb.writeString(ownerName);
		pb.writeUniqueId(ownerUuid);
		pb.writeFloat(localTurretRotationHome);
		pb.writeBoolean(this.isSettingUp);
		if (this.isSettingUp) {
			pb.writeInt(this.setupTicks);
		}
	}

	@Override
	public void readSpawnData(ByteBuf additionalData) {
		PacketBuffer pb = new PacketBuffer(additionalData);
		this.setHealth(pb.readFloat());
		IVehicleType type = VehicleType.getVehicleType(pb.readInt());
		this.setVehicleType(type, pb.readInt());
		try {
			this.upgradeHelper.deserializeNBT(pb.readCompoundTag());
			this.ammoHelper.deserializeNBT(pb.readCompoundTag());
			this.moveHelper.deserializeNBT(pb.readCompoundTag());
			this.firingHelper.deserializeNBT(pb.readCompoundTag());
			this.firingVarsHelper.deserializeNBT(pb.readCompoundTag());
		}
		catch (IOException e) {
			AncientWarfareVehicles.log.error(e);
		}
		this.localLaunchPower = pb.readFloat();
		this.localTurretPitch = pb.readFloat();
		this.localTurretRotation = pb.readFloat();
		this.localTurretDestPitch = pb.readFloat();
		this.localTurretDestRot = pb.readFloat();
		this.firingHelper.clientLaunchSpeed = localLaunchPower;
		this.firingHelper.clientTurretPitch = localTurretPitch;
		this.firingHelper.clientTurretYaw = localTurretRotation;
		this.upgradeHelper.updateUpgradeStats();
		this.ownerName = pb.readString(16);
		this.ownerUuid = pb.readUniqueId();
		this.localTurretRotationHome = pb.readFloat();
		this.isSettingUp = pb.readBoolean();
		if (this.isSettingUp) {
			this.setupTicks = pb.readInt();
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
		this.upgradeHelper.deserializeNBT(tag.getCompoundTag("upgrades"));
		this.ammoHelper.deserializeNBT(tag.getCompoundTag("ammo"));
		this.moveHelper.deserializeNBT(tag.getCompoundTag("move"));
		this.firingHelper.deserializeNBT(tag.getCompoundTag("fire"));
		this.firingVarsHelper.deserializeNBT(tag.getCompoundTag("vars"));
		this.localLaunchPower = tag.getFloat("lc");
		this.localTurretPitch = tag.getFloat("tp");
		this.localTurretDestPitch = tag.getFloat("tpd");
		this.localTurretRotation = tag.getFloat("tr");
		this.localTurretDestRot = tag.getFloat("trd");
		this.upgradeHelper.updateUpgrades();
		this.ammoHelper.updateAmmoCounts();
		this.ownerName = tag.getString("ownerName");
		this.ownerUuid = tag.getUniqueId("ownerUuid");
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
		tag.setTag("upgrades", this.upgradeHelper.serializeNBT());
		tag.setTag("ammo", this.ammoHelper.serializeNBT());
		tag.setTag("move", this.moveHelper.serializeNBT());
		tag.setTag("fire", this.firingHelper.serializeNBT());
		tag.setTag("vars", this.firingVarsHelper.serializeNBT());
		tag.setFloat("lc", localLaunchPower);
		tag.setFloat("tp", localTurretPitch);
		tag.setFloat("tpd", localTurretDestPitch);
		tag.setFloat("tr", localTurretRotation);
		tag.setFloat("trd", localTurretDestRot);
		tag.setString("ownerName", ownerName);
		tag.setUniqueId("ownerUuid", ownerUuid);
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
		if (getRidingEntity() instanceof IMissileHitCallback) {
			((IMissileHitCallback) getRidingEntity()).onMissileImpact(world, x, y, z);
		}
	}

	@Override
	public void onMissileImpactEntity(World world, Entity entity) {
		if (getRidingEntity() instanceof IMissileHitCallback) {
			((IMissileHitCallback) getRidingEntity()).onMissileImpactEntity(world, entity);
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
	public float getDefaultMoveSpeed() {
		return this.currentForwardSpeedMax;
	}

	@Override
	public void onStuckDetected() {
		if (getControllingPassenger() instanceof NpcBase) {
			((NpcBase) getControllingPassenger()).onStuckDetected();
		}
	}

	@Nullable
	@Override
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return (T) inventory.storageInventory;
		}
		return super.getCapability(capability, facing);
	}

	@Override
	public void setOwner(EntityPlayer player) {
		ownerName = player.getName();
		ownerUuid = player.getUniqueID();
	}

	@Override
	public void setOwner(String ownerName, UUID ownerUuid) {
		this.ownerName = ownerName;
		this.ownerUuid = ownerUuid;
	}

	@Nullable
	@Override
	public String getOwnerName() {
		return ownerName;
	}

	@Nullable
	@Override
	public UUID getOwnerUuid() {
		return ownerUuid;
	}

	@Override
	public boolean isOwner(EntityPlayer player) {
		return player.getUniqueID().equals(ownerUuid);
	}
}
