package net.shadowmage.ancientwarfare.npc.entity.faction;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIRestrictOpenDoor;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWatchClosest2;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIAttackNearest;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIDoor;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIFollowPlayer;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIMoveHome;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIWander;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIWatchClosest;
import net.shadowmage.ancientwarfare.npc.ai.faction.NpcAIFactionHurt;
import net.shadowmage.ancientwarfare.npc.ai.vehicle.NpcAIAimVehicle;
import net.shadowmage.ancientwarfare.npc.ai.vehicle.NpcAIFindVehicle;
import net.shadowmage.ancientwarfare.npc.ai.vehicle.NpcAIFireVehicle;
import net.shadowmage.ancientwarfare.npc.ai.vehicle.NpcAIMountVehicle;
import net.shadowmage.ancientwarfare.npc.entity.vehicle.EntityTarget;
import net.shadowmage.ancientwarfare.npc.entity.vehicle.ITarget;
import net.shadowmage.ancientwarfare.npc.entity.vehicle.IVehicleUser;
import net.shadowmage.ancientwarfare.npc.entity.vehicle.TargetFactory;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleBase;

import javax.annotation.Nullable;
import java.util.Optional;

@SuppressWarnings("squid:S2160")
public class NpcFactionSiegeEngineer extends NpcFaction implements IVehicleUser {

	private VehicleBase vehicle = null;
	private ITarget target = TargetFactory.NONE;

	@SuppressWarnings("unused")
	public NpcFactionSiegeEngineer(World world) {
		super(world);
		addAI();
	}

	@SuppressWarnings("unused")
	public NpcFactionSiegeEngineer(World world, String factionName) {
		super(world, factionName);
		addAI();
	}

	private void addAI() {
		this.tasks.addTask(0, new EntityAISwimming(this));
		this.tasks.addTask(0, new EntityAIRestrictOpenDoor(this));
		this.tasks.addTask(0, new NpcAIDoor(this, true));
		this.tasks.addTask(1, new NpcAIFollowPlayer(this));
		this.tasks.addTask(2, new NpcAIMoveHome(this, 50F, 5F, 30F, 5F));
		this.tasks.addTask(3, new NpcAIFindVehicle<>(this));
		this.tasks.addTask(4, new NpcAIMountVehicle<>(this));
		this.tasks.addTask(5, new NpcAIAimVehicle<>(this));
		this.tasks.addTask(6, new NpcAIFireVehicle<>(this));

		this.tasks.addTask(101, new EntityAIWatchClosest2(this, EntityPlayer.class, 3.0F, 1.0F));
		this.tasks.addTask(102, new NpcAIWander(this));
		this.tasks.addTask(103, new NpcAIWatchClosest(this, EntityLiving.class, 8.0F));

		this.targetTasks.addTask(1, new NpcAIFactionHurt(this, this::isHostileTowards));
		this.targetTasks.addTask(2, new NpcAIAttackNearest(this, this::isHostileTowards));
	}

	@Override
	public String getNpcType() {
		return "siege_engineer";
	}

	@Override
	public boolean worksInRain() {
		return true;
	}

	@Override
	public boolean isPassive() {
		return false;
	}

	@Override
	public boolean shouldSleep() {
		return false;
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

	@Override
	public void resetTarget() {
		target = TargetFactory.NONE;
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
		return true;
	}

	@Override
	public Optional<ITarget> getTarget() {
		return target == TargetFactory.NONE ? Optional.empty() : Optional.of(target);
	}
}
