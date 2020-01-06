package net.shadowmage.ancientwarfare.vehicle.VehicleVarHelpers;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.structure.entity.EntityGate;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleBase;
import net.shadowmage.ancientwarfare.vehicle.entity.types.VehicleTypeBatteringRam;
import net.shadowmage.ancientwarfare.vehicle.helpers.VehicleFiringVarsHelper;
import net.shadowmage.ancientwarfare.vehicle.init.AWVehicleSounds;
import net.shadowmage.ancientwarfare.vehicle.missiles.DamageType;

import java.util.List;
import java.util.Random;

public class BatteringRamVarHelper extends VehicleFiringVarsHelper {

	float logAngle = 0.f;
	float logSpeed = 0.f;

	/**
	 * @param vehicle
	 */
	public BatteringRamVarHelper(VehicleBase vehicle) {
		super(vehicle);
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setFloat("lA", logAngle);
		tag.setFloat("lS", logSpeed);
		return tag;
	}

	@Override
	public void deserializeNBT(NBTTagCompound tag) {
		logAngle = tag.getFloat("lA");
		logSpeed = tag.getFloat("lS");
	}

	@Override
	public void onFiringUpdate() {
		if (logAngle >= 30) {
			vehicle.firingHelper.startLaunching();
			logSpeed = 0;
		} else {
			logAngle++;
			logSpeed = 1;
		}
	}

	@Override
	public void onReloadUpdate() {
		if (logAngle < 0) {
			logAngle++;
			logSpeed = 1;
		} else {
			logAngle = 0;
			logSpeed = 0;
		}
	}

	@Override
	public void onLaunchingUpdate() {
		if (logAngle <= -30) {
			vehicle.firingHelper.setFinishedLaunching();
			doDamageEffects();
			logSpeed = 0;
		} else {
			logAngle -= 2;
			logSpeed = -2;
		}
	}

	public void doDamageEffects() {
		if (vehicle.world.isRemote) {
			return;
		}
		BlockPos[] effectedPositions = VehicleTypeBatteringRam.getEffectedPositions(vehicle);
		AxisAlignedBB bb;
		List<Entity> hitEntities;
		for (BlockPos pos : effectedPositions) {
			if (pos == null) {
				continue;
			}
			bb = new AxisAlignedBB(pos, pos.add(1, 1, 1));
			hitEntities = vehicle.world.getEntitiesWithinAABBExcludingEntity(vehicle, bb);
			if (hitEntities != null) {
				boolean firstGateBlock = true; // only used if a gate was hit
				for (Entity ent : hitEntities) {
					System.out.println("entity: " + ent);
					ent.attackEntityFrom(DamageType.batteringDamage, 5 + vehicle.vehicleMaterialLevel);
					if (ent instanceof EntityGate) {
						String gateTypeName = (((EntityGate) ent).gateType.getVariant().toString().toLowerCase());
						if (gateTypeName.contains("wood") && firstGateBlock) {
							ent.playSound(AWVehicleSounds.BATTERING_RAM_HIT_WOOD, 3, 1);
							firstGateBlock = false; // makes playing the sound only once, the gate can hit the Gate entity multiple times at once
						} else if (gateTypeName.contains("iron") && firstGateBlock) {
							System.out.println("sound played");
							ent.playSound(AWVehicleSounds.BATTERING_RAM_HIT_IRON, 3, 1);
							firstGateBlock = false;
						}
					}
				}
			}
			// nerfing the battering ram to only break a block with a 25% chance
			Random rand = new Random();
			if (rand.nextDouble() < 0.20) {
				BlockTools.breakBlockAndDrop(vehicle.world, pos);
			}
		}
	}

	@Override
	public void onReloadingFinished() {
		logAngle = 0;
		logSpeed = 0;
	}

	@Override
	public float getVar1() {
		return logAngle;
	}

	@Override
	public float getVar2() {
		return logSpeed;
	}

	@Override
	public float getVar3() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getVar4() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getVar5() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getVar6() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getVar7() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getVar8() {
		// TODO Auto-generated method stub
		return 0;
	}

}
