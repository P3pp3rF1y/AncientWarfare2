package net.shadowmage.ancientwarfare.npc.entity.faction;

import com.google.common.base.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.ai.EntityAIRestrictOpenDoor;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWatchClosest2;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIAttackNearest;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIDoor;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIFollowPlayer;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIMoveHome;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIWander;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIWatchClosest;
import net.shadowmage.ancientwarfare.npc.ai.faction.NpcAIFactionArcherStayAtHome;
import net.shadowmage.ancientwarfare.npc.ai.faction.NpcAIFactionHurt;
import net.shadowmage.ancientwarfare.npc.ai.faction.NpcAIFactionRangedAttack;
import net.shadowmage.ancientwarfare.npc.entity.RangeAttackHelper;

public class NpcFactionArcher extends NpcFaction implements IRangedAttackMob {
	public NpcFactionArcher(World world) {
		super(world);
		addAI();
	}

	public NpcFactionArcher(World world, String factionName) {
		super(world, factionName);
		addAI();
	}

	private void addAI() {
		//noinspection Guava
		Predicate<Entity> selector = entity -> {
			//noinspection ConstantConditions
			if (!isHostileTowards(entity)) {
				return false;
			}
			if (hasHome()) {
				BlockPos home = getHomePosition();
				double dist = entity.getDistanceSq(home.getX() + 0.5d, home.getY(), home.getZ() + 0.5d);
				if (dist > 30 * 30) {
					return false;
				}
			}
			return true;
		};

		tasks.addTask(0, new EntityAISwimming(this));
		tasks.addTask(0, new EntityAIRestrictOpenDoor(this));
		tasks.addTask(0, new NpcAIDoor(this, true));
		tasks.addTask(1, new NpcAIFollowPlayer(this));
		tasks.addTask(2, new NpcAIMoveHome(this, 50.f, 5.f, 30.f, 5.f));
		tasks.addTask(2, new NpcAIFactionArcherStayAtHome(this));
		tasks.addTask(3, new NpcAIFactionRangedAttack(this));

		tasks.addTask(101, new EntityAIWatchClosest2(this, EntityPlayer.class, 3.0F, 1.0F));
		tasks.addTask(102, new NpcAIWander(this));
		tasks.addTask(103, new NpcAIWatchClosest(this, EntityLiving.class, 8.0F));

		targetTasks.addTask(1, new NpcAIFactionHurt(this, this::isHostileTowards));
		targetTasks.addTask(2, new NpcAIAttackNearest(this, selector));
	}

	@Override
	public void attackEntityWithRangedAttack(EntityLivingBase target, float force) {
		RangeAttackHelper.doRangedAttack(this, target, force, 1.0f);
	}

	@Override
	public boolean canAttackClass(Class claz) {
		return true;
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
	public String getNpcType() {
		return "archer";
	}

	@Override
	public boolean shouldSleep() {
		return false;
	}
}
