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

package net.shadowmage.ancientwarfare.vehicle.missiles;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.shadowmage.ancientwarfare.core.util.Trig;
import net.shadowmage.ancientwarfare.vehicle.entity.IMissileHitCallback;
import net.shadowmage.ancientwarfare.vehicle.registry.AmmoRegistry;

import java.util.Iterator;
import java.util.List;

public class MissileBase extends Entity implements IEntityAdditionalSpawnData {

	/**
	 * Must be set after missile is constructed, but before spawned server side.  Client-side this will be set by the readSpawnData method.  This ammo type is responsible for many onTick qualities,
	 * effects of impact, and model/render instance used.
	 */
	public IAmmo ammoType = Ammo.ammoArrow;
	public Entity launcher = null;
	public Entity shooterLiving;
	IMissileHitCallback shooter = null;
	public int missileType = Ammo.ammoArrow.ammoType;
	int rocketBurnTime = 0;
	public int ticksImpacted = 0;

	public boolean inGround = false;
	public boolean hasImpacted = false;
	int blockX;
	int blockY;
	int blockZ;
	int blockID;
	int blockMeta;

	/**
	 * initial velocities, used by rocket for acceleration factor
	 */
	float mX;
	float mY;
	float mZ;

	/**
	 * @param par1World
	 */
	public MissileBase(World par1World) {
		super(par1World);
		this.entityCollisionReduction = 1.f;
		this.setSize(0.4f, 0.4f);
	}

	/**
	 * called server side after creating but before spawning. ammoType is set client-side by the readSpawnData method, as should all other movement (rotation/motion) params.
	 *
	 * @param type
	 * @param x
	 * @param y
	 * @param z
	 * @param mx
	 * @param my
	 * @param mz
	 */
	public void setMissileParams(IAmmo type, float x, float y, float z, float mx, float my, float mz) {
		this.ammoType = type;
		if (ammoType != null) {
			this.missileType = ammoType.getAmmoType();
		}
		this.setPosition(x, y, z);
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		this.motionX = mx;
		this.motionY = my;
		this.motionZ = mz;
		this.mX = mx;
		this.mY = my;
		this.mZ = mz;
		if (this.ammoType.updateAsArrow()) {
			this.onUpdateArrowRotation();
		}
		this.prevRotationPitch = this.rotationPitch;
		this.prevRotationYaw = this.rotationYaw;
		if (this.ammoType.isRocket() || this.ammoType.isTorpedo())//use launch power to determine rocket burn time...
		{
			float temp = MathHelper.sqrt(mx * mx + my * my + mz * mz);
			this.rocketBurnTime = (int) (temp * 20.f * AmmoHwachaRocket.burnTimeFactor);

			this.mX = (float) (motionX / temp) * AmmoHwachaRocket.accelerationFactor;
			this.mY = (float) (motionY / temp) * AmmoHwachaRocket.accelerationFactor;
			this.mZ = (float) (motionZ / temp) * AmmoHwachaRocket.accelerationFactor;
			this.motionX = mX;
			this.motionY = mY;
			this.motionZ = mZ;
		}
		//  Config.logDebug("missile spawning. motY: "+this.motionY);
	}

	public void setMissileParams2(IAmmo ammo, float x, float y, float z, float yaw, float angle, float velocity) {
		float vX = -Trig.sinDegrees(yaw) * Trig.cosDegrees(angle) * velocity * 0.05f;
		float vY = Trig.sinDegrees(angle) * velocity * 0.05f;
		float vZ = -Trig.cosDegrees(yaw) * Trig.cosDegrees(angle) * velocity * 0.05f;
		this.setMissileParams(ammo, x, y, z, vX, vY, vZ);
	}

	public void setShooter(Entity shooter) {
		this.shooterLiving = shooter;
	}

	public void setLaunchingEntity(Entity ent) {
		this.launcher = ent;
	}

	public void setMissileCallback(IMissileHitCallback shooter) {
		this.shooter = shooter;
	}

	public void onImpactEntity(Entity ent, float x, float y, float z) {
		if (Ammo.shouldEffectEntity(world, ent, this)) {
			this.ammoType.onImpactEntity(world, ent, x, y, z, this);
			if (this.shooter != null) {
				this.shooter.onMissileImpactEntity(world, ent);
			}
		}
	}

	public void onImpactWorld(RayTraceResult hit) {
		if (!world.isRemote) {
			//    Config.logDebug("World Impacted by: "+this.ammoType.getDisplayName()+" :: "+this);
		}
		this.ammoType.onImpactWorld(world, hit.blockX, hit.blockY, hit.blockZ, this, hit);
		if (this.shooter != null) {
			this.shooter.onMissileImpact(world, hit.blockX, hit.blockY, hit.blockZ);
		}
	}

	@SideOnly(Side.CLIENT)
	/**
	 * Return whether this entity should be rendered as on fire.
	 */
	@Override
	public boolean canRenderOnFire() {
		return this.ammoType.isFlaming();
	}

	@Override
	public boolean canBeCollidedWith() {
		return false;
	}

	@Override
	public boolean canBePushed() {
		return false;
	}

	@Override
	public void applyEntityCollision(Entity par1Entity) {

	}

	@Override
	public void onUpdate() {
		this.ticksExisted++;
		super.onUpdate();
		this.onMovementTick();
		if (!this.world.isRemote) {
			if (this.ticksExisted > 6000)//5 min timer max for missiles...
			{
				this.setDead();
			} else if (this.ammoType.isTorpedo() && this.ticksExisted > 400)//and much shorter for torpedoes, 10 second lifetime
			{
				this.setDead();
			}
		}
	}

	protected void checkProximity() {
		if (this.motionY > 0) {
			return;//don't bother checking when travelling upwards, wait until the downward swing...
		}
		//check ground.
		int groundDiff = 0;
		int id = 0;
		int x = (int) posX;
		int y = (int) posY;
		int z = (int) posZ;
		boolean impacted = false;
		RayTraceResult hit;
		if (ammoType.groundProximity() > 0) {
			while (id == 0 && groundDiff <= ammoType.groundProximity()) {
				id = world.getBlockId(x, y - groundDiff, z);
				groundDiff++;
				if (id != 0) {
					this.onImpactWorld(new RayTraceResult(x, y, z, 0, Vec3.createVectorHelper(x, y, z)));
					impacted = true;
					break;
				}
			}
		}
		//check entities if not detonated by ground
		if (!impacted && ammoType.entityProximity() > 0) {
			float entProx = ammoType.entityProximity();
			float foundDist = 0;
			List entities = world.getEntitiesWithinAABBExcludingEntity(this,
					AxisAlignedBB.getBoundingBox(posX - entProx, posY - entProx, posZ - entProx, posX + entProx, posY + entProx, posZ + entProx));
			if (entities != null && !entities.isEmpty()) {
				Iterator it = entities.iterator();
				Entity ent;
				while (it.hasNext()) {
					ent = (Entity) it.next();
					if (ent != null && ent.getClass() != MissileBase.class)//don't collide with missiles
					{
						foundDist = this.getDistanceToEntity(ent);
						if (foundDist < entProx) {
							this.onImpactEntity(ent, (float) posX, (float) posY, (float) posZ);
							break;
						}
					}
				}
			}
		}
	}

	public void onMovementTick() {
		if (this.inGround) {
			int id = this.world.getBlockId(blockX, blockY, blockZ);
			int meta = this.world.getBlockMetadata(blockX, blockY, blockZ);
			if (id != blockID || meta != blockMeta) {
				this.motionX = 0;
				this.motionY = 0;
				this.motionZ = 0;
				this.inGround = false;
			}
		}
		if (!this.inGround) {
			Vec3 positionVector = this.world.getWorldVec3Pool().getVecFromPool(this.posX, this.posY, this.posZ);
			Vec3 moveVector = this.world.getWorldVec3Pool().getVecFromPool(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
			RayTraceResult hitPosition = this.world.rayTraceBlocks_do_do(positionVector, moveVector, false, true);
			positionVector = this.world.getWorldVec3Pool().getVecFromPool(this.posX, this.posY, this.posZ);
			moveVector = this.world.getWorldVec3Pool().getVecFromPool(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
			Entity hitEntity = null;
			boolean testEntities = true;
			if (this.world.isRemote) {
				testEntities = false;
			}
			if (testEntities) {
				List nearbyEntities = this.world.getEntitiesWithinAABBExcludingEntity(this,
						this.boundingBox.addCoord(this.motionX, this.motionY, this.motionZ).expand(1.0D, 1.0D, 1.0D));
				double closestHit = 0.0D;
				float borderSize;

				for (int i = 0; i < nearbyEntities.size(); ++i) {
					Entity curEnt = (Entity) nearbyEntities.get(i);
					if (curEnt.canBeCollidedWith()) {
						if (this.launcher != null) {
							if (curEnt == this.launcher || curEnt == this.launcher.riddenByEntity || curEnt == this.shooterLiving || curEnt == this.shooter) {
								continue;
							}
						}
						borderSize = 0.3F;
						AxisAlignedBB var12 = curEnt.boundingBox.expand((double) borderSize, (double) borderSize, (double) borderSize);
						RayTraceResult checkHit = var12.calculateIntercept(positionVector, moveVector);
						if (checkHit != null) {
							double hitDistance = positionVector.distanceTo(checkHit.hitVec);
							if (hitDistance < closestHit || closestHit == 0.0D) {
								hitEntity = curEnt;
								closestHit = hitDistance;
							}
						}
					}
				}
			}
			if (hitEntity != null) {
				hitPosition = new RayTraceResult(hitEntity);
			}
			if (hitPosition != null) {
				if (hitPosition.entityHit != null) {
					this.onImpactEntity(hitPosition.entityHit, (float) posX, (float) posY, (float) posZ);
					this.hasImpacted = true;
					if (!this.ammoType.isPenetrating() && !this.world.isRemote) {
						this.setDead();
					} else if (this.ammoType.isPenetrating()) {
						this.motionX *= 0.65f;
						this.motionY *= 0.65f;
						this.motionZ *= 0.65f;
					}
				} else {
					this.onImpactWorld(hitPosition);
					this.hasImpacted = true;
					if (!this.ammoType.isPenetrating()) {
						this.motionX = (double) ((float) (hitPosition.hitVec.xCoord - this.posX));
						this.motionY = (double) ((float) (hitPosition.hitVec.yCoord - this.posY));
						this.motionZ = (double) ((float) (hitPosition.hitVec.zCoord - this.posZ));
						float var20 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
						this.posX -= this.motionX / (double) var20 * 0.05000000074505806D;
						this.posY -= this.motionY / (double) var20 * 0.05000000074505806D;
						this.posZ -= this.motionZ / (double) var20 * 0.05000000074505806D;
						this.playSound("random.bowhit", 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
						this.inGround = true;
						if (!this.ammoType.isPersistent() && !this.world.isRemote) {
							this.setDead();
						} else if (this.ammoType.isPersistent()) {
							this.blockX = hitPosition.blockX;
							this.blockY = hitPosition.blockY;
							this.blockZ = hitPosition.blockZ;
							this.blockID = this.world.getBlockId(blockX, blockY, blockZ);
							this.blockMeta = this.world.getBlockMetadata(blockX, blockY, blockZ);
						}
					} else {
						this.motionX *= 0.65f;
						this.motionY *= 0.65f;
						this.motionZ *= 0.65f;
					}
				}
			}

			if (this.ammoType.isProximityAmmo() && this.ticksExisted > 20) {
				checkProximity();
				if (this.isDead) {
					return;
				}
			}

			this.posX += this.motionX;
			this.posY += this.motionY;
			this.posZ += this.motionZ;

			if (this.ammoType.isRocket() && this.rocketBurnTime > 0)//if it is a rocket, accellerate if still burning
			{
				this.rocketBurnTime--;
				this.motionX += mX;
				this.motionY += mY;
				this.motionZ += mZ;
				if (this.world.isRemote) {
					this.world.spawnParticle("smoke", this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D);
				}
			} else if (this.ammoType.isTorpedo()) {
				if (this.rocketBurnTime > 0) {
					this.rocketBurnTime--;
					this.motionX += mX;
					this.motionY += mY;
					this.motionZ += mZ;
				}
				if (this.world.isRemote && this.inWater) {
					if (this.inWater) {
						this.world.spawnParticle("bubble", this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D);
					} else {
						this.world.spawnParticle("smoke", this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D);
					}
				}
				if (!this.isInWater()) {
					this.motionY -= (double) this.ammoType.getGravityFactor();
				} else {
					this.motionY *= 0.45f;
					if (Math.abs(this.motionY) < 0.001) {
						this.motionY = 0.f;
					}
				}
			} else {
				this.motionY -= (double) this.ammoType.getGravityFactor();
			}
			this.setPosition(this.posX, this.posY, this.posZ);
			if (this.ammoType.updateAsArrow()) {
				this.onUpdateArrowRotation();
			}
		}
	}

	public void onUpdateArrowRotation() {
		double motionSpeed = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
		this.rotationYaw = Trig.toDegrees((float) Math.atan2(this.motionX, this.motionZ)) - 90;
		this.rotationPitch = Trig.toDegrees((float) Math.atan2(this.motionY, (double) motionSpeed)) - 90;
		while (this.rotationPitch - this.prevRotationPitch < -180.0F) {
			this.prevRotationPitch -= 360.0F;
		}

		while (this.rotationPitch - this.prevRotationPitch >= 180.0F) {
			this.prevRotationPitch += 360.0F;
		}

		while (this.rotationYaw - this.prevRotationYaw < -180.0F) {
			this.prevRotationYaw -= 360.0F;
		}

		while (this.rotationYaw - this.prevRotationYaw >= 180.0F) {
			this.prevRotationYaw += 360.0F;
		}
	}

	@Override
	public void setPositionAndRotation2(double par1, double par3, double par5, float par7, float par8, int par9) {
		this.setPosition(par1, par3, par5);
	}

	public String getTexture() {
		return ammoType.getModelTexture();
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound tag) {
		this.missileType = tag.getInteger("type");
		this.ammoType = AmmoRegistry.instance().getAmmoEntry(missileType);
		this.inGround = tag.getBoolean("inGround");
		this.blockX = tag.getInteger("bX");
		this.blockY = tag.getInteger("bY");
		this.blockZ = tag.getInteger("bZ");
		this.blockID = tag.getInteger("bID");
		this.blockMeta = tag.getInteger("bMd");
		this.ticksExisted = tag.getInteger("ticks");
		this.mX = tag.getFloat("mX");
		this.mY = tag.getFloat("mY");
		this.mZ = tag.getFloat("mZ");
		if (this.ammoType == null) {
			this.ammoType = Ammo.ammoArrow;
		}
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound tag) {
		tag.setInteger("type", missileType);
		tag.setBoolean("inGround", this.inGround);
		tag.setInteger("bX", this.blockX);
		tag.setInteger("bY", this.blockY);
		tag.setInteger("bZ", this.blockZ);
		tag.setInteger("bID", this.blockID);
		tag.setInteger("bMd", this.blockMeta);
		tag.setInteger("ticks", this.ticksExisted);
		tag.setFloat("mX", this.mX);
		tag.setFloat("mY", this.mY);
		tag.setFloat("mZ", this.mZ);
	}

	@Override
	protected void entityInit() {
	}

	@Override
	public void writeSpawnData(ByteArrayDataOutput data) {
		data.writeInt(missileType);
		data.writeFloat(rotationYaw);
		data.writeFloat(rotationPitch);
		data.writeBoolean(inGround);
		data.writeInt(blockX);
		data.writeInt(blockY);
		data.writeInt(blockZ);
		data.writeInt(blockID);
		data.writeInt(blockMeta);
		data.writeInt(rocketBurnTime);
		data.writeBoolean(this.launcher != null);
		if (this.launcher != null) {
			data.writeInt(this.launcher.entityId);
		}
	}

	@Override
	public void readSpawnData(ByteArrayDataInput data) {
		this.missileType = data.readInt();
		this.ammoType = AmmoRegistry.instance().getAmmoEntry(missileType);
		if (this.ammoType == null) {
			this.ammoType = Ammo.ammoArrow;
		}
		this.prevRotationYaw = this.rotationYaw = data.readFloat();
		this.prevRotationPitch = this.rotationPitch = data.readFloat();
		this.inGround = data.readBoolean();
		this.blockX = data.readInt();
		this.blockY = data.readInt();
		this.blockZ = data.readInt();
		this.blockID = data.readInt();
		this.blockMeta = data.readInt();
		this.rocketBurnTime = data.readInt();
		boolean hasLauncher = data.readBoolean();
		if (hasLauncher) {
			Entity launcher = world.getEntityByID(data.readInt());
		}
	}
}
