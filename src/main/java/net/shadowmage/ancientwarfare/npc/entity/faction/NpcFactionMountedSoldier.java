package net.shadowmage.ancientwarfare.npc.entity.faction;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIRestrictOpenDoor;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWatchClosest2;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIAttackMeleeLongRange;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIAttackNearest;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIDoor;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIFollowPlayer;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIHurt;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIMoveHome;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIWander;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIWatchClosest;

public class NpcFactionMountedSoldier extends NpcFactionMounted {
	private NpcAIAttackMeleeLongRange meleeAI;

	@SuppressWarnings("unused") //required for deserialization
	public NpcFactionMountedSoldier(World world) {
		super(world);
		addAI();
	}

	@SuppressWarnings("unused") //used in reflection
	public NpcFactionMountedSoldier(World world, String factionName) {
		super(world, factionName);
		addAI();
	}

	private void addAI() {
		meleeAI = new NpcAIAttackMeleeLongRange(this);
		tasks.addTask(0, new EntityAISwimming(this));
		tasks.addTask(0, new EntityAIRestrictOpenDoor(this));
		tasks.addTask(0, new NpcAIDoor(this, true));
		tasks.addTask(1, new NpcAIFollowPlayer(this));
		tasks.addTask(2, meleeAI);
		tasks.addTask(3, new NpcAIMoveHome(this, 50F, 5F, 30F, 5F));

		tasks.addTask(101, new EntityAIWatchClosest2(this, EntityPlayer.class, 3.0F, 1.0F));
		tasks.addTask(102, new NpcAIWander(this));
		tasks.addTask(103, new NpcAIWatchClosest(this, EntityLiving.class, 8.0F));

		targetTasks.addTask(1, new NpcAIHurt(this));
		targetTasks.addTask(2, new NpcAIAttackNearest(this, this::isHostileTowards));
	}

	@Override
	public String getNpcType() {
		return "cavalry";
	}

	@Override
	public void onWeaponInventoryChanged() {
		super.onWeaponInventoryChanged();

		if (meleeAI != null) {
			meleeAI.setAttackReachFromWeapon(getHeldItemMainhand());
		}
	}
}
