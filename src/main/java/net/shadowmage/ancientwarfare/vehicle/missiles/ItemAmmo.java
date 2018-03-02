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

import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.entity.AWFakePlayer;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.core.util.EntityTools;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;
import net.shadowmage.ancientwarfare.vehicle.config.AWVehicleStatics;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleBase;
import net.shadowmage.ancientwarfare.vehicle.item.ItemBaseVehicle;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public abstract class ItemAmmo extends ItemBaseVehicle implements IAmmo {

	private static Random rng = new Random();
	public static ItemAmmo[] ammoTypes = new ItemAmmo[64];//starting with 64 types...can/will expand as needed

	/**
	 * procedure to make new ammo type:
	 * create ammo class
	 * create static instance below (or anywhere really)
	 * register the render in renderRegistry (or register it with renderregistry during startup)
	 * add ammo to applicable vehicle type constructors
	 */

	public static ItemAmmo ammoBallShot = new ItemAmmoBallShot(52);// has to be declared first, because others depend on it...
	public static ItemAmmo ammoBallIronShot = new ItemAmmoIronBallShot(53);

	public static ItemAmmo ammoStoneShot10 = new ItemAmmoStoneShot(0, 10);//
	public static ItemAmmo ammoStoneShot15 = new ItemAmmoStoneShot(1, 15);
	public static ItemAmmo ammoStoneShot30 = new ItemAmmoStoneShot(2, 30);
	public static ItemAmmo ammoStoneShot45 = new ItemAmmoStoneShot(3, 45);
	public static ItemAmmo ammoFireShot10 = new ItemAmmoFlameShot(4, 10);//
	public static ItemAmmo ammoFireShot15 = new ItemAmmoFlameShot(5, 15);
	public static ItemAmmo ammoFireShot30 = new ItemAmmoFlameShot(6, 30);
	public static ItemAmmo ammoFireShot45 = new ItemAmmoFlameShot(7, 45);
	public static ItemAmmo ammoExplosive10 = new ItemAmmoExplosiveShot(8, 10, false);//
	public static ItemAmmo ammoExplosive15 = new ItemAmmoExplosiveShot(9, 15, false);
	public static ItemAmmo ammoExplosive30 = new ItemAmmoExplosiveShot(10, 30, false);
	public static ItemAmmo ammoExplosive45 = new ItemAmmoExplosiveShot(11, 45, false);
	public static ItemAmmo ammoHE10 = new ItemAmmoExplosiveShot(12, 10, true);//
	public static ItemAmmo ammoHE15 = new ItemAmmoExplosiveShot(13, 15, true);
	public static ItemAmmo ammoHE30 = new ItemAmmoExplosiveShot(14, 30, true);
	public static ItemAmmo ammoHE45 = new ItemAmmoExplosiveShot(15, 45, true);
	public static ItemAmmo ammoNapalm10 = new ItemAmmoNapalmShot(16, 10);//
	public static ItemAmmo ammoNapalm15 = new ItemAmmoNapalmShot(17, 15);
	public static ItemAmmo ammoNapalm30 = new ItemAmmoNapalmShot(18, 30);
	public static ItemAmmo ammoNapalm45 = new ItemAmmoNapalmShot(19, 45);
	public static ItemAmmo ammoClusterShot10 = new ItemAmmoClusterShot(20, 10);//
	public static ItemAmmo ammoClusterShot15 = new ItemAmmoClusterShot(21, 15);
	public static ItemAmmo ammoClusterShot30 = new ItemAmmoClusterShot(22, 30);
	public static ItemAmmo ammoClusterShot45 = new ItemAmmoClusterShot(23, 45);
	public static ItemAmmo ammoPebbleShot10 = new ItemAmmoPebbleShot(24, 10);//
	public static ItemAmmo ammoPebbleShot15 = new ItemAmmoPebbleShot(25, 15);
	public static ItemAmmo ammoPebbleShot30 = new ItemAmmoPebbleShot(26, 30);
	public static ItemAmmo ammoPebbleShot45 = new ItemAmmoPebbleShot(27, 45);
	public static ItemAmmo ammoIronShot5 = new ItemAmmoIronShot(28, 5, 10);//
	public static ItemAmmo ammoIronShot10 = new ItemAmmoIronShot(29, 10, 15);
	public static ItemAmmo ammoIronShot15 = new ItemAmmoIronShot(30, 15, 30);
	public static ItemAmmo ammoIronShot25 = new ItemAmmoIronShot(31, 25, 45);
	public static ItemAmmo ammoCanisterShot5 = new ItemAmmoCanisterShot(32, 5);//
	public static ItemAmmo ammoCanisterShot10 = new ItemAmmoCanisterShot(33, 10);
	public static ItemAmmo ammoCanisterShot15 = new ItemAmmoCanisterShot(34, 15);
	public static ItemAmmo ammoCanisterShot25 = new ItemAmmoCanisterShot(35, 25);
	public static ItemAmmo ammoGrapeShot5 = new ItemAmmoGrapeShot(36, 5);//
	public static ItemAmmo ammoGrapeShot10 = new ItemAmmoGrapeShot(37, 10);
	public static ItemAmmo ammoGrapeShot15 = new ItemAmmoGrapeShot(38, 15);
	public static ItemAmmo ammoGrapeShot25 = new ItemAmmoGrapeShot(39, 25);
	public static ItemAmmo ammoArrow = new ItemAmmoArrow(40);//
	public static ItemAmmo ammoArrowFlame = new ItemAmmoArrowFlame(41);//
	public static ItemAmmo ammoArrowIron = new ItemAmmoArrowIron(42);//
	public static ItemAmmo ammoArrowIronFlame = new ItemAmmoArrowIronFlame(43);//
	public static ItemAmmo ammoBallistaBolt = new ItemAmmoBallistaBolt(44);
	public static ItemAmmo ammoBallistaBoltFlame = new ItemAmmoBallistaBoltFlame(45);//
	public static ItemAmmo ammoBallistaBoltExplosive = new ItemAmmoBallistaBoltExplosive(46);//
	public static ItemAmmo ammoBallistaBoltIron = new ItemAmmoBallistaBoltIron(47);//
	public static ItemAmmo ammoRocket = new ItemAmmoHwachaRocket(48);
	public static ItemAmmo ammoHwachaRocketFlame = new ItemAmmoHwachaRocketFlame(49);
	public static ItemAmmo ammoHwachaRocketExplosive = new ItemAmmoHwachaRocketExplosive(50);
	public static ItemAmmo ammoHwachaRocketAirburst = new ItemAmmoHwachaRocketAirburst(51);
	//52 stoneBallShot (decl. above)
	//53 ironBallShot (decl. above)
	public static ItemAmmo ammoSoldierArrowWood = new ItemAmmoSoldierArrow(54, 5, false);
	public static ItemAmmo ammoSoldierArrowIron = new ItemAmmoSoldierArrow(55, 7, false);
	public static ItemAmmo ammoSoldierArrowWoodFlame = new ItemAmmoSoldierArrow(56, 5, true);
	public static ItemAmmo ammoSoldierArrowIronFlame = new ItemAmmoSoldierArrow(57, 7, true);
	public static ItemAmmo ammoTorpedo10 = new ItemAmmoTorpedo(58, 10);
	public static ItemAmmo ammoTorpedo15 = new ItemAmmoTorpedo(59, 15);
	public static ItemAmmo ammoTorpedo30 = new ItemAmmoTorpedo(60, 30);
	public static ItemAmmo ammoTorpedo45 = new ItemAmmoTorpedo(61, 45);
	//62-63 reserved for future ammo types

	int entityDamage;
	int vehicleDamage;
	static final float gravityFactor = 9.81f * 0.05f * 0.05f;
	String displayName = "AW.Ammo";
	String configName = "none";
	List<String> displayTooltip = new ArrayList<String>();
	ResourceLocation modelTexture = TextureMap.LOCATION_MISSING_TEXTURE;
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

	private String regName;

	public ItemAmmo(String regName) {
		super(regName);

		this.regName = regName;
		setUnlocalizedName("ammo." + regName);
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		tooltip.add(I18n.format("ammo." + regName + ".tooltip"));
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

/* TODO rendering
	@Override
	public String getIconTexture() {
		return "ancientwarfare:ammo/" + iconTexture;
	}
*/

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
