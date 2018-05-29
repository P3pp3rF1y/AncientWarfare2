package net.shadowmage.ancientwarfare.npc.entity.vehicle;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIRestrictOpenDoor;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWatchClosest2;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIAttackNearest;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIDoor;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIFollowPlayer;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIHurt;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIWander;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIWatchClosest;
import net.shadowmage.ancientwarfare.npc.ai.owned.NpcAIPlayerOwnedAlarmResponse;
import net.shadowmage.ancientwarfare.npc.ai.owned.NpcAIPlayerOwnedCommander;
import net.shadowmage.ancientwarfare.npc.ai.owned.NpcAIPlayerOwnedFollowCommand;
import net.shadowmage.ancientwarfare.npc.ai.owned.NpcAIPlayerOwnedGetFood;
import net.shadowmage.ancientwarfare.npc.ai.owned.NpcAIPlayerOwnedIdleWhenHungry;
import net.shadowmage.ancientwarfare.npc.ai.vehicle.NpcAIAimVehicle;
import net.shadowmage.ancientwarfare.npc.ai.vehicle.NpcAIDismountVehicle;
import net.shadowmage.ancientwarfare.npc.ai.vehicle.NpcAIFindVehicle;
import net.shadowmage.ancientwarfare.npc.ai.vehicle.NpcAIFireVehicle;
import net.shadowmage.ancientwarfare.npc.ai.vehicle.NpcAIMountVehicle;
import net.shadowmage.ancientwarfare.npc.entity.NpcPlayerOwned;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleBase;

import javax.annotation.Nullable;
import java.util.Optional;

public class NpcSiegeEngineer extends NpcPlayerOwned implements IVehicleUser {
	@Nullable
	private VehicleBase vehicle = null;

	public NpcSiegeEngineer(World world) {
		super(world);

		this.tasks.addTask(0, new EntityAISwimming(this));
		this.tasks.addTask(0, new EntityAIRestrictOpenDoor(this));
		this.tasks.addTask(0, new NpcAIDoor(this, true));
		this.tasks.addTask(2, new NpcAIFollowPlayer(this));
		this.tasks.addTask(2, new NpcAIPlayerOwnedFollowCommand(this));
		this.tasks.addTask(3, new NpcAIPlayerOwnedAlarmResponse(this));
		this.tasks.addTask(4, new NpcAIPlayerOwnedGetFood(this));
		this.tasks.addTask(5, new NpcAIPlayerOwnedIdleWhenHungry(this));
		this.tasks.addTask(6, new NpcAIDismountVehicle<>(this));
		this.tasks.addTask(6, new NpcAIFindVehicle<>(this));
		this.tasks.addTask(7, new NpcAIMountVehicle<>(this));
		this.tasks.addTask(8, new NpcAIAimVehicle<>(this));
		this.tasks.addTask(9, new NpcAIFireVehicle<>(this));

		this.tasks.addTask(101, new EntityAIWatchClosest2(this, EntityPlayer.class, 3.0F, 1.0F));
		this.tasks.addTask(102, new NpcAIWander(this) {
			@Override
			public boolean shouldExecute() {
				return !isRidingVehicle() && super.shouldExecute();
			}
		});
		this.tasks.addTask(103, new NpcAIWatchClosest(this, EntityLiving.class, 8.0F));

		this.targetTasks.addTask(0, new NpcAIPlayerOwnedCommander(this));
		this.targetTasks.addTask(1, new NpcAIHurt(this));
		this.targetTasks.addTask(2, new NpcAIAttackNearest(this, this::isHostileTowards));
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(60D);
	}

	@Override
	public String getNpcSubType() {
		return "";
	}

	@Override
	public String getNpcType() {
		return "siege.engineer";
	}

	@Override
	public boolean canBeAttackedBy(Entity e) {
		return true;
	}

	@Override
	public Optional<VehicleBase> getVehicle() {
		if (vehicle == null || vehicle.isDead) {
			return Optional.empty();
		}
		return Optional.of(vehicle);
	}

	@Override
	public void setVehicle(VehicleBase vehicle) {
		this.vehicle = vehicle;
	}

	@Override
	public void resetVehicle() {
		vehicle = null;
	}

	@Override
	public boolean isRidingVehicle() {
		return getVehicle().isPresent() && isRiding();
	}

	@Override
	public boolean canContinueRidingVehicle() {
		return getFoodRemaining() > 0;
	}
}
