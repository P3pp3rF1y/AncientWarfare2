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
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.entity.AWFakePlayer;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.core.util.EntityTools;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;
import net.shadowmage.ancientwarfare.vehicle.config.AWVehicleStatics;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleBase;
import net.shadowmage.ancientwarfare.vehicle.item.AWVehicleItems;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public abstract class Ammo implements IAmmo {

	private static Random rng = new Random();
	public static Ammo[] ammoTypes = new Ammo[64];//starting with 64 types...can/will expand as needed

	/**
	 * procedure to make new ammo type:
	 * create ammo class
	 * create static instance below (or anywhere really)
	 * register the render in renderRegistry (or register it with renderregistry during startup)
	 * add ammo to applicable vehicle type constructors
	 */

	public static Ammo ammoBallShot = new AmmoBallShot(52);// has to be declared first, because others depend on it...
	public static Ammo ammoBallIronShot = new AmmoIronBallShot(53);

	public static Ammo ammoStoneShot10 = new AmmoStoneShot(0, 10);//
	public static Ammo ammoStoneShot15 = new AmmoStoneShot(1, 15);
	public static Ammo ammoStoneShot30 = new AmmoStoneShot(2, 30);
	public static Ammo ammoStoneShot45 = new AmmoStoneShot(3, 45);
	public static Ammo ammoFireShot10 = new AmmoFlameShot(4, 10);//
	public static Ammo ammoFireShot15 = new AmmoFlameShot(5, 15);
	public static Ammo ammoFireShot30 = new AmmoFlameShot(6, 30);
	public static Ammo ammoFireShot45 = new AmmoFlameShot(7, 45);
	public static Ammo ammoExplosive10 = new AmmoExplosiveShot(8, 10, false);//
	public static Ammo ammoExplosive15 = new AmmoExplosiveShot(9, 15, false);
	public static Ammo ammoExplosive30 = new AmmoExplosiveShot(10, 30, false);
	public static Ammo ammoExplosive45 = new AmmoExplosiveShot(11, 45, false);
	public static Ammo ammoHE10 = new AmmoExplosiveShot(12, 10, true);//
	public static Ammo ammoHE15 = new AmmoExplosiveShot(13, 15, true);
	public static Ammo ammoHE30 = new AmmoExplosiveShot(14, 30, true);
	public static Ammo ammoHE45 = new AmmoExplosiveShot(15, 45, true);
	public static Ammo ammoNapalm10 = new AmmoNapalmShot(16, 10);//
	public static Ammo ammoNapalm15 = new AmmoNapalmShot(17, 15);
	public static Ammo ammoNapalm30 = new AmmoNapalmShot(18, 30);
	public static Ammo ammoNapalm45 = new AmmoNapalmShot(19, 45);
	public static Ammo ammoClusterShot10 = new AmmoClusterShot(20, 10);//
	public static Ammo ammoClusterShot15 = new AmmoClusterShot(21, 15);
	public static Ammo ammoClusterShot30 = new AmmoClusterShot(22, 30);
	public static Ammo ammoClusterShot45 = new AmmoClusterShot(23, 45);
	public static Ammo ammoPebbleShot10 = new AmmoPebbleShot(24, 10);//
	public static Ammo ammoPebbleShot15 = new AmmoPebbleShot(25, 15);
	public static Ammo ammoPebbleShot30 = new AmmoPebbleShot(26, 30);
	public static Ammo ammoPebbleShot45 = new AmmoPebbleShot(27, 45);
	public static Ammo ammoIronShot5 = new AmmoIronShot(28, 5, 10);//
	public static Ammo ammoIronShot10 = new AmmoIronShot(29, 10, 15);
	public static Ammo ammoIronShot15 = new AmmoIronShot(30, 15, 30);
	public static Ammo ammoIronShot25 = new AmmoIronShot(31, 25, 45);
	public static Ammo ammoCanisterShot5 = new AmmoCanisterShot(32, 5);//
	public static Ammo ammoCanisterShot10 = new AmmoCanisterShot(33, 10);
	public static Ammo ammoCanisterShot15 = new AmmoCanisterShot(34, 15);
	public static Ammo ammoCanisterShot25 = new AmmoCanisterShot(35, 25);
	public static Ammo ammoGrapeShot5 = new AmmoGrapeShot(36, 5);//
	public static Ammo ammoGrapeShot10 = new AmmoGrapeShot(37, 10);
	public static Ammo ammoGrapeShot15 = new AmmoGrapeShot(38, 15);
	public static Ammo ammoGrapeShot25 = new AmmoGrapeShot(39, 25);
	public static Ammo ammoArrow = new AmmoArrow(40);//
	public static Ammo ammoArrowFlame = new AmmoArrowFlame(41);//
	public static Ammo ammoArrowIron = new AmmoArrowIron(42);//
	public static Ammo ammoArrowIronFlame = new AmmoArrowIronFlame(43);//
	public static Ammo ammoBallistaBolt = new AmmoBallistaBolt(44);
	public static Ammo ammoBallistaBoltFlame = new AmmoBallistaBoltFlame(45);//
	public static Ammo ammoBallistaBoltExplosive = new AmmoBallistaBoltExplosive(46);//
	public static Ammo ammoBallistaBoltIron = new AmmoBallistaBoltIron(47);//
	public static Ammo ammoRocket = new AmmoHwachaRocket(48);
	public static Ammo ammoHwachaRocketFlame = new AmmoHwachaRocketFlame(49);
	public static Ammo ammoHwachaRocketExplosive = new AmmoHwachaRocketExplosive(50);
	public static Ammo ammoHwachaRocketAirburst = new AmmoHwachaRocketAirburst(51);
	//52 stoneBallShot (decl. above)
	//53 ironBallShot (decl. above)
	public static Ammo ammoSoldierArrowWood = new AmmoSoldierArrow(54, 5, false);
	public static Ammo ammoSoldierArrowIron = new AmmoSoldierArrow(55, 7, false);
	public static Ammo ammoSoldierArrowWoodFlame = new AmmoSoldierArrow(56, 5, true);
	public static Ammo ammoSoldierArrowIronFlame = new AmmoSoldierArrow(57, 7, true);
	public static Ammo ammoTorpedo10 = new AmmoTorpedo(58, 10);
	public static Ammo ammoTorpedo15 = new AmmoTorpedo(59, 15);
	public static Ammo ammoTorpedo30 = new AmmoTorpedo(60, 30);
	public static Ammo ammoTorpedo45 = new AmmoTorpedo(61, 45);
	//62-63 reserved for future ammo types

	private final ItemStack ammoStack;
	public final int ammoType;
	int entityDamage;
	int vehicleDamage;
	static final float gravityFactor = 9.81f * 0.05f * 0.05f;
	String displayName = "AW.Ammo";
	String configName = "none";
	List<String> displayTooltip = new ArrayList<String>();
	String modelTexture = "foo.png";
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
	IAmmo secondaryAmmoType = null;
	int secondaryAmmoCount = 0;
	String iconTexture = "foo";
	int numCrafted = 10;

	public Ammo(int ammoType) {
		this.ammoType = ammoType;
		this.ammoStack = new ItemStack(AWVehicleItems.ammo, 1, ammoType);
		if (ammoType >= 0 && ammoType < ammoTypes.length) {
			ammoTypes[ammoType] = this;
		}
		this.displayName = "ammo." + ammoType;
		this.displayTooltip.add("ammo." + ammoType + ".tooltip");
	}

	public void addTooltip(String tip) {
		this.displayTooltip.add(tip);
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
	public int getAmmoType() {
		return this.ammoType;
	}

	@Override
	public ItemStack getDisplayStack() {
		return this.ammoStack;
	}

	@Override
	public ItemStack getAmmoStack(int qty) {
		return new ItemStack(AWVehicleItems.ammo, qty, this.getAmmoType());
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
	public String getModelTexture() {
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
		return secondaryAmmoType;
	}

	@Override
	public int getSecondaryAmmoTypeCount() {
		return secondaryAmmoCount;
	}

	@Override
	public boolean hasSecondaryAmmo() {
		return this.secondaryAmmoType != null;
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

	@Override
	public String getIconTexture() {
		return "ancientwarfare:ammo/" + iconTexture;
	}

/* TODO ammo recipes
	@Override
	public ResourceListRecipe constructRecipe() {
		if (!this.isCraftable || !this.isEnabled) {
			return null;
		}
		ResourceListRecipe recipe = new ResourceListRecipe(getAmmoStack(this.getNumCrafted()), RecipeType.AMMO);
		recipe.addNeededResearch(getNeededResearch());
		recipe.addResources(getResources());
		return recipe;
	}

	@Override
	public Collection<Integer> getNeededResearch() {
		return this.neededResearch;
	}

	@Override
	public void addResearch(Integer num) {
		this.neededResearch.add(num);
	}

	@Override
	public void addResearch(IResearchGoal goal) {
		this.neededResearch.add(goal.getGlobalResearchNum());
	}

	@Override
	public Collection<ItemStackWrapperCrafting> getResources() {
		return this.resources;
	}

	@Override
	public int getNumCrafted() {
		return this.numCrafted;
	}
*/

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

	protected void spawnGroundBurst(World world, float x, float y, float z, float maxVelocity, IAmmo type, int count, float minPitch, int sideHit,
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
				if (sideHit == 1 || sideHit == 0) {
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
	private float getMinYaw(int side) {
		switch (side) {
			case 2://north
				return 360 - 45;
			case 3://south
				return 180 - 45;
			case 4://west
				return 90 - 45;
			case 5://east
				return 270 - 45;
		}
		return 0;
	}

	private float getMaxYaw(int side) {
		switch (side) {
			case 2://north
				return 360 + 45;
			case 3://south
				return 180 + 45;
			case 4://west
				return 90 + 45;
			case 5://east
				return 270 + 45;
		}
		return 0;
	}

/* TODO ammo rendering
	@Override
	public Icon getDisplayIcon() {
		Description d = DescriptionRegistry2.instance().getDescriptionFor(ItemLoader.ammoItem.itemID);
		if (d != null) {
			return d.getIconFor(getAmmoType());
		}
		return null;
	}
*/

	protected void spawnAirBurst(World world, float x, float y, float z, float maxVelocity, IAmmo type, int count, Entity shooter) {
		spawnGroundBurst(world, x, y, z, maxVelocity, type, count, -90, 0, shooter);
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
