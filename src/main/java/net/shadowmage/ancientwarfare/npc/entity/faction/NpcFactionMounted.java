package net.shadowmage.ancientwarfare.npc.entity.faction;

import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public abstract class NpcFactionMounted extends NpcFaction implements IHorseMountedNpc {

	private boolean horseLives = true;

	@Override
	public boolean isHorseAlive() {
		return horseLives;
	}

	@Override
	public void setHorseKilled() {
		horseLives = false;
	}

	public NpcFactionMounted(World world) {
		super(world);
	}

	public NpcFactionMounted(World world, String factionName) {
		super(world, factionName);
	}

	@Override
	protected void onRepack() {
		if (getRidingEntity() instanceof EntityHorse) {
			getRidingEntity().setDead();
			dismountRidingEntity();
		}
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
	public void readEntityFromNBT(NBTTagCompound tag) {
		super.readEntityFromNBT(tag);
		horseLives = tag.getBoolean("horseLives");
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound tag) {
		super.writeEntityToNBT(tag);
		tag.setBoolean("horseLives", horseLives);
	}
}
