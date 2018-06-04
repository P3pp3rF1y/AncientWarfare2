package net.shadowmage.ancientwarfare.npc.entity.faction;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIRestrictOpenDoor;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityAIWatchClosest2;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIDoor;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIFollowPlayer;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIMoveHome;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIWander;
import net.shadowmage.ancientwarfare.npc.ai.faction.NpcAIFactionPriest;

public class NpcFactionPriest extends NpcFaction {
	public NpcFactionPriest(World world) {
		super(world);
	}

	public NpcFactionPriest(World world, String factionName) {
		super(world, factionName);
		//  setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(Items.BOOK));
		//TODO set in-hand item to...a cross? (or other holy symbol...an ankh?)

		this.tasks.addTask(0, new EntityAISwimming(this));
		this.tasks.addTask(0, new EntityAIRestrictOpenDoor(this));
		this.tasks.addTask(0, new NpcAIDoor(this, true));
		this.tasks.addTask(1, new NpcAIFollowPlayer(this));
		this.tasks.addTask(2, new NpcAIMoveHome(this, 50F, 5F, 30F, 5F));
		this.tasks.addTask(3, new NpcAIFactionPriest(this));

		this.tasks.addTask(101, new EntityAIWatchClosest2(this, EntityPlayer.class, 3.0F, 1.0F));
		this.tasks.addTask(102, new NpcAIWander(this));
		this.tasks.addTask(103, new EntityAIWatchClosest(this, EntityLiving.class, 8.0F));
	}

	@Override
	public String getNpcType() {
		return factionName + ".priest";
	}
}
