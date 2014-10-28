package net.shadowmage.ancientwarfare.npc.entity;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIOpenDoor;
import net.minecraft.entity.ai.EntityAIRestrictOpenDoor;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityAIWatchClosest2;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIPlayerOwnedBard;
import net.shadowmage.ancientwarfare.npc.ai.NpcAICommandGuard;
import net.shadowmage.ancientwarfare.npc.ai.NpcAICommandMove;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIFleeHostiles;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIFollowPlayer;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIGetFood;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIIdleWhenHungry;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIMoveHome;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIPlayerOwnedRideHorse;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIWander;

public class NpcBard extends NpcPlayerOwned
{

BardTuneData tuneData = new BardTuneData();

public NpcBard(World par1World)
  {
  super(par1World);
  this.tasks.addTask(0, new EntityAISwimming(this));
  this.tasks.addTask(0, new EntityAIRestrictOpenDoor(this));
  this.tasks.addTask(0, new EntityAIOpenDoor(this, true));
  this.tasks.addTask(0, (horseAI=new NpcAIPlayerOwnedRideHorse(this))); 
  this.tasks.addTask(2, new NpcAIFollowPlayer(this));
  this.tasks.addTask(2, new NpcAICommandGuard(this));
  this.tasks.addTask(2, new NpcAICommandMove(this));
  this.tasks.addTask(3, new NpcAIFleeHostiles(this));
  this.tasks.addTask(4, new NpcAIGetFood(this));  
  this.tasks.addTask(5, new NpcAIIdleWhenHungry(this)); 
  
  this.tasks.addTask(7, new NpcAIMoveHome(this, 50.f, 3.f, 30.f, 3.f));
  this.tasks.addTask(8, new NpcAIPlayerOwnedBard(this));
  
  //post-100 -- used by delayed shared tasks (look at random stuff, wander)
  this.tasks.addTask(101, new EntityAIWatchClosest2(this, EntityPlayer.class, 3.0F, 1.0F));
  this.tasks.addTask(102, new NpcAIWander(this, 0.625D));
  this.tasks.addTask(103, new EntityAIWatchClosest(this, EntityLiving.class, 8.0F));
  }

public BardTuneData getTuneData()
  {
  return tuneData;
  }

@Override
public boolean isValidOrdersStack(ItemStack stack)
  {
  return false;
  }

@Override
public void onOrdersInventoryChanged()
  {
  }

@Override
public String getNpcSubType()
  {
  return "";
  }

@Override
public String getNpcType()
  {
  return "bard";
  }

@Override
public boolean hasAltGui()
  {
  return true;
  }

@Override
public void openAltGui(EntityPlayer player)
  {
  NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_NPC_BARD, getEntityId(), 0, 0);
  }

@Override
public void readEntityFromNBT(NBTTagCompound tag)
  {  
  super.readEntityFromNBT(tag);
  tuneData.readFromNBT(tag.getCompoundTag("tuneData"));
  }

@Override
public void writeEntityToNBT(NBTTagCompound tag)
  {
  super.writeEntityToNBT(tag);
  tag.setTag("tuneData", tuneData.writeToNBT(new NBTTagCompound()));
  }

public static final class BardTuneData
{
private boolean random = false;
private boolean playOnPlayerEntry = false;
private int minDelay;
private int maxDelay;
private List<BardTuneEntry> tunes = new ArrayList<BardTuneEntry>();

public int size(){return tunes.size();}
public BardTuneEntry get(int index){return tunes.get(index);}

public void addNewEntry()
  {
  BardTuneEntry e = new BardTuneEntry();
  tunes.add(e);
  }

public void decrementEntry(int index)
  {
  if(index<=0 || index>=tunes.size()){return;}
  BardTuneEntry e = tunes.remove(index);
  index--;
  tunes.add(index, e);
  }

public void incrementEntry(int index)
  {
  if(index<0 || index>=tunes.size()-1){return;}
  BardTuneEntry e = tunes.remove(index);
  index++;
  tunes.add(index, e);
  }

public void deleteEntry(int index)
  {
  if(index<0 || index>=tunes.size()){return;}
  tunes.remove(index);
  }

public int getMinDelay(){return minDelay;}
public int getMaxDelay(){return maxDelay;}
public boolean getPlayOnPlayerEntry(){return playOnPlayerEntry;}
public boolean getIsRandom(){return random;}

public void setMinDelay(int val){minDelay=val;}
public void setMaxDelay(int val){maxDelay=val;}
public void setPlayOnPlayerEntry(boolean val){playOnPlayerEntry=val;}
public void setRandom(boolean val){random=val;}

public void readFromNBT(NBTTagCompound tag)
  {
  tunes.clear();
  BardTuneEntry d;
  NBTTagList l = tag.getTagList("entries", Constants.NBT.TAG_COMPOUND);
  for(int i = 0; i < l.tagCount(); i++)
    {
    d = new BardTuneEntry();
    d.readFromNBT(l.getCompoundTagAt(i));
    tunes.add(d);
    }
  }

public NBTTagCompound writeToNBT(NBTTagCompound tag)
  {
  NBTTagList l = new NBTTagList();
  for(int i = 0; i < tunes.size(); i++)
    {
    l.appendTag(tunes.get(i).writeToNBT(new NBTTagCompound()));
    }  
  tag.setTag("entries", l);
  return tag;
  }
}

public static final class BardTuneEntry
{
String name;
int length;//length in seconds, used to determine when count down for next tune should start
int volume;// percentage, as integer 0 = 0%, 100=100%, 150=150%

public BardTuneEntry()
  {
  name = "";
  length = 0;
  volume = 100;
  }

public void setLength(int length){this.length = length;}

public void setName(String name){this.name = name==null? "" : name;}

public void setVolume(int volume){this.volume = volume;}

public int volume(){return volume;}

public String name(){return name;}

public int length(){return length;}

public void readFromNBT(NBTTagCompound tag)
  {
  name = tag.getString("name");
  length = tag.getInteger("length");
  volume = tag.getInteger("volume");
  }

public NBTTagCompound writeToNBT(NBTTagCompound tag)
  {
  tag.setString("name", name);
  tag.setInteger("length", length);
  tag.setInteger("volume", volume);
  return tag;
  }

}

}
