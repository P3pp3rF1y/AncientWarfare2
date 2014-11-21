package net.shadowmage.ancientwarfare.npc.entity.faction;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIOpenDoor;
import net.minecraft.entity.ai.EntityAIRestrictOpenDoor;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityAIWatchClosest2;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.util.SongPlayData;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIFollowPlayer;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIMoveHome;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIWander;
import net.shadowmage.ancientwarfare.npc.ai.faction.NpcAIFactionBard;

public abstract class NpcFactionBard extends NpcFaction
{

SongPlayData tuneData = new SongPlayData();

public NpcFactionBard(World par1World)
  {
  super(par1World);
  this.tasks.addTask(0, new EntityAISwimming(this));
  this.tasks.addTask(0, new EntityAIRestrictOpenDoor(this));
  this.tasks.addTask(0, new EntityAIOpenDoor(this, true));
  this.tasks.addTask(1, new NpcAIFollowPlayer(this));
  this.tasks.addTask(2, new NpcAIMoveHome(this, 50.f, 3.f, 30.f, 3.f)); 
  this.tasks.addTask(3, new NpcAIFactionBard(this));
  
  this.tasks.addTask(101, new EntityAIWatchClosest2(this, EntityPlayer.class, 3.0F, 1.0F));
  this.tasks.addTask(102, new NpcAIWander(this, 0.625D));
  this.tasks.addTask(103, new EntityAIWatchClosest(this, EntityLiving.class, 8.0F)); 
  }

public SongPlayData getTuneData()
  {
  return tuneData;
  }

@Override
public boolean isHostileTowards(Entity e)
  {
  return false;
  }

@Override
public boolean canTarget(Entity e)
  {
  return false;
  }

@Override
public boolean hasAltGui()
  {
  return true;
  }

@Override
public void openAltGui(EntityPlayer player)
  {
  NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_NPC_FACTION_BARD, getEntityId(), 0, 0);
  }

}
