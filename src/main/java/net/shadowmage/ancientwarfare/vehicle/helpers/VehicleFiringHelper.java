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

package net.shadowmage.ancientwarfare.vehicle.helpers;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.util.INBTSerializable;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.util.MathUtils;
import net.shadowmage.ancientwarfare.core.util.Trig;
import net.shadowmage.ancientwarfare.npc.entity.vehicle.ITarget;
import net.shadowmage.ancientwarfare.vehicle.config.AWVehicleStatics;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleBase;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleMovementType;
import net.shadowmage.ancientwarfare.vehicle.missiles.IAmmo;
import net.shadowmage.ancientwarfare.vehicle.missiles.MissileBase;
import net.shadowmage.ancientwarfare.vehicle.network.PacketAimUpdate;
import net.shadowmage.ancientwarfare.vehicle.network.PacketFireUpdate;

import java.util.Optional;
import java.util.Random;

/**
 * handles aiming, firing, updating turret, and client/server comms for input updates
 *
 * @author Shadowmage
 */
public class VehicleFiringHelper implements INBTSerializable<NBTTagCompound> {

	private static final int TRAJECTORY_ITERATIONS_CLIENT = 20;

	protected static Random rng = new Random();
	/**
	 * these values are updated when the client chooses an aim point, used by overlay rendering gui
	 */
	public float clientHitRange = 0.f;
	public float clientHitPosX = 0.f;
	public float clientHitPosY = 0.f;
	public float clientHitPosZ = 0.f;

	/**
	 * client-side values used by the riding player to check current input vs previous to see if new input packets should be sent...
	 */
	public float clientTurretYaw = 0.f;
	public float clientTurretPitch = 0.f;
	public float clientLaunchSpeed = 0.f;

	/**
	 * used on final launch, to calc final angle from 'approximate' firing arm/turret angle
	 */
	public Vec3d targetPos = null;

	/**
	 * is this vehicle in the process of launching a missile ? (animation, etc)
	 */
	private boolean isFiring = false;

	/**
	 * has started launching...
	 */
	private boolean isLaunching = false;
	/**
	 * if this vehicle isFiring, has it already finished launched, and is in the process of cooling down?
	 */
	private boolean isReloading = false;

	/**
	 * how many ticks until this vehicle is done reloading and can fire again
	 */
	private int reloadingTicks = 0;

	private VehicleBase vehicle;

	public VehicleFiringHelper(VehicleBase vehicle) {
		this.vehicle = vehicle;
	}

	/**
	 * spawns the number of missiles that this vehicle should fire by weight-count
	 * at the given offset from normal missile spawn position (offset is world
	 * coordinates, needs translating prior to being passed in)
	 */
	public void spawnMissilesByWeightCount(float ox, float oy, float oz) {
		int count = getMissileLaunchCount();
		for (int i = 0; i < count; i++) {
			spawnMissile(ox, oy, oz);
		}
	}

	/**
	 * spawn a missile of current missile type, with current firing paramaters, with additional raw x, y, z offsets
	 */
	public void spawnMissile(float ox, float oy, float oz) {
		if (!vehicle.world.isRemote) {
			IAmmo ammo = vehicle.ammoHelper.getCurrentAmmoType();
			if (ammo == null) {
				return;
			}
			if (vehicle.ammoHelper.getCurrentAmmoCount() > 0) {
				vehicle.ammoHelper.decreaseCurrentAmmo();

				Vec3d off = vehicle.getMissileOffset();
				float x = (float) (vehicle.posX + off.x + ox);
				float y = (float) (vehicle.posY + off.y + oy);
				float z = (float) (vehicle.posZ + off.z + oz);

				int count = ammo.hasSecondaryAmmo() ? ammo.getSecondaryAmmoTypeCount() : 1;
				//      Config.logDebug("type: "+ammo.getDisplayName()+" missile count to fire: "+count + " hasSecondaryAmmo: "+ammo.hasSecondaryAmmo() + " secType: "+ammo.getSecondaryAmmoType());
				MissileBase missile = null;
				float maxPower;
				float yaw;
				float pitch;
				float accuracy;
				float power;
				for (int i = 0; i < count; i++) {
					maxPower = getAdjustedMaxMissileVelocity();
					power = Math.min(vehicle.localLaunchPower, maxPower);
					yaw = vehicle.localTurretRotation;
					pitch = vehicle.localTurretPitch + vehicle.rotationPitch;
					if (AWVehicleStatics.adjustMissilesForAccuracy) {
						accuracy = getAccuracyAdjusted();
						yaw += (float) rng.nextGaussian() * (1.f - accuracy) * 10.f;
						if (vehicle.canAimPower() && !ammo.isRocket()) {
							power += (float) rng.nextGaussian() * (1.f - accuracy) * 2.5f;
							if (power < 1.f) {
								power = 1.f;
							}
						} else if (vehicle.canAimPitch()) {
							pitch += (float) rng.nextGaussian() * (1.f - accuracy) * 10.f;
						} else if (ammo != null && ammo.isRocket()) {
							power += power / vehicle.currentLaunchSpeedPowerMax;
							pitch += (rng.nextFloat() * 2.f - 1.f) * (1.f - accuracy) * 50.f;
						}
					}
					missile = getMissile2(x, y, z, yaw, pitch, power);
					if (vehicle.vehicleType.getMovementType() == VehicleMovementType.AIR1 || vehicle.vehicleType
							.getMovementType() == VehicleMovementType.AIR2) {
						missile.motionX += vehicle.motionX;
						missile.motionY += vehicle.motionY;
						missile.motionZ += vehicle.motionZ;
					}
					if (missile != null) {
						vehicle.world.spawnEntity(missile);
					}
				}
			}
		}
	}

	MissileBase getMissile2(float x, float y, float z, float yaw, float pitch, float velocity) {
		IAmmo ammo = vehicle.ammoHelper.getCurrentAmmoType();
		if (ammo != null) {
			MissileBase missile = new MissileBase(vehicle.world);
			if (ammo.hasSecondaryAmmo()) {
				ammo = ammo.getSecondaryAmmoType();
			}
			missile.setMissileParams2(ammo, x, y, z, yaw, pitch, velocity);
			missile.setMissileCallback(vehicle);
			missile.setLaunchingEntity(vehicle);
			missile.setShooter(vehicle.getControllingPassenger());
			return missile;
		}
		return null;
	}

	public void onTick() {
		if (this.isReloading) {
			this.vehicle.onReloadUpdate();
			if (this.reloadingTicks <= 0) {
				this.setFinishedReloading();
				this.vehicle.firingVarsHelper.onReloadingFinished();
			}
			this.reloadingTicks--;
		}
		if (this.isFiring) {
			vehicle.onFiringUpdate();
		}
		if (this.isLaunching) {
			vehicle.onLaunchingUpdate();
		}
		if (vehicle.world.isRemote) {
			if (!vehicle.canAimPitch()) {
				this.clientTurretPitch = vehicle.localTurretPitch;
			}
			if (!vehicle.canAimPower()) {
				this.clientLaunchSpeed = vehicle.localLaunchPower;
			}
			if (!vehicle.canAimRotate()) {
				this.clientTurretYaw = vehicle.rotationYaw;
			}
		}
		if (!vehicle.canAimPower()) {
			vehicle.localLaunchPower = vehicle.currentLaunchSpeedPowerMax;
		}
		if (vehicle.canAimRotate()) {
			float diff = vehicle.rotationYaw - vehicle.prevRotationYaw;
			vehicle.localTurretRotation += diff;
			this.clientTurretYaw += diff;
		}
	}

	/**
	 * get how many missiles can be fired at the current missileType and weight
	 * will return at least 1
	 */
	public int getMissileLaunchCount() {
		IAmmo ammo = vehicle.ammoHelper.getCurrentAmmoType();
		int missileCount = 1;
		if (ammo != null) {
			missileCount = (int) (vehicle.vehicleType.getMaxMissileWeight() / ammo.getAmmoWeight());
			if (missileCount < 1) {
				missileCount = 1;
			}
		}
		return missileCount;
	}

	/**
	 * gets the adjusted max missile velocity--adjusted by missile weight percentage of vehicleMaxMissileWeight
	 */
	public float getAdjustedMaxMissileVelocity() {
		float velocity = vehicle.currentLaunchSpeedPowerMax;
		IAmmo ammo = vehicle.ammoHelper.getCurrentAmmoType();
		if (ammo != null) {
			float missileWeight = ammo.getAmmoWeight();
			float maxWeight = vehicle.vehicleType.getMaxMissileWeight();
			if (missileWeight > maxWeight) {
				float totalWeight = missileWeight + maxWeight;
				float temp = maxWeight / totalWeight;
				temp *= 2;
				velocity *= temp;
			}
		}
		//  Config.logDebug("adj velocity: "+velocity);
		return velocity;
	}

	/**
	 * get accuracy after adjusting for rider (soldier)
	 */
	private float getAccuracyAdjusted() {
		return vehicle.currentAccuracy;
	}

	/**
	 * if not already firing, this will initiate the launch sequence (phase 1 of 3).
	 * Called by this to start missileLaunch. (triggered from packet)
	 */
	private void initiateLaunchSequence() {
		if (!this.isFiring && !this.isLaunching && this.reloadingTicks <= 0) {
			this.isFiring = true;
			this.isLaunching = false;
			this.isReloading = false;
		}
	}

	/**
	 * setReloading to finished. private for a reason... (return to phase 0)
	 */
	private void setFinishedReloading() {
		this.isFiring = false;
		this.isReloading = false;
		this.isLaunching = false;
		this.reloadingTicks = 0;
	}

	/**
	 * initiate actual launching of missiles (phase 2 of 3)
	 */
	public void startLaunching() {
		this.isFiring = false;
		this.isLaunching = true;
		this.isReloading = false;
	}

	/**
	 * finish the launching sequence, and begin reloading (phase 3 of 3)
	 */
	public void setFinishedLaunching() {
		this.isFiring = false;
		this.isReloading = true;
		this.isLaunching = false;
		this.reloadingTicks = vehicle.currentReloadTicks;
	}

	public void handleFireUpdate() {
		if (reloadingTicks <= 0 || vehicle.world.isRemote) {

			boolean shouldFire = vehicle.ammoHelper.getCurrentAmmoCount() > 0 || vehicle.ammoHelper.doesntUseAmmo();
			if (shouldFire) {

				if (!vehicle.world.isRemote) {
					NetworkHandler.sendToAllTracking(vehicle, new PacketFireUpdate(vehicle));
				}
				this.initiateLaunchSequence();
			}
		}
	}

	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	public void updateAim(Optional<Float> pitch, Optional<Float> yaw, Optional<Float> power) {
		boolean sendReply = false;
		if (pitch.isPresent()) {
			sendReply = true;
			vehicle.localTurretDestPitch = pitch.get();
		}
		if (yaw.isPresent()) {
			sendReply = true;
			vehicle.localTurretDestRot = yaw.get();
		}
		if (power.isPresent()) {
			sendReply = true;
			vehicle.localLaunchPower = power.get();
		}
		if (!vehicle.world.isRemote && sendReply) {
			NetworkHandler.sendToAllTracking(vehicle, new PacketAimUpdate(vehicle, pitch, yaw, power));
		}
	}

	public void handleFireInput() {
		if (isReadyToFire()) {
			NetworkHandler.sendToServer(new PacketFireUpdate(vehicle));
		}
	}

	public boolean isReadyToFire() {
		return !isFiring && !isLaunching && !isReloading && hasAmmo();
	}

	private boolean hasAmmo() {
		return vehicle.ammoHelper.doesntUseAmmo() || (vehicle.ammoHelper.getCurrentAmmoCount() > 0 && vehicle.ammoHelper.getCurrentAmmoType() != null);
	}

	public void handleAimKeyInput(float pitch, float yaw) {
		boolean pitchUpdated = false;
		boolean powerUpdated = false;
		boolean yawUpdated = false;
		if (vehicle.canAimPitch()) {
			float pitchTest = this.clientTurretPitch + pitch;
			if (pitchTest < vehicle.currentTurretPitchMin) {
				pitchTest = vehicle.currentTurretPitchMin;
			} else if (pitchTest > vehicle.currentTurretPitchMax) {
				pitchTest = vehicle.currentTurretPitchMax;
			}
			if (!MathUtils.epsilonEquals(pitchTest, this.clientTurretPitch)) {
				pitchUpdated = true;
				this.clientTurretPitch = pitchTest;
			}
		} else if (vehicle.canAimPower()) {
			float powerTest = clientLaunchSpeed + pitch;
			if (powerTest < 0) {
				powerTest = 0;
			} else if (powerTest > getAdjustedMaxMissileVelocity()) {
				powerTest = getAdjustedMaxMissileVelocity();
			}
			if (!MathUtils.epsilonEquals(clientLaunchSpeed, powerTest)) {
				powerUpdated = true;
				this.clientLaunchSpeed = powerTest;
			}
		}
		if (vehicle.canAimRotate()) {
			yawUpdated = true;
			this.clientTurretYaw += yaw;
		}

		if (powerUpdated || pitchUpdated || yawUpdated) {
			Optional<Float> turretPitch = pitchUpdated ? Optional.of(clientTurretPitch) : Optional.empty();
			Optional<Float> turretYaw = yawUpdated ? Optional.of(clientTurretYaw) : Optional.empty();
			Optional<Float> power = powerUpdated ? Optional.of(clientLaunchSpeed) : Optional.empty();

			NetworkHandler.sendToServer(new PacketAimUpdate(vehicle, turretPitch, turretYaw, power));
		}
	}

	/**
	 * CLIENT SIDE--used client side to update client desired pitch and yaw and send these to server/other clients...
	 */
	public void handleAimInput(Vec3d target) {
		boolean updated = false;
		boolean updatePitch = false;
		boolean updatePower = false;
		boolean updateYaw = false;
		Vec3d offset = vehicle.getMissileOffset();
		float x = (float) (vehicle.posX + offset.x);
		float y = (float) (vehicle.posY + offset.y);
		float z = (float) (vehicle.posZ + offset.z);
		float tx = (float) (target.x - x);
		float ty = (float) (target.y - y);
		float tz = (float) (target.z - z);
		float range = MathHelper.sqrt(tx * tx + tz * tz);
		if (vehicle.canAimPitch()) {
			Tuple<Float, Float> angles = Trig.getLaunchAngleToHit(tx, ty, tz, vehicle.localLaunchPower);
			if (angles.getFirst().isNaN() || angles.getSecond().isNaN()) {
			} else if (Trig.isAngleBetween(angles.getSecond(), vehicle.currentTurretPitchMin, vehicle.currentTurretPitchMax)) {
				if (!MathUtils.epsilonEquals(this.clientTurretPitch, angles.getSecond())) {
					this.clientTurretPitch = angles.getSecond();
					updated = true;
					updatePitch = true;
				}
			} else if (Trig.isAngleBetween(angles.getFirst(), vehicle.currentTurretPitchMin, vehicle.currentTurretPitchMax)) {
				if (!MathUtils.epsilonEquals(clientTurretPitch, angles.getFirst())) {
					this.clientTurretPitch = angles.getFirst();
					updated = true;
					updatePitch = true;
				}
			}
		} else if (vehicle.canAimPower()) {
			float power = Trig.iterativeSpeedFinder(tx, ty, tz, vehicle.localTurretPitch + vehicle.rotationPitch, TRAJECTORY_ITERATIONS_CLIENT,
					(vehicle.ammoHelper.getCurrentAmmoType() != null && vehicle.ammoHelper.getCurrentAmmoType().isRocket()));
			if (!MathUtils.epsilonEquals(clientLaunchSpeed, power) && power < getAdjustedMaxMissileVelocity()) {
				this.clientLaunchSpeed = power;
				updated = true;
				updatePower = true;
			}
		}
		if (vehicle.canAimRotate()) {
			float yaw = getAimYaw(target.x, target.z);
			if (!MathUtils.epsilonEquals(yaw, clientTurretYaw) && (vehicle.currentTurretRotationMax >= 180 || Trig
					.isAngleBetween(yaw, vehicle.localTurretRotationHome - vehicle.currentTurretRotationMax,
							vehicle.localTurretRotationHome + vehicle.currentTurretRotationMax))) {
				if (Math.abs(yaw - clientTurretYaw) > 0.25f) {
					this.clientTurretYaw = yaw;
					updated = true;
					updateYaw = true;
				}
			}
		}

		if (updated) {
			this.clientHitRange = range;
			Optional<Float> turretPitch = updatePitch ? Optional.of(clientTurretPitch) : Optional.empty();
			Optional<Float> turretYaw = updateYaw ? Optional.of(clientTurretYaw) : Optional.empty();
			Optional<Float> power = updatePower ? Optional.of(clientLaunchSpeed) : Optional.empty();
			NetworkHandler.sendToServer(new PacketAimUpdate(vehicle, turretPitch, turretYaw, power));
		}
	}

	public boolean isAtTarget() {
		return isAtTarget(0.35f);
	}

	private boolean isAtTarget(float range) {
		float yaw = Trig.wrapTo360(vehicle.localTurretRotation);
		float dest = Trig.wrapTo360(vehicle.localTurretDestRot);

		return MathUtils.epsilonEquals(vehicle.localTurretDestPitch, vehicle.localTurretPitch) && Math.abs(yaw - dest) < range;
	}

	public boolean isNearTarget() {
		return isAtTarget(5f);
	}

	public void handleSoldierTargetInput(double targetX, double targetY, double targetZ) {
		boolean updated = false;
		boolean updatePitch = false;
		boolean updatePower = false;
		boolean updateYaw = false;
		Vec3d offset = vehicle.getMissileOffset();
		float x = (float) (vehicle.posX + offset.x);
		float y = (float) (vehicle.posY + offset.y);
		float z = (float) (vehicle.posZ + offset.z);
		float tx = (float) (targetX - x);
		float ty = (float) (targetY - y);
		float tz = (float) (targetZ - z);
		if (vehicle.canAimPitch()) {
			Tuple<Float, Float> angles = Trig.getLaunchAngleToHit(tx, ty, tz, vehicle.localLaunchPower);
			if (angles.getFirst().isNaN() || angles.getSecond().isNaN()) {
			} else if (Trig.isAngleBetween(angles.getSecond(), vehicle.currentTurretPitchMin, vehicle.currentTurretPitchMax)) {
				if (!MathUtils.epsilonEquals(vehicle.localTurretDestPitch, angles.getSecond())) {
					vehicle.localTurretDestPitch = angles.getSecond();
					updated = true;
					updatePitch = true;
				}
			} else if (Trig.isAngleBetween(angles.getFirst(), vehicle.currentTurretPitchMin, vehicle.currentTurretPitchMax)) {
				if (!MathUtils.epsilonEquals(vehicle.localTurretDestPitch, angles.getFirst())) {
					vehicle.localTurretDestPitch = angles.getFirst();
					updated = true;
					updatePitch = true;
				}
			}
		} else if (vehicle.canAimPower()) {
			float power = Trig.iterativeSpeedFinder(tx, ty, tz, vehicle.localTurretPitch + vehicle.rotationPitch, TRAJECTORY_ITERATIONS_CLIENT,
					(vehicle.ammoHelper.getCurrentAmmoType() != null && vehicle.ammoHelper.getCurrentAmmoType().isRocket()));
			if (!MathUtils.epsilonEquals(vehicle.localLaunchPower, power) && power < getAdjustedMaxMissileVelocity()) {
				this.vehicle.localLaunchPower = power;
				updated = true;
				updatePower = true;
			}
		}
		if (vehicle.canAimRotate()) {
			float yaw = getAimYaw(targetX, targetZ);
			if (!MathUtils.epsilonEquals(yaw, vehicle.localTurretDestRot) && (vehicle.currentTurretRotationMax >= 180 || Trig
					.isAngleBetween(yaw, vehicle.localTurretRotationHome - vehicle.currentTurretRotationMax,
							vehicle.localTurretRotationHome + vehicle.currentTurretRotationMax))) {
				this.vehicle.localTurretDestRot = yaw;
				updated = true;
				updateYaw = true;
			}
		}
		if (updated && !vehicle.world.isRemote) {
			Optional<Float> turretPitch = updatePitch ? Optional.of(vehicle.localTurretDestPitch) : Optional.empty();
			Optional<Float> turretYaw = updateYaw ? Optional.of(vehicle.localTurretDestRot) : Optional.empty();
			Optional<Float> power = updatePower ? Optional.of(vehicle.localLaunchPower) : Optional.empty();
			NetworkHandler.sendToAllTracking(vehicle, new PacketAimUpdate(vehicle, turretPitch, turretYaw, power));
		}
	}

	private float getAimYaw(double targetX, double targetZ) {
		Vec3d offset = vehicle.getMissileOffset();
		float vecX = (float) (vehicle.posX + (vehicle.canTurretTurn() ? offset.x : 0) - targetX);
		float vecZ = (float) (vehicle.posZ + (vehicle.canTurretTurn() ? offset.z : 0) - targetZ);
		//noinspection SuspiciousNameCombination
		return Trig.wrapTo360(Trig.toDegrees((float) Math.atan2(vecX, vecZ)));
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("rt", reloadingTicks);
		tag.setBoolean("f", this.isFiring);
		tag.setBoolean("r", this.isReloading);
		tag.setBoolean("l", this.isLaunching);
		return tag;
	}

	@Override
	public void deserializeNBT(NBTTagCompound tag) {
		this.reloadingTicks = tag.getInteger("rt");
		this.isFiring = tag.getBoolean("f");
		this.isReloading = tag.getBoolean("r");
		this.isLaunching = tag.getBoolean("l");
	}

	private static final float NEGLIGIBLE_ANGLE_DIFFERENCE = 0.35f;

	public boolean isAimedAt(ITarget target) {
		return isPitchPointedAt(target) && isYawPointedAt(target) && isPowerSetToPointAt(target);
	}

	private boolean isYawPointedAt(ITarget target) {
		float minYaw = getAimYaw(target.getBoundigBox().minX, target.getBoundigBox().minZ);
		float maxYaw = getAimYaw(target.getBoundigBox().maxX, target.getBoundigBox().maxZ);

		if (minYaw > maxYaw) {
			float temp = minYaw;
			minYaw = maxYaw;
			maxYaw = temp;
		}

		float vehicleRotation = vehicle.canAimRotate() ? vehicle.localTurretRotation : vehicle.rotationYaw;

		return Trig.isAngleBetween(vehicleRotation, minYaw, maxYaw);
	}

	private boolean isPitchPointedAt(ITarget target) {
		if (!vehicle.canAimPitch()) {
			return true;
		}

		Vec3d offset = vehicle.getMissileOffset();
		float targetX = (float) target.getBoundigBox().minX - (float) (vehicle.posX + offset.x);
		float targetY = (float) target.getBoundigBox().minY - (float) (vehicle.posY + offset.y);
		float targetZ = (float) target.getBoundigBox().minZ - (float) (vehicle.posZ + offset.z);

		Tuple<Float, Float> anglesMin = Trig.getLaunchAngleToHit(targetX, targetY, targetZ, vehicle.localLaunchPower);
		//noinspection SimplifiableIfStatement
		if (Math.abs(anglesMin.getSecond() - vehicle.localTurretPitch) < NEGLIGIBLE_ANGLE_DIFFERENCE || Math
				.abs(anglesMin.getFirst() - vehicle.localTurretPitch) < NEGLIGIBLE_ANGLE_DIFFERENCE) {
			return true;
		}

		targetX = (float) target.getBoundigBox().maxX - (float) (vehicle.posX + offset.x);
		targetY = (float) target.getBoundigBox().maxY - (float) (vehicle.posY + offset.y);
		targetZ = (float) target.getBoundigBox().maxZ - (float) (vehicle.posZ + offset.z);

		Tuple<Float, Float> anglesMax = Trig.getLaunchAngleToHit(targetX, targetY, targetZ, vehicle.localLaunchPower);
		//noinspection SimplifiableIfStatement
		if (Math.abs(anglesMax.getSecond() - vehicle.localTurretPitch) < NEGLIGIBLE_ANGLE_DIFFERENCE || Math
				.abs(anglesMax.getFirst() - vehicle.localTurretPitch) < NEGLIGIBLE_ANGLE_DIFFERENCE) {
			return true;
		}

		return Trig.isAngleBetween(vehicle.localTurretPitch, anglesMin.getFirst(), anglesMax.getFirst())
				|| Trig.isAngleBetween(vehicle.localTurretPitch, anglesMin.getSecond(), anglesMax.getSecond());
	}

	private boolean isPowerSetToPointAt(ITarget target) {
		if (!vehicle.canAimPower()) {
			return true;
		}
		Vec3d offset = vehicle.getMissileOffset();
		float x = (float) (vehicle.posX + offset.x);
		float y = (float) (vehicle.posY + offset.y);
		float z = (float) (vehicle.posZ + offset.z);

		float minDistY;
		float maxDistY;
		if (Math.abs(target.getBoundigBox().minY - y) < Math.abs(target.getBoundigBox().maxY - y)) {
			minDistY = (float) (target.getBoundigBox().minY - y);
			maxDistY = (float) (target.getBoundigBox().maxY - y);
		} else {
			minDistY = (float) (target.getBoundigBox().maxY - y);
			maxDistY = (float) (target.getBoundigBox().minY - y);
		}

		float powerMin = getPowerFor((float) target.getBoundigBox().minX - x, minDistY, (float) target.getBoundigBox().minZ - z);
		float powerMax = getPowerFor((float) target.getBoundigBox().maxX - x, maxDistY, (float) target.getBoundigBox().maxZ - z);

		if (powerMin > powerMax) {
			float temp = powerMin;
			powerMin = powerMax;
			powerMax = temp;
		}

		return vehicle.localLaunchPower >= powerMin && vehicle.localLaunchPower <= powerMax;
	}

	private float getPowerFor(float distX, float distY, float distZ) {
		return Trig.iterativeSpeedFinder(distX, distY, distZ, vehicle.localTurretPitch + vehicle.rotationPitch,
						TRAJECTORY_ITERATIONS_CLIENT, (vehicle.ammoHelper.getCurrentAmmoType() != null && vehicle.ammoHelper.getCurrentAmmoType().isRocket()));
	}

	public float getAimYaw(ITarget target) {
		return getAimYaw(target.getX(), target.getZ());
	}
}
