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

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.entity.AWFakePlayer;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.core.util.EntityTools;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;
import net.shadowmage.ancientwarfare.vehicle.AncientWarfareVehicles;
import net.shadowmage.ancientwarfare.vehicle.config.AWVehicleStatics;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleBase;

import javax.annotation.Nonnull;
import java.util.Random;
import java.util.UUID;

public abstract class Ammo implements IAmmo {

	private static Random rng = new Random();
	public static Ammo[] ammoTypes = new Ammo[64];//starting with 64 types...can/will expand as needed

	//62-63 reserved for future ammo types

	int entityDamage;
	int vehicleDamage;
	static final float gravityFactor = 9.81f * 0.05f * 0.05f;
	String displayName = "AW.Ammo";
	String configName = "none";
	ResourceLocation modelTexture = new ResourceLocation("missingno");
	boolean isRocket = false;
	boolean isArrow = false;
	boolean isPersistent = false;
	boolean isFlaming = false;
	boolean isPenetrating = false;
	boolean isProximityAmmo = false;
	boolean isCraftable = true;
	boolean isEnabled = true;
	boolean isTorpedo = false;
	float groundProximity = 0.f;
	float entityProximity = 0.f;
	float ammoWeight = 10;
	float renderScale = 1.f;
	int secondaryAmmoCount = 0;

	private ResourceLocation registryName;

	public Ammo(String regName) {
		registryName = new ResourceLocation(AncientWarfareVehicles.modID, regName);
	}

	@Override
	public ResourceLocation getRegistryName() {
		return registryName;
	}

	@Override
	public void setEntityDamage(int damage) {
		if (damage < 0) {
			damage = 0;
		}
		this.entityDamage = damage;
	}

	@Override
	public void setVehicleDamage(int damage) {
		if (damage < 0) {
			damage = 0;
		}
		this.vehicleDamage = damage;
	}

	@Override
	public String getConfigName() {
		return configName;
	}

	@Override
	public boolean isEnabled() {
		return isEnabled;
	}

	@Override
	public void setEnabled(boolean val) {
		this.isEnabled = val;
	}

	@Override
	public boolean isTorpedo() {
		return this.isTorpedo;
	}

	@Override
	public ResourceLocation getModelTexture() {
		return modelTexture;
	}

	@Override
	public boolean isRocket() {
		return isRocket;
	}

	@Override
	public boolean isPersistent() {
		return isPersistent;
	}

	@Override
	public boolean updateAsArrow() {
		return isArrow;
	}

	@Override
	public boolean isAvailableAsItem() {
		return this.isCraftable;
	}

	@Override
	public float getAmmoWeight() {
		return ammoWeight;
	}

	/**
	 * override to implement upgrade-specific ammo use.  this method will be checked before a vehicle will accept ammo into its bay, or fire ammo from its bay
	 */
	@Override
	public boolean isAmmoValidFor(VehicleBase vehicle) {
		return true;
	}

	@Override
	public float getRenderScale() {
		return renderScale;
	}

	@Override
	public float getGravityFactor() {
		return gravityFactor;
	}

	@Override
	public int getEntityDamage() {
		return entityDamage;
	}

	@Override
	public int getVehicleDamage() {
		return vehicleDamage;
	}

	@Override
	public boolean isFlaming() {
		return isFlaming;
	}

	@Override
	public boolean isPenetrating() {
		return isPenetrating;
	}

	@Override
	public IAmmo getSecondaryAmmoType() {
		return null;
	}

	@Override
	public int getSecondaryAmmoTypeCount() {
		return secondaryAmmoCount;
	}

	@Override
	public boolean hasSecondaryAmmo() {
		return false;
	}

	@Override
	public boolean isProximityAmmo() {
		return isProximityAmmo;
	}

	@Override
	public float entityProximity() {
		return entityProximity;
	}

	@Override
	public float groundProximity() {
		return groundProximity;
	}

	protected void breakBlockAndDrop(World world, int x, int y, int z) {
		if (!AWVehicleStatics.blockDestruction /*|| !WarzoneManager.instance().shouldBreakBlock(world, x, y, z) TODO warzone implementation?*/) {
			return;
		}
		BlockTools.breakBlockAndDrop(world, AWFakePlayer.get(world), new BlockPos(x, y, z));
	}

	/**
	 * starts at y, works down to find the first solid block, and ignites the one above it (set to fire)
	 *
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 */
	protected void igniteBlock(World world, int x, int y, int z, int maxSearch) {
		if (!AWVehicleStatics.blockFires) {
			return;
		}
		for (int i = 0; i < maxSearch && y - i >= 1; i++) {
			BlockPos curPos = new BlockPos(x, y - i, z);
			if (!world.isAirBlock(curPos) && world.isAirBlock(curPos.up())) {
				world.setBlockState(curPos.up(), Blocks.FIRE.getDefaultState(), 3);
			}
		}
	}

	public static boolean shouldEffectEntity(World world, Entity entity, MissileBase missile) {
		if (!AWVehicleStatics.allowFriendlyFire && missile.shooterLiving instanceof NpcBase) {
			@Nonnull NpcBase npc = ((NpcBase) missile.shooterLiving);
			UUID otherId = null;
			String otherName = "";
			if (entity instanceof NpcBase) {
				otherId = ((NpcBase) entity).getOwnerUuid();
				otherName = ((NpcBase) entity).getOwnerName();
			} else if (entity instanceof EntityPlayer) {
				otherId = entity.getUniqueID();
				otherName = entity.getName();
			}
			return !EntityTools.isOwnerOrSameTeam(npc.getOwnerUuid(), npc.getOwnerName(), otherId, otherName);
		}
		return true;
	}

	/**
	 * starts at y, works down to find the first solid block, and ignites the one above it (set to fire)
	 *
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 */
	protected void setBlockToLava(World world, int x, int y, int z, int maxSearch) {
		if (!AWVehicleStatics.blockFires) {
			return;
		}
		int id;
		for (int i = 0; i < maxSearch && y - i >= 1; i++) {
			BlockPos curPos = new BlockPos(x, y - i, z);
			if (!world.isAirBlock(curPos)) {
				if (world.isAirBlock(curPos.up())) {
					world.setBlockState(curPos.up(), Blocks.LAVA.getDefaultState(), 3);
				}
				break;
			}
		}
	}

	protected void createExplosion(World world, MissileBase missile, float x, float y, float z, float power) {
		boolean destroyBlocks = AWVehicleStatics.blockDestruction;
		boolean fires = AWVehicleStatics.blockFires;

		Explosion explosion = new Explosion(world, missile, x, y, z, power, fires, destroyBlocks);

		explosion.doExplosionA();

		explosion.doExplosionB(true);
	}

	protected void spawnGroundBurst(World world, float x, float y, float z, float maxVelocity, IAmmo type, int count, float minPitch, EnumFacing sideHit,
			Entity shooter) {
		if (type != null && !world.isRemote) {
			world.newExplosion(null, x, y, z, 0.25f, false, true);
			createExplosion(world, null, x, y, z, 1.f);
			MissileBase missile;
			float randRange = 90 - minPitch;
			float randVelocity = 0;
			float pitch = 0;
			float yaw = 0;
			float velocity = 0;
			if (type.hasSecondaryAmmo()) {
				count = type.getSecondaryAmmoTypeCount();
				type = type.getSecondaryAmmoType();
			}
			for (int i = 0; i < count; i++) {
				if (sideHit.getAxis().isVertical()) {
					pitch = 90 - (rng.nextFloat() * randRange);
					yaw = rng.nextFloat() * 360.f;
					randVelocity = rng.nextFloat();
					randVelocity = randVelocity < 0.5f ? 0.5f : randVelocity;
					velocity = maxVelocity * randVelocity;
				} else {
					float minYaw = getMinYaw(sideHit);
					float maxYaw = getMaxYaw(sideHit);
					if (minYaw > maxYaw) {
						float tmp = maxYaw;
						maxYaw = minYaw;
						minYaw = tmp;
					}
					float yawRange = maxYaw - minYaw;
					pitch = 90 - (rng.nextFloat() * randRange);
					yaw = minYaw + (rng.nextFloat() * yawRange);
					randVelocity = rng.nextFloat();
					randVelocity = randVelocity < 0.5f ? 0.5f : randVelocity;
					velocity = maxVelocity * randVelocity;
				}
				missile = getMissileByType(type, world, x, y, z, yaw, pitch, velocity, shooter);
				if (missile != null) {
					world.spawnEntity(missile);
				}
			}
		}
	}

	//n=0 :: 2
	//e=-90/270 :: 5
	//s=-180/180 :: 3
	//w=-270/90 :: 4
	private float getMinYaw(EnumFacing side) {
		switch (side) {
			case NORTH://north
				return 360 - 45;
			case SOUTH://south
				return 180 - 45;
			case WEST://west
				return 90 - 45;
			case EAST://east
				return 270 - 45;
		}
		return 0;
	}

	private float getMaxYaw(EnumFacing side) {
		switch (side) {
			case NORTH://north
				return 360 + 45;
			case SOUTH://south
				return 180 + 45;
			case WEST://west
				return 90 + 45;
			case EAST://east
				return 270 + 45;
		}
		return 0;
	}

	protected void spawnAirBurst(World world, float x, float y, float z, float maxVelocity, IAmmo type, int count, Entity shooter) {
		spawnGroundBurst(world, x, y, z, maxVelocity, type, count, -90, EnumFacing.DOWN, shooter);
	}

	public MissileBase getMissileByType(IAmmo type, World world, float x, float y, float z, float yaw, float pitch, float velocity, Entity shooter) {
		if (type == null) {
			return null;
		}
		MissileBase missile = new MissileBase(world);
		missile.setShooter(shooter);
		missile.setMissileParams2(type, x, y, z, yaw, pitch, velocity);
		return missile;
	}
}
