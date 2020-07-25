package net.shadowmage.ancientwarfare.npc.entity.faction;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIRestrictOpenDoor;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWatchClosest2;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIAttackMeleeLongRange;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIAttackNearest;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIBlockWithShield;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIDoor;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIFollowPlayer;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIHurt;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIMoveHome;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIWander;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIWatchClosest;
import net.shadowmage.ancientwarfare.npc.ai.faction.NpcAIFactionCommander;

import javax.annotation.Nonnull;

public class NpcFactionLeader extends NpcFaction {
	private NpcAIAttackMeleeLongRange meleeAI = new NpcAIAttackMeleeLongRange(this);
	private NpcAIBlockWithShield shieldBlockAI = new NpcAIBlockWithShield(this, 30, 40);

	public NpcFactionLeader(World world) {
		super(world);
		addAI();
	}

	public NpcFactionLeader(World world, String factionName) {
		super(world, factionName);
		addAI();
	}

	private void addAI() {
		tasks.addTask(0, new EntityAISwimming(this));
		tasks.addTask(0, new EntityAIRestrictOpenDoor(this));
		tasks.addTask(0, new NpcAIDoor(this, true));
		tasks.addTask(1, new NpcAIFactionCommander(this));
		tasks.addTask(1, new NpcAIFollowPlayer(this));
		tasks.addTask(3, meleeAI);
		tasks.addTask(4, new NpcAIMoveHome(this, 50F, 5F, 30F, 5F));

		tasks.addTask(101, new EntityAIWatchClosest2(this, EntityPlayer.class, 3.0F, 1.0F));
		tasks.addTask(102, new NpcAIWander(this));
		tasks.addTask(103, new NpcAIWatchClosest(this, EntityLiving.class, 8.0F));

		targetTasks.addTask(1, new NpcAIHurt(this));
		targetTasks.addTask(2, new NpcAIAttackNearest(this, this::isHostileTowards));
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
		return "leader";
	}

	@Override
	public boolean shouldSleep() {
		return false;
	}

	@Override
	public void onWeaponInventoryChanged() {
		super.onWeaponInventoryChanged();

		if (meleeAI != null) {
			meleeAI.setAttackReachFromWeapon(getHeldItemMainhand());
		}
	}

	@Override
	public void onOffhandInventoryChanged() {
		super.onOffhandInventoryChanged();
		if (!world.isRemote) {
			@Nonnull ItemStack mainhandStack = getHeldItemMainhand();
			@Nonnull ItemStack offhandStack = getHeldItemOffhand();
			if (offhandStack.getItem().isShield(offhandStack, this) && !isBow(mainhandStack.getItem())) {
				tasks.addTask(3, shieldBlockAI);
			}
		}
	}
}
