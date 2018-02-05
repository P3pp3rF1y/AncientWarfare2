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

package shadowmage.ancient_warfare.common.vehicles.helpers;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.shadowmage.ancientwarfare.core.util.Trig;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleBase;
import shadowmage.ancient_warfare.common.config.Config;
import shadowmage.ancient_warfare.common.interfaces.INBTTaggable;
import shadowmage.ancient_warfare.common.network.Packet02Vehicle;
import shadowmage.ancient_warfare.common.utils.BlockTools;
import shadowmage.ancient_warfare.common.vehicles.VehicleMovementType;

public class VehicleMoveHelper implements INBTTaggable {

	byte forwardInput;
	byte turnInput;
	byte powerInput;
	byte rotationInput;

	double destX;
	double destY;
	double destZ;
	float destYaw;
	float destPitch;

	int moveTicks = 0;
	int rotationTicks = 0;
	int pitchTicks = 0;

	public float forwardMotion = 0.f;
	protected float verticalMotion = 0.f;
	protected float turnMotion = 0.f;
	protected float pitchMotion = 0.f;
	protected float strafeMotion = 0.f;
	public float throttle = 0.f;

	protected float groundDrag = 0.96f;
	protected float groundStop = 0.02f;
	protected float rotationDrag = 0.94f;
	protected float rotationStop = 0.2f;

	protected float rotationSpeed = 0.f;
	protected float pitchSpeed = 0.f;

	protected boolean wasOnGround = true;

	protected VehicleBase vehicle;

	public VehicleMoveHelper(VehicleBase vehicle) {
		this.vehicle = vehicle;
	}

	public float getRotationSpeed() {
		return this.rotationSpeed;
	}

	public void setForwardInput(byte in) {
		this.forwardInput = in;
	}

	public void setStrafeInput(byte in) {
		this.turnInput = in;
	}

	public void handleMoveData(NBTTagCompound tag) {
		if (tag.hasKey("rp")) {
			this.pitchTicks = Config.vehicleMoveUpdateFrequency + 1;
			this.destPitch = tag.getFloat("rp");
		}
		if (tag.hasKey("ry")) {
			this.rotationTicks = Config.vehicleMoveUpdateFrequency + 1;
			this.destYaw = tag.getFloat("ry");
		}
		if (tag.hasKey("px")) {
			this.moveTicks = Config.vehicleMoveUpdateFrequency + 1;
			this.destX = tag.getFloat("px");
		}
		if (tag.hasKey("py")) {
			this.moveTicks = Config.vehicleMoveUpdateFrequency + 1;
			this.destY = tag.getFloat("py");
		}
		if (tag.hasKey("pz")) {
			this.moveTicks = Config.vehicleMoveUpdateFrequency + 1;
			this.destZ = tag.getFloat("pz");
		}
		if (tag.hasKey("tr")) {
			this.throttle = tag.getFloat("tr");
		}
		if (tag.hasKey("fm")) {
			this.forwardMotion = tag.getFloat("fm");
		}
	}

	public void handleInputData(NBTTagCompound tag) {
		if (tag.hasKey("f")) {
			this.forwardInput = tag.getByte("f");
		}
		if (tag.hasKey("s")) {
			this.turnInput = tag.getByte("s");
		}
		if (tag.hasKey("p")) {
			this.powerInput = tag.getByte("p");
		}
		if (tag.hasKey("r")) {
			this.rotationInput = tag.getByte("r");
		}
	}

	public void onUpdate() {
		if (vehicle.worldObj.isRemote) {
			onUpdateClient();
		} else {
			if (this.vehicle.riddenByEntity == null) {
				this.clearInputFromDismount();
			}
			onUpdateServer();
			this.vehicle.nav.onMovementUpdate();
		}
	}

	protected void onUpdateClient() {
		vehicle.noClip = true;
		vehicle.motionX = 0;
		vehicle.motionY = 0;
		vehicle.motionZ = 0;
		rotationSpeed = 0;
		pitchSpeed = 0;
		if (this.rotationTicks > 0) {
			rotationSpeed = (this.destYaw - vehicle.rotationYaw) / rotationTicks;
			vehicle.rotationYaw += this.rotationSpeed;
			this.rotationTicks--;
		}
		if (this.pitchTicks > 0) {
			pitchSpeed = (this.destPitch - vehicle.rotationPitch) / pitchTicks;
			vehicle.rotationPitch += this.pitchSpeed;
			this.pitchTicks--;
		}
		if (moveTicks > 0) {
			double dx = (destX - vehicle.posX) / (float) moveTicks;
			double dy = (destY - vehicle.posY) / (float) moveTicks;
			double dz = (destZ - vehicle.posZ) / (float) moveTicks;
			vehicle.motionX = dx;
			vehicle.motionY = dy;
			vehicle.motionZ = dz;
			moveTicks--;
		}
		vehicle.wheelRotationPrev = vehicle.wheelRotation;
		if (vehicle.vehicleType.getMovementType() == VehicleMovementType.AIR1 || vehicle.vehicleType.getMovementType() == VehicleMovementType.AIR2) {
			vehicle.wheelRotation += throttle;
		} else if (vehicle.vehicleType.getMovementType() == VehicleMovementType.WATER || vehicle.vehicleType.getMovementType() == VehicleMovementType.WATER2) {
			vehicle.wheelRotation += forwardMotion * 64;
		} else {
			vehicle.wheelRotation += forwardMotion * 60;
		}
		this.vehicle.moveEntity(vehicle.motionX, vehicle.motionY, vehicle.motionZ);
	}

	protected void onUpdateServer() {
		VehicleMovementType move = vehicle.vehicleType.getMovementType();
		switch (move) {
			case GROUND:
				this.applyGroundMotion();
				break;

			case WATER:
				this.applyWaterMotion();
				break;

			case WATER2:
				this.applyWaterMotion2();
				break;

			case AIR1:
				this.applyAir1Motion();
				break;

			case AIR2:
				this.applyAir2Motion();
				break;
		}
		if (move == VehicleMovementType.AIR1 || move == VehicleMovementType.AIR2) {
			vehicle.fallDistance = 0.f;
			if (vehicle.riddenByEntity != null) {
				vehicle.riddenByEntity.fallDistance = 0.f;
			}
		}
		vehicle.motionX = Trig.sinDegrees(vehicle.rotationYaw) * -forwardMotion;
		vehicle.motionZ = Trig.cosDegrees(vehicle.rotationYaw) * -forwardMotion;
		this.vehicle.moveEntity(vehicle.motionX, vehicle.motionY, vehicle.motionZ);
		this.wasOnGround = vehicle.onGround;
		if (vehicle.isCollidedHorizontally) {
			forwardMotion *= 0.65f;
		}
		this.tearUpGrass();
		boolean sendUpdate = (vehicle.motionX != 0 || vehicle.motionY != 0 || vehicle.motionZ != 0 || vehicle.rotationYaw != vehicle.prevRotationYaw || vehicle.rotationPitch != vehicle.prevRotationPitch);
		sendUpdate = sendUpdate || vehicle.riddenByEntity != null;
		sendUpdate = sendUpdate && this.vehicle.ticksExisted % Config.vehicleMoveUpdateFrequency == 0;
		sendUpdate = sendUpdate || this.vehicle.ticksExisted % 60 == 0;
		if (sendUpdate) {
			Packet02Vehicle pkt = new Packet02Vehicle();
			pkt.setMoveUpdate(this.vehicle, true, move == VehicleMovementType.AIR1 || move == VehicleMovementType.AIR2, true);
			pkt.sendPacketToAllTrackingClients(vehicle);
		}
	}

	protected void applyGroundMotion() {
		this.applyTurnInput(0.05f);
		this.applyForwardInput(0.0125f, true);
		vehicle.motionY -= 9.81 * 0.05f * 0.05f;
	}

	protected void applyWaterMotion() {
		this.applyTurnInput(0.05f);
		if (this.handleBoatBob(true) < 0) {
			this.forwardMotion *= 0.85f;
			this.strafeMotion *= 0.85f;
		}
		this.applyForwardInput(0.0125f, true);
	}

	protected void applyWaterMotion2() {
		this.applyTurnInput(0.05f);
		this.handleSubmarineMovement();
		if (this.handleBoatBob(false) < 0) {
			this.forwardMotion *= 0.85f;
			this.strafeMotion *= 0.85f;
		}
		if (vehicle.riddenByEntity != null) {
			vehicle.riddenByEntity.setAir(300);
		}
		this.applyForwardInput(0.0125f, true);
	}

	protected void applyAir1Motion() {
		this.applyThrottleInput();
		this.applyPitchInput(-15, 15);
		this.applyTurnInput(0.05f);
		this.applyAirplaneInput();
		this.detectCrash();
	}

	protected void applyAir2Motion() {
		this.applyThrottleInput();
		this.applyPitchInput(-5, 5);
		this.applyTurnInput(0.05f);
		this.applyHelicopterInput();
		this.detectCrash();
	}

	protected void handleSubmarineMovement() {
		float maxY = 0.2f;
		if (powerInput != 0) {
			if (Math.abs(vehicle.motionY) < maxY) {
				float percent = (float) (Math.abs(vehicle.motionY) / maxY);
				float adj = maxY * (1.f - percent) * (float) powerInput * 0.05f;
				vehicle.motionY += adj;
				if (vehicle.motionY < -maxY) {
					vehicle.motionY = -maxY;
				}
				if (vehicle.motionY > maxY) {
					vehicle.motionY = maxY;
				}
			}
		}
	}

	protected void applyAirplaneInput() {
		float weightAdjust = 1.f;
		if (vehicle.currentWeight > vehicle.baseWeight) {
			weightAdjust = vehicle.baseWeight / vehicle.currentWeight;
		}
		float maxSpeed = vehicle.currentForwardSpeedMax * weightAdjust;
		float percent = 1 - (forwardMotion / maxSpeed);
		float doublePercent = 1 - (forwardMotion / (maxSpeed * 2));
		percent = percent > 0.25f ? 0.25f : percent;
		float drag = vehicle.onGround ? 0.95f : 1.f - ((1 - throttle) * 0.1f);

		float changeFactor = percent * throttle * 0.125f;

		if (forwardMotion + changeFactor > maxSpeed) {
			changeFactor = 0;
		}
		forwardMotion += changeFactor;
		if (forwardMotion < 0) {
			forwardMotion = 0;
		}
		forwardMotion *= drag;
		if (forwardMotion > vehicle.currentForwardSpeedMax * 0.35f)//stall speed check
		{
			vehicle.motionY = vehicle.rotationPitch * forwardMotion * 0.0125f;
		} else {
			vehicle.motionY -= 9.81 * 0.05f * 0.05f;
		}
		if (forwardMotion > maxSpeed * 2) {
			forwardMotion = maxSpeed * 2;
		}
		if (Math.abs(forwardMotion) < groundStop && throttle == 0) {
			forwardMotion = 0.f;
		}
	}

	protected void applyHelicopterInput() {
		float weightAdjust = 1.f;
		if (vehicle.currentWeight > vehicle.baseWeight) {
			weightAdjust = vehicle.baseWeight / vehicle.currentWeight;
		}

		boolean reverse = (vehicle.rotationPitch > 0 && forwardMotion > 0) || (vehicle.rotationPitch < 0 && forwardMotion < 0);
		float maxSpeed = vehicle.currentForwardSpeedMax * weightAdjust;
		float percent = 1 - (Math.abs(forwardMotion) / maxSpeed);

		float changeFactor = percent * -vehicle.rotationPitch * 0.2f * 0.125f * throttle;
		if (reverse) {
			changeFactor *= 2;
		}
		forwardMotion += changeFactor;

		float drag = vehicle.onGround ? 0.95f : 1.f - (0.05f - Math.abs(vehicle.rotationPitch) * 0.01f);
		forwardMotion *= drag;

		if (forwardMotion > maxSpeed) {
			forwardMotion = maxSpeed;
		}
		if (forwardMotion < -maxSpeed) {
			forwardMotion = -maxSpeed;
		}
		if (Math.abs(forwardMotion) < groundStop && vehicle.rotationPitch <= 0.15f && vehicle.rotationPitch > -0.15f) {
			forwardMotion = 0.f;
		}
		float grav = 9.81f * 0.05f * 0.05f;
		float adjThr = 1 - (throttle > 0.65f ? 1.f : (throttle / 0.65f));
		if (throttle >= 0.642f && throttle <= 0.658f) {
			throttle = 0.65f;
		}
		/**
		 * 0=no adjust to grav
		 * 0.65f=cancel grav
		 * >0.65f=anti grav
		 */
		if (throttle >= 0.3f) {

			float minVertSpeed = -0.7f;
			float maxVertSpeed = 0.35f;
			float perfectSpeed = 0.f;
			float bitFactor = 0.142857f;
			float invFactor = 2.857143f;
			if (throttle < 0.65f) {
				float tpercent = 1 - ((throttle - 0.3f) * invFactor);
				perfectSpeed = tpercent * minVertSpeed;
			} else if (throttle > 0.65f) {
				float tpercent = (throttle - 0.65f) * invFactor;
				perfectSpeed = tpercent * maxVertSpeed;
			}
			//float perfectSpeed = minVertSpeed + (((throttle-0.25f)*1.33333f)*spread);
			float speedDelta = (float) (perfectSpeed - vehicle.motionY);
			if (Math.abs(speedDelta) < 0.03f) {
				vehicle.motionY = perfectSpeed;
			} else {
				float adjPercent = Math.abs(speedDelta) / 0.45f;
				adjPercent = adjPercent > 1.f ? 1.f : adjPercent;
				adjPercent *= 0.1f;
				float adjFactor = 1.0f;
				//      Config.logDebug("perfect: "+perfectSpeed +  " d: "+speedDelta + " sPerc: "+adjPercent);
				if (perfectSpeed < vehicle.motionY) {
					adjFactor *= 2.f;
				}
				vehicle.motionY += adjPercent * speedDelta * adjFactor;
			}
		} else {
			vehicle.motionY -= grav * adjThr;
		}

	}

	protected void applyThrottleInput() {
		if (this.powerInput != 0) {
			this.throttle += 0.025f * (float) this.powerInput;
		}
		this.throttle = this.throttle < 0.f ? 0.f : this.throttle > 1.f ? 1.f : this.throttle;
	}

	protected void applyPitchInput(float min, float max) {
		if (forwardInput != 0) {
			this.pitchMotion = (float) -forwardInput * 0.25f;
		} else {
			this.pitchMotion = 0;
		}
		this.vehicle.rotationPitch += this.pitchMotion;
		if (vehicle.rotationPitch < min) {
			vehicle.rotationPitch = min;
		}
		if (vehicle.rotationPitch > max) {
			vehicle.rotationPitch = max;
		}
		if (vehicle.rotationPitch > -0.15f && vehicle.rotationPitch < 0.15f) {
			vehicle.rotationPitch = 0.f;
		}
	}

	protected void applyForwardInput(float inputFactor, boolean slowReverse) {
		if (vehicle.currentForwardSpeedMax <= 0.f) {
			forwardMotion = 0.f;
			return;
		}
		float weightAdjust = 1.f;
		if (vehicle.currentWeight > vehicle.baseWeight) {
			weightAdjust = vehicle.baseWeight / vehicle.currentWeight;
		}
		if (forwardInput != 0) {
			boolean reverse = (forwardInput == -1 && forwardMotion > 0) || (forwardInput == 1 && forwardMotion < 0);
			float maxSpeed = vehicle.currentForwardSpeedMax * weightAdjust;
			float maxReverse = -maxSpeed * (slowReverse ? 0.6f : 1.f);
			float percent = 1 - (forwardMotion >= 0 ? (forwardMotion / maxSpeed) : (forwardMotion / maxReverse));
			percent = percent > 0.25f ? 0.25f : percent;
			percent = reverse ? 0.25f : percent;
			float changeFactor = percent * forwardInput * inputFactor;
			if (reverse) {
				changeFactor *= 2;
			}
			forwardMotion += changeFactor;
			if (forwardMotion > maxSpeed) {
				forwardMotion = maxSpeed;
			}
			if (forwardMotion < maxReverse) {
				forwardMotion = maxReverse;
			}
		} else {
			forwardMotion *= groundDrag;
		}
		if (Math.abs(forwardMotion) < groundStop && forwardInput == 0) {
			forwardMotion = 0.f;
		}
	}

	protected void applyTurnInput(float inputFactor) {
		float weightAdjust = 1.f;
		if (vehicle.currentWeight > vehicle.baseWeight) {
			weightAdjust = vehicle.baseWeight / vehicle.currentWeight;
		}
		if (turnInput != 0) {
			boolean reverse = (turnInput == -1 && turnMotion > 0) || (turnInput == 1 && turnMotion < 0);
			float maxSpeed = vehicle.currentStrafeSpeedMax * weightAdjust;
			float percent = 1 - (Math.abs(turnMotion) / maxSpeed);
			percent = reverse ? 1.f : percent;
			float changeFactor = percent * (turnInput * 2) * inputFactor;
			if (reverse) {
				changeFactor *= 2;
			}
			turnMotion += changeFactor;
			if (turnMotion > maxSpeed) {
				turnMotion = maxSpeed;
			}
			if (turnMotion < -maxSpeed) {
				turnMotion = -maxSpeed;
			}
		} else {
			turnMotion *= rotationDrag;
		}
		if (Math.abs(turnMotion) < rotationStop && turnInput == 0) {
			turnMotion = 0.f;
		}
		this.vehicle.rotationYaw -= this.turnMotion;
	}

	protected void detectCrash() {
		boolean crashSpeed = false;
		if (forwardMotion > vehicle.currentForwardSpeedMax * 0.35f) {
			crashSpeed = true;
		}
		boolean vertCrashSpeed = false;
		if (vehicle.motionY < -0.25f || vehicle.motionY > 0.25f) {
			vertCrashSpeed = true;
		}
		if (vehicle.isCollidedHorizontally) {
			if (!wasOnGround || crashSpeed) {
				if (!vehicle.worldObj.isRemote && vehicle.riddenByEntity instanceof EntityPlayer) {
					EntityPlayer player = (EntityPlayer) vehicle.riddenByEntity;
					player.addChatMessage("you have crashed!!");
				}
				if (!vehicle.worldObj.isRemote) {
					vehicle.setDead();
				}
			}
		}
		if (vehicle.isCollidedVertically) {
			if (vertCrashSpeed && !wasOnGround) {
				if (!vehicle.worldObj.isRemote && vehicle.riddenByEntity instanceof EntityPlayer) {
					EntityPlayer player = (EntityPlayer) vehicle.riddenByEntity;
					player.addChatMessage("you have crashed (vertical)!!");
				}
				if (!vehicle.worldObj.isRemote) {
					vehicle.setDead();
				}
			}
		}
	}

	/**
	 * code to set Y motion on a surface or subsurface water vehicle
	 *
	 * @param floats if the vehicle should return to the surface if it is underwater (false for submarines)
	 */
	protected int handleBoatBob(boolean floats) {
		float bitHeight = vehicle.height * 0.2f;
		AxisAlignedBB bb;
		int submergedBits = 0;
		for (int i = 0; i < 5; i++) {
			bb = AxisAlignedBB.getAABBPool()
					.getAABB(vehicle.boundingBox.minX, vehicle.boundingBox.minY + (i * bitHeight), vehicle.boundingBox.minZ, vehicle.boundingBox.maxX,
							vehicle.boundingBox.minY + ((1 + i) * bitHeight), vehicle.boundingBox.maxZ);
			if (vehicle.worldObj.isAABBInMaterial(bb, Material.water)) {
				submergedBits++;
			} else {
				break;
			}
		}
		submergedBits -= 2;
		if (!floats && submergedBits > 0) {
			submergedBits = 0;
		}
		if (submergedBits < 0) {
			vehicle.motionY -= 9.81f * 0.05f * 0.05f;
		} else {
			vehicle.motionY += (float) submergedBits * 0.02f;
			vehicle.motionY *= 0.8f;
		}
		return submergedBits;
	}

	protected void tearUpGrass() {
		if (vehicle.worldObj.isRemote || !vehicle.onGround || !Config.vehiclesTearUpGrass) {
			return;
		}
		for (int var24 = 0; var24 < 4; ++var24) {
			int x = MathHelper.floor_double(vehicle.posX + ((double) (var24 % 2) - 0.5D) * 0.8D);
			int y = MathHelper.floor_double(vehicle.posY);
			int z = MathHelper.floor_double(vehicle.posZ + ((double) (var24 / 2) - 0.5D) * 0.8D);
			//check top/upper blocks(riding through)
			int id = vehicle.worldObj.getBlockId(x, y, z);
			if (isPlant(id)) {
				BlockTools.breakBlockAndDrop(vehicle.worldObj, x, y, z, 0);
			}
			//check lower blocks (riding on)
			if (vehicle.worldObj.getBlockId(x, y - 1, z) == Block.grass.blockID) {
				vehicle.worldObj.setBlock(x, y - 1, z, Block.dirt.blockID, 0, 3);
			}
		}
	}

	protected static int[] plantBlockIDs = new int[] {Block.snow.blockID,
			Block.deadBush.blockID,
			Block.tallGrass.blockID,
			Block.plantRed.blockID,
			Block.plantYellow.blockID,
			Block.mushroomBrown.blockID,
			Block.mushroomRed.blockID};

	protected boolean isPlant(int id) {
		for (int i = 0; i < plantBlockIDs.length; i++) {
			if (id == plantBlockIDs[i]) {
				return true;
			}
		}
		return false;
	}

	public void setMoveTo(double x, double y, double z) {
		float yawDiff = Trig.getYawTowardsTarget(vehicle.posX, vehicle.posZ, x, z, vehicle.rotationYaw);
		byte fMot = 0;
		byte sMot = 0;
		if (Math.abs(yawDiff) > 5)//more than 5 degrees off, correct yaw first, then move forwards
		{
			if (yawDiff < 0) {
				sMot = 1;//left
			} else {
				sMot = -1;//right
			}
		}
		if (Math.abs(yawDiff) < 10 && Trig
				.getVelocity(x - vehicle.posX, y - vehicle.posY, z - vehicle.posZ) >= 0.25f)//further away than 1 block, move towards it
		{
			fMot = 1;
		}
		this.forwardInput = fMot;
		this.turnInput = sMot;
	}

	public void stopMotion() {
		this.clearInputFromDismount();
		vehicle.motionX = 0;
		vehicle.motionY = 0;
		vehicle.motionZ = 0;
	}

	public void clearInputFromDismount() {
		this.forwardInput = 0;
		this.turnInput = 0;
		this.powerInput = 0;
		this.rotationInput = 0;
		this.throttle = 0;
	}

	@Override
	public NBTTagCompound getNBTTag() {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setByte("fi", forwardInput);
		tag.setByte("si", turnInput);
		tag.setByte("pi", powerInput);
		tag.setByte("ri", rotationInput);
		tag.setFloat("tr", throttle);
		return tag;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		this.forwardInput = tag.getByte("fi");
		this.turnInput = tag.getByte("si");
		this.powerInput = tag.getByte("pi");
		this.rotationInput = tag.getByte("ri");
		this.throttle = tag.getFloat("tr");
	}

}
