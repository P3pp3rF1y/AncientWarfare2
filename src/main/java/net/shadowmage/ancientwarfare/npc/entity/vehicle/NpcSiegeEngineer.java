package net.shadowmage.ancientwarfare.npc.entity.vehicle;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIRestrictOpenDoor;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIDoor;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIFollowPlayer;
import net.shadowmage.ancientwarfare.npc.ai.vehicle.NpcAIFindVehicle;
import net.shadowmage.ancientwarfare.npc.ai.vehicle.NpcAIMountVehicle;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleBase;

import javax.annotation.Nullable;
import java.util.Optional;

public class NpcSiegeEngineer extends NpcBase {
	@Nullable
	private VehicleBase vehicle = null;

	public NpcSiegeEngineer(World world) {
		super(world);

		this.tasks.addTask(0, new EntityAISwimming(this));
		this.tasks.addTask(0, new EntityAIRestrictOpenDoor(this));
		this.tasks.addTask(0, new NpcAIDoor(this, true));
		this.tasks.addTask(2, new NpcAIFollowPlayer(this));
		this.tasks.addTask(6, new NpcAIFindVehicle(this));
		this.tasks.addTask(7, new NpcAIMountVehicle(this));
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
	public boolean isHostileTowards(Entity e) {
		return e instanceof EntityPlayer;
	}

	@Override
	public boolean canTarget(Entity e) {
		return e instanceof EntityLivingBase;
	}

	@Override
	public boolean canBeAttackedBy(Entity e) {
		return true;
	}

	public Optional<VehicleBase> getVehicle() {
		return Optional.ofNullable(vehicle);
	}

	public void setVehicle(VehicleBase vehicle) {
		this.vehicle = vehicle;
	}

	public void resetVehicle() {
		vehicle = null;
	}
}
