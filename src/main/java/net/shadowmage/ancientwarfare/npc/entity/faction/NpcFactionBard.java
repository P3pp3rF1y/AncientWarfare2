package net.shadowmage.ancientwarfare.npc.entity.faction;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIRestrictOpenDoor;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityAIWatchClosest2;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.interfaces.ISinger;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.util.SongPlayData;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIDoor;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIFollowPlayer;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIMoveHome;
import net.shadowmage.ancientwarfare.npc.ai.NpcAISing;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIWander;

public class NpcFactionBard extends NpcFaction implements ISinger {

	private SongPlayData tuneData = new SongPlayData();

	public NpcFactionBard(World world) {
		super(world);
	}

	public NpcFactionBard(World world, String factionName) {
		super(world, factionName);
		this.tasks.addTask(0, new EntityAISwimming(this));
		this.tasks.addTask(0, new EntityAIRestrictOpenDoor(this));
		this.tasks.addTask(0, new NpcAIDoor(this, true));
		this.tasks.addTask(1, new NpcAIFollowPlayer(this));
		this.tasks.addTask(2, new NpcAIMoveHome(this, 50F, 3F, 30F, 3F));
		this.tasks.addTask(3, new NpcAISing(this));

		this.tasks.addTask(101, new EntityAIWatchClosest2(this, EntityPlayer.class, 3.0F, 1.0F));
		this.tasks.addTask(102, new NpcAIWander(this));
		this.tasks.addTask(103, new EntityAIWatchClosest(this, EntityLiving.class, 8.0F));
	}

	@Override
	public SongPlayData getSongs() {
		return tuneData;
	}

	@Override
	public boolean isHostileTowards(Entity e) {
		return false;
	}

	@Override
	public boolean canTarget(Entity e) {
		return false;
	}

	@Override
	public boolean hasAltGui() {
		return true;
	}

	@Override
	public String getNpcType() {
		return "bard";
	}

	@Override
	public void openAltGui(EntityPlayer player) {
		NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_NPC_FACTION_BARD, getEntityId(), 0, 0);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound tag) {
		super.readEntityFromNBT(tag);
		tuneData.readFromNBT(tag.getCompoundTag("tuneData"));
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound tag) {
		super.writeEntityToNBT(tag);
		tag.setTag("tuneData", tuneData.writeToNBT(new NBTTagCompound()));
	}
}
