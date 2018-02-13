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
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.shadowmage.ancientwarfare.core.util.Trig;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleBase;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleMovementType;
import net.shadowmage.ancientwarfare.vehicle.missiles.IAmmo;
import net.shadowmage.ancientwarfare.vehicle.missiles.MissileBase;
import shadowmage.ancient_warfare.common.config.Config;
import shadowmage.ancient_warfare.common.config.Settings;
import shadowmage.ancient_warfare.common.interfaces.INBTTaggable;
import shadowmage.ancient_warfare.common.network.Packet02Vehicle;
import shadowmage.ancient_warfare.common.npcs.NpcBase;
import shadowmage.ancient_warfare.common.utils.Pair;
import shadowmage.ancient_warfare.common.utils.Vec3d;

import java.util.Random;

/**
 * handles aiming, firing, updating turret, and client/server comms for input updates
 *
 * @author Shadowmage
 */
public class VehicleFiringHelper implements INBTTaggable {

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
	public boolean isFiring = false;

	/**
	 * has started launching...
	 */
	public boolean isLaunching = false;
	/**
	 * if this vehicle isFiring, has it already finished launched, and is in the process of cooling down?
	 */
	public boolean isReloading = false;

	/**
	 * how many ticks until this vehicle is done reloading and can fire again
	 */
	public int reloadingTicks = 0;

	private VehicleBase vehicle;

	public VehicleFiringHelper(VehicleBase vehicle) {
		this.vehicle = vehicle;
	}

	/**
	 * spawns the number of missiles that this vehicle should fire by weight-count
	 * at the given offset from normal missile spawn position (offset is world
	 * coordinates, needs translating prior to being passed in)
	 *
	 * @param ox
	 * @param oy
	 * @param oz
	 */
	public void spawnMissilesByWeightCount(float ox, float oy, float oz) {
		int count = getMissileLaunchCount();
		for (int i = 0; i < count; i++) {
			spawnMissile(ox, oy, oz);
		}
	}

	/**
	 * spawn a missile of current missile type, with current firing paramaters, with additional raw x, y, z offsets
	 *
	 * @param ox
	 * @param oy
	 * @param oz
	 */
	public void spawnMissile(float ox, float oy, float oz) {
		if (!vehicle.world.isRemote) {
			IAmmo ammo = vehicle.ammoHelper.getCurrentAmmoType();
			if (ammo == null) {
				return;
			}
			if (vehicle.ammoHelper.getCurrentAmmoCount() > 0) {
				vehicle.ammoHelper.decreaseCurrentAmmo(1);

				Vec3d off = vehicle.getMissileOffset();
				float x = (float) vehicle.posX + off.x + ox;
				float y = (float) vehicle.posY + off.y + oy;
				float z = (float) vehicle.posZ + off.z + oz;

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
					power = vehicle.localLaunchPower > maxPower ? maxPower : vehicle.localLaunchPower;
					yaw = vehicle.localTurretRotation;
					pitch = vehicle.localTurretPitch + vehicle.rotationPitch;
					if (Config.adjustMissilesForAccuracy) {
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
							pitch += (float) (rng.nextFloat() * 2.f - 1.f) * (1.f - accuracy) * 50.f;
						}
					}
					missile = vehicle.ammoHelper.getMissile2(x, y, z, yaw, pitch, power);
					if (vehicle.vehicleType.getMovementType() == VehicleMovementType.AIR1 || vehicle.vehicleType
							.getMovementType() == VehicleMovementType.AIR2) {
						missile.motionX += vehicle.motionX;
						missile.motionY += vehicle.motionY;
						missile.motionZ += vehicle.motionZ;
					}
					if (missile != null) {
						vehicle.world.spawnEntityInWorld(missile);
					}
				}
			}
		}
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
	 *
	 * @return
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
	 *
	 * @return
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
	 *
	 * @return
	 */
	public float getAccuracyAdjusted() {
		float accuracy = this.vehicle.currentAccuracy;
		if (vehicle.riddenByEntity != null && vehicle.riddenByEntity instanceof NpcBase) {
			NpcBase npc = (NpcBase) vehicle.riddenByEntity;
			return accuracy *= npc.getAccuracy();
		}
		return accuracy;
	}

	/**
	 * if not already firing, this will initiate the launch sequence (phase 1 of 3).
	 * Called by this to start missileLaunch. (triggered from packet)
	 */
	public void initiateLaunchSequence() {
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

	/**
	 * called from the vehicle to handle input packet data for firing and/or aim updates
	 *
	 * @param tag
	 */
	public void handleInputData(NBTTagCompound tag) {
		if (tag.hasKey("fm") && (this.reloadingTicks <= 0 || vehicle.world.isRemote))//if fire command and not already firing (or is client)...
		{
			this.handleFireUpdate();
		}
		if (tag.hasKey("aim")) {
			this.handleAimUpdate(tag);
		}
	}

	/**
	 * handle fire updates
	 *
	 * @param tag
	 */
	public void handleFireUpdate() {
		if (reloadingTicks <= 0 || vehicle.world.isRemote)//if not already firing, or client, accept packet
		{
			boolean shouldFire = vehicle.ammoHelper.getCurrentAmmoCount() > 0 || vehicle.ammoHelper.hasNoAmmo();
			if (shouldFire)//if should fire...do so.  if server, relay should-fire to clients
			{
				if (!vehicle.world.isRemote) {
					Packet02Vehicle pkt = new Packet02Vehicle();
					pkt.setParams(vehicle);
					NBTTagCompound reply = new NBTTagCompound();
					reply.setBoolean("fm", true);
					pkt.setInputData(reply);
					pkt.sendPacketToAllTrackingClients(vehicle);
				}
				this.initiateLaunchSequence();
			}
		}
	}

	/**
	 * handle aim-update information
	 *
	 * @param tag
	 */
	public void handleAimUpdate(NBTTagCompound tag) {
		NBTTagCompound reply = new NBTTagCompound();
		boolean sendReply = false;
		if (tag.hasKey("aimPitch")) {
			sendReply = true;
			vehicle.localTurretDestPitch = tag.getFloat("aimPitch");
			reply.setFloat("aimPitch", vehicle.localTurretDestPitch);
		}
		if (tag.hasKey("aimYaw")) {
			sendReply = true;
			vehicle.localTurretDestRot = tag.getFloat("aimYaw");
			reply.setFloat("aimYaw", vehicle.localTurretDestRot);
		}
		if (tag.hasKey("aimPow")) {
			sendReply = true;
			vehicle.localLaunchPower = tag.getFloat("aimPow");
			reply.setFloat("aimPow", vehicle.localLaunchPower);
		}
		if (!vehicle.world.isRemote && sendReply) {
			reply.setBoolean("aim", true);
			Packet02Vehicle pkt = new Packet02Vehicle();
			pkt.setParams(vehicle);
			pkt.setInputData(reply);
			pkt.sendPacketToAllTrackingClients(vehicle);
		}
	}

	/**
	 * CLIENT SIDE--handle fire-button input from riding client.  Relay to server.  Include target vector if appropriate.
	 *
	 * @param target
	 */
	public void handleFireInput(Vec3 target) {
		if (!this.isFiring && (vehicle.ammoHelper.getCurrentAmmoCount() > 0 || vehicle.ammoHelper.getCurrentAmmoType() == null)) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setBoolean("fm", true);
			if (target != null) {
				tag.setFloat("fmx", (float) target.xCoord);
				tag.setFloat("fmy", (float) target.yCoord);
				tag.setFloat("fmz", (float) target.zCoord);
			}
			Packet02Vehicle pkt = new Packet02Vehicle();
			pkt.setParams(vehicle);
			pkt.setInputData(tag);
			pkt.sendPacketToServer();
		}
	}

	/**
	 * params are in change DELTAS
	 *
	 * @param pitch
	 * @param yaw
	 */
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
			if (pitchTest != this.clientTurretPitch) {
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
			if (this.clientLaunchSpeed != powerTest) {
				powerUpdated = true;
				this.clientLaunchSpeed = powerTest;
			}
		}
		if (vehicle.canAimRotate()) {
			yawUpdated = true;
			this.clientTurretYaw += yaw;
			//TODO bound yaw with keyboard...
		}

		if (powerUpdated || pitchUpdated || yawUpdated) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setBoolean("aim", true);
			if (pitchUpdated) {
				tag.setFloat("aimPitch", clientTurretPitch);
			}
			if (powerUpdated) {
				tag.setFloat("aimPow", clientLaunchSpeed);
			}
			if (yawUpdated) {
				tag.setFloat("aimYaw", clientTurretYaw);
			}
			Packet02Vehicle pkt = new Packet02Vehicle();
			pkt.setParams(vehicle);
			pkt.setInputData(tag);
			pkt.sendPacketToServer();
		}
	}

	/**
	 * CLIENT SIDE--used client side to update client desired pitch and yaw and send these to server/other clients...
	 *
	 * @param target
	 */
	public void handleAimInput(Vec3 target) {
		boolean updated = false;
		boolean updatePitch = false;
		boolean updatePower = false;
		boolean updateYaw = false;
		Vec3d offset = vehicle.getMissileOffset();
		float x = (float) vehicle.posX + offset.x;
		float y = (float) vehicle.posY + offset.y;
		float z = (float) vehicle.posZ + offset.z;
		float tx = (float) (target.xCoord - x);
		float ty = (float) (target.yCoord - y);
		float tz = (float) (target.zCoord - z);
		float range = MathHelper.sqrt(tx * tx + tz * tz);
		if (vehicle.canAimPitch()) {
			Pair<Float, Float> angles = Trig.getLaunchAngleToHit(tx, ty, tz, vehicle.localLaunchPower);
			if (angles.key().isNaN() || angles.value().isNaN()) {
			} else if (angles.value() >= vehicle.currentTurretPitchMin && angles.value() <= vehicle.currentTurretPitchMax) {
				if (this.clientTurretPitch != angles.value()) {
					this.clientTurretPitch = angles.value();
					updated = true;
					updatePitch = true;
				}
			} else if (angles.key() >= vehicle.currentTurretPitchMin && angles.key() <= vehicle.currentTurretPitchMax) {
				if (this.clientTurretPitch != angles.key()) {
					this.clientTurretPitch = angles.key();
					updated = true;
					updatePitch = true;
				}
			}
		} else if (vehicle.canAimPower()) {
			float power = Trig.iterativeSpeedFinder(tx, ty, tz, vehicle.localTurretPitch + vehicle.rotationPitch, Settings.getClientPowerIterations(),
					(vehicle.ammoHelper.getCurrentAmmoType() != null && vehicle.ammoHelper.getCurrentAmmoType().isRocket()));
			if (this.clientLaunchSpeed != power && power < getAdjustedMaxMissileVelocity()) {
				this.clientLaunchSpeed = power;
				updated = true;
				updatePower = true;
			}
		}
		if (vehicle.canAimRotate()) {
			float xAO = (float) (vehicle.posX + offset.x - target.xCoord);
			float zAO = (float) (vehicle.posZ + offset.z - target.zCoord);
			float yaw = Trig.toDegrees((float) Math.atan2(xAO, zAO));
			if (yaw != this.clientTurretYaw && (vehicle.currentTurretRotationMax >= 180 || Trig
					.isAngleBetween(yaw, vehicle.localTurretRotationHome - vehicle.currentTurretRotationMax,
							vehicle.localTurretRotationHome + vehicle.currentTurretRotationMax))) {
				if (Trig.getAbsDiff(yaw, this.clientTurretYaw) > 0.25f) {
					this.clientTurretYaw = yaw;
					updated = true;
					updateYaw = true;
				}
			}
		}

		if (updated) {
			this.clientHitRange = range;
			NBTTagCompound tag = new NBTTagCompound();
			tag.setBoolean("aim", true);
			if (updatePitch) {
				tag.setFloat("aimPitch", this.clientTurretPitch);
			}
			if (updateYaw) {
				tag.setFloat("aimYaw", this.clientTurretYaw);
			}
			if (updatePower) {
				tag.setFloat("aimPow", this.clientLaunchSpeed);
			}
			Packet02Vehicle pkt = new Packet02Vehicle();
			pkt.setParams(vehicle);
			pkt.setInputData(tag);
			pkt.sendPacketToServer();
		}
	}

	/**
	 * used by soldiers to see if turret has lined up with input params
	 *
	 * @return
	 */
	public boolean isAtTarget() {
		float yaw = this.vehicle.localTurretRotation;
		float dest = this.vehicle.localTurretDestRot;
		while (yaw < 0) {
			yaw += 360.f;
		}
		while (yaw >= 360.f) {
			yaw -= 360.f;
		}
		while (dest < 0) {
			dest += 360.f;
		}
		while (dest >= 360.f) {
			dest -= 360.f;
		}
		//  Config.logDebug("y: "+yaw+" d: "+dest);
		return vehicle.localTurretDestPitch == vehicle.localTurretPitch && Trig.getAbsDiff(yaw, dest) < 0.35f;
	}

	public boolean isNearTarget() {
		float yaw = this.vehicle.localTurretRotation;
		float dest = this.vehicle.localTurretDestRot;
		while (yaw < 0) {
			yaw += 360.f;
		}
		while (yaw >= 360.f) {
			yaw -= 360.f;
		}
		while (dest < 0) {
			dest += 360.f;
		}
		while (dest >= 360.f) {
			dest -= 360.f;
		}
		//  Config.logDebug("y: "+yaw+" d: "+dest);
		return Trig.getAbsDiff(vehicle.localTurretDestPitch, vehicle.localTurretPitch) < 5 && vehicle.localTurretDestPitch == vehicle.localTurretPitch && Trig
				.getAbsDiff(yaw, dest) < 5f;
	}

	/**
	 * used by soldiers to attempt to target a position. range should have already been validated by getEffectiveRange at some point
	 *
	 * @param o
	 * @param oY
	 * @param oZ
	 */
	public void handleSoldierTargetInput(double targetX, double targetY, double targetZ) {
		boolean updated = false;
		boolean updatePitch = false;
		boolean updatePower = false;
		boolean updateYaw = false;
		Vec3d offset = vehicle.getMissileOffset();
		float x = (float) vehicle.posX + offset.x;
		float y = (float) vehicle.posY + offset.y;
		float z = (float) vehicle.posZ + offset.z;
		float tx = (float) (targetX - x);
		float ty = (float) (targetY - y);
		float tz = (float) (targetZ - z);
		float range = MathHelper.sqrt(tx * tx + tz * tz);
		if (vehicle.canAimPitch()) {
			Pair<Float, Float> angles = Trig.getLaunchAngleToHit(tx, ty, tz, vehicle.localLaunchPower);
			if (angles.key().isNaN() || angles.value().isNaN()) {
			} else if (angles.value() >= vehicle.currentTurretPitchMin && angles.value() <= vehicle.currentTurretPitchMax) {
				if (vehicle.localTurretDestPitch != angles.value()) {
					vehicle.localTurretDestPitch = angles.value();
					updated = true;
					updatePitch = true;
				}
			} else if (angles.key() >= vehicle.currentTurretPitchMin && angles.key() <= vehicle.currentTurretPitchMax) {
				if (vehicle.localTurretDestPitch != angles.key()) {
					vehicle.localTurretDestPitch = angles.key();
					updated = true;
					updatePitch = true;
				}
			}
		} else if (vehicle.canAimPower()) {
			float power = Trig.iterativeSpeedFinder(tx, ty, tz, vehicle.localTurretPitch + vehicle.rotationPitch, Settings.getClientPowerIterations(),
					(vehicle.ammoHelper.getCurrentAmmoType() != null && vehicle.ammoHelper.getCurrentAmmoType().isRocket()));
			if (vehicle.localLaunchPower != power && power < getAdjustedMaxMissileVelocity()) {
				this.vehicle.localLaunchPower = power;
				updated = true;
				updatePower = true;
			}
		}
		if (vehicle.canAimRotate()) {
			float xAO = (float) (vehicle.posX + offset.x - targetX);
			float zAO = (float) (vehicle.posZ + offset.z - targetZ);
			float yaw = Trig.toDegrees((float) Math.atan2(xAO, zAO));
			if (yaw != this.vehicle.localTurretDestRot && (vehicle.currentTurretRotationMax >= 180 || Trig
					.isAngleBetween(yaw, vehicle.localTurretRotationHome - vehicle.currentTurretRotationMax,
							vehicle.localTurretRotationHome + vehicle.currentTurretRotationMax))) {
				this.vehicle.localTurretDestRot = yaw;
				updated = true;
				updateYaw = true;
			}
		}
		if (updated) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setBoolean("aim", true);
			if (updatePitch) {
				tag.setFloat("aimPitch", this.vehicle.localTurretDestPitch);
			}
			if (updateYaw) {
				tag.setFloat("aimYaw", this.vehicle.localTurretDestRot);
			}
			if (updatePower) {
				tag.setFloat("aimPow", this.vehicle.localLaunchPower);
			}
			if (!vehicle.world.isRemote) {
				Packet02Vehicle pkt = new Packet02Vehicle();
				pkt.setParams(vehicle);
				pkt.setInputData(tag);
				pkt.sendPacketToAllTrackingClients(vehicle);
			}
		}
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

	public void resetUpgradeStats() {

	}

}
