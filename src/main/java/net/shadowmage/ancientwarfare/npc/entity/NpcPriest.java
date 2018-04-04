package net.shadowmage.ancientwarfare.npc.entity;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIRestrictOpenDoor;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityAIWatchClosest2;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIDoor;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIFleeHostiles;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIFollowPlayer;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIMoveHome;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIWander;
import net.shadowmage.ancientwarfare.npc.ai.owned.NpcAIPlayerOwnedAlarmResponse;
import net.shadowmage.ancientwarfare.npc.ai.owned.NpcAIPlayerOwnedFollowCommand;
import net.shadowmage.ancientwarfare.npc.ai.owned.NpcAIPlayerOwnedGetFood;
import net.shadowmage.ancientwarfare.npc.ai.owned.NpcAIPlayerOwnedIdleWhenHungry;
import net.shadowmage.ancientwarfare.npc.ai.owned.NpcAIPlayerOwnedPriest;
import net.shadowmage.ancientwarfare.npc.ai.owned.NpcAIPlayerOwnedRideHorse;

public class NpcPriest extends NpcPlayerOwned {

	public NpcPriest(World par1World) {
		super(par1World);

		this.tasks.addTask(0, new EntityAISwimming(this));
		this.tasks.addTask(0, new EntityAIRestrictOpenDoor(this));
		this.tasks.addTask(0, new NpcAIDoor(this, true));
		this.tasks.addTask(0, (horseAI = new NpcAIPlayerOwnedRideHorse(this)));
		this.tasks.addTask(2, new NpcAIFollowPlayer(this));
		this.tasks.addTask(2, new NpcAIPlayerOwnedFollowCommand(this));
		this.tasks.addTask(3, new NpcAIFleeHostiles(this));
		this.tasks.addTask(3, new NpcAIPlayerOwnedAlarmResponse(this));
		this.tasks.addTask(4, new NpcAIPlayerOwnedGetFood(this));
		this.tasks.addTask(5, new NpcAIPlayerOwnedIdleWhenHungry(this));
		this.tasks.addTask(6, new NpcAIMoveHome(this, 50F, 8F, 30F, 3F));
		this.tasks.addTask(7, new NpcAIPlayerOwnedPriest(this));

		//post-100 -- used by delayed shared tasks (look at random stuff, wander)
		this.tasks.addTask(101, new EntityAIWatchClosest2(this, EntityPlayer.class, 3.0F, 1.0F));
		this.tasks.addTask(102, new NpcAIWander(this));
		this.tasks.addTask(103, new EntityAIWatchClosest(this, EntityLiving.class, 8.0F));
	}

	@Override
	public void onOrdersInventoryChanged() {

	}

	@Override
	public String getNpcSubType() {
		return "";
	}

	@Override
	public String getNpcType() {
		return "priest";
	}

}
