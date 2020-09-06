package net.shadowmage.ancientwarfare.npc.entity.vehicle;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIRestrictOpenDoor;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWatchClosest2;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIAttackNearest;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIDoor;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIFollowPlayer;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIHurt;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIWander;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIWatchClosest;
import net.shadowmage.ancientwarfare.npc.ai.owned.NpcAIOwnerHurtByTarget;
import net.shadowmage.ancientwarfare.npc.ai.owned.NpcAIOwnerHurtTarget;
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
import net.shadowmage.ancientwarfare.npc.npc_command.NpcCommand;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleBase;

import javax.annotation.Nullable;
import java.util.Optional;

@SuppressWarnings({"squid:MaximumInheritanceDepth", "squid:S2160"})
public class NpcSiegeEngineer extends NpcPlayerOwned implements IVehicleUser {
	@Nullable
	private VehicleBase vehicle = null;
	private ITarget target = TargetFactory.NONE;

	@Override
	public Optional<ITarget> getTarget() {
		return target == TargetFactory.NONE ? Optional.empty() : Optional.of(target);
	}

	private void setTarget(BlockPos pos) {
		target = new BlockPosTarget(pos);
	}

	@Override
	public void resetTarget() {
		target = TargetFactory.NONE;
	}

	@Override
	public void setAttackTarget(@Nullable EntityLivingBase entity) {
		super.setAttackTarget(entity);
		if (entity != null) {
			target = new EntityTarget(entity);
		} else {
			resetTarget();
		}
	}

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

		targetTasks.addTask(0, new NpcAIPlayerOwnedCommander(this));
		targetTasks.addTask(1, new NpcAIOwnerHurtByTarget(this));
		targetTasks.addTask(2, new NpcAIOwnerHurtTarget(this));
		targetTasks.addTask(3, new NpcAIHurt(this));
		targetTasks.addTask(4, new NpcAIAttackNearest(this, this::isHostileTowards));
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(120D);
	}

	@Override
	public String getNpcSubType() {
		return "";
	}

	@Override
	public String getNpcType() {
		return "siege_engineer";
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

	@Override
	public void handlePlayerCommand(NpcCommand.Command cmd) {
		if (cmd.type == NpcCommand.CommandType.ATTACK_AREA) {
			setTarget(cmd.pos);
		} else if (cmd.type == NpcCommand.CommandType.CLEAR_COMMAND) {
			resetTarget();
		} else {
			super.handlePlayerCommand(cmd);
		}
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		if (ticksExisted % 20 == 0) {
			checkTargetExistence();
		}
	}

	private void checkTargetExistence() {
		if (target != TargetFactory.NONE && !target.exists(world)) {
			target = TargetFactory.NONE;
		}
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound tag) {
		super.writeEntityToNBT(tag);
		TargetFactory.serializeNBT(target, tag);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound tag) {
		super.readEntityFromNBT(tag);
		target = TargetFactory.deserializeFromNBT(tag);
	}
}
