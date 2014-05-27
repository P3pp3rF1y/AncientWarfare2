package net.shadowmage.ancientwarfare.npc.entity;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.interfaces.IOwnable;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;
import net.shadowmage.ancientwarfare.npc.item.ItemNpcSpawner;
import net.shadowmage.ancientwarfare.npc.skin.NpcSkinManager;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;

public abstract class NpcBase extends EntityCreature implements IEntityAdditionalSpawnData, IOwnable
{

protected String ownerName = "";//the owner of this NPC, used for checking teams

protected String followingPlayerName;//set/cleared onInteract from player if player.team==this.team

protected NpcLevelingStats levelingStats;

/**
 * a single base texture for ALL npcs to share, used in case other textures were not set
 */
private final ResourceLocation baseDefaultTexture;

private ResourceLocation currentTexture = null;

public ItemStack ordersStack;

public ItemStack upkeepStack;

long idMsb;//used for skins

public NpcBase(World par1World)
  {
  super(par1World);
  baseDefaultTexture = new ResourceLocation("ancientwarfare:textures/entity/npc/npc_default.png");
  levelingStats = new NpcLevelingStats(this);
  
  this.getNavigator().setBreakDoors(true);
  this.getNavigator().setAvoidsWater(true);
  
  this.width = 1.f;
  }

@Override
protected void entityInit()
  {
  super.entityInit();
  this.getDataWatcher().addObject(20, Integer.valueOf(0));//ai tasks, TODO load/save from nbt
  }

public int getAITasks()
  {
  return getDataWatcher().getWatchableObjectInt(20);
  }

public void addAITask(int task)
  {
  AWLog.logDebug("adding ai task: "+task);
  int tasks = getAITasks();
  int tc = tasks;
  tasks = tasks | task;
  if(tc!=tasks)
    {
    setAITasks(tasks);    
    }
  }

public void removeAITask(int task)
  {
  AWLog.logDebug("removing ai task: "+task);
  int tasks = getAITasks();
  int tc = tasks;
  tasks = tasks & (~task);
  if(tc!=tasks)
    {
    setAITasks(tasks);    
    }
  }

private void setAITasks(int tasks)
  {
  AWLog.logDebug("setting npc ai tasks to: "+tasks);
  this.getDataWatcher().updateObject(20, Integer.valueOf(tasks));  
  }

@Override
public void setHomeArea(int par1, int par2, int par3, int par4)
  {  
  super.setHomeArea(par1, par2, par3, par4);
  AWLog.logDebug("setting home position...");
  }

public void addExperience(int amount)
  {
  String type = getNpcFullType();
  getLevelingStats().addExperience(type, amount);
  }

/**
 * implementations should read in any data written during {@link #writeAdditionalItemData(NBTTagCompound)}
 * @param tag
 */
public abstract void readAdditionalItemData(NBTTagCompound tag);

/**
 * Implementations should write out any persistent entity-data needed to restore entity-state from an item-stack.<br>
 * This should include inventory, levels, orders, faction / etc
 * @param tag
 */
public abstract void writeAdditionalItemData(NBTTagCompound tag);

public abstract boolean isValidOrdersStack(ItemStack stack);

public abstract void onOrdersInventoryChanged();

public abstract void onWeaponInventoryChanged();

public abstract String getNpcSubType();

public abstract String getNpcType();

public String getNpcFullType()
  {
  String type = getNpcType();
  String sub = getNpcSubType();
  if(!sub.isEmpty()){type = type+"."+sub;}
  return type;
  }

public NpcLevelingStats getLevelingStats()
  {
  return levelingStats;
  }

public ResourceLocation getDefaultTexture()
  {
  return baseDefaultTexture;
  }

public ItemStack getItemToSpawn()
  {
  return ItemNpcSpawner.getSpawnerItemForNpc(this);
  }

public long getIDForSkin()
  {
  return this.idMsb;
  }

@Override
public ItemStack getPickedResult(MovingObjectPosition target)
  {
  return getItemToSpawn();
  }

@Override
public void writeSpawnData(ByteBuf buffer)
  {
  buffer.writeLong(getUniqueID().getLeastSignificantBits());
  }

@Override
public void readSpawnData(ByteBuf additionalData)
  {
  long l2;
  l2 = additionalData.readLong();
  this.idMsb = l2;
  }

@Override
public void onUpdate()
  {
  worldObj.theProfiler.startSection("AWNpcTick");
  updateArmSwingProgress();
  if(ticksExisted%200==0 && getHealth()<getMaxHealth())
    {
    setHealth(getHealth()+1);
    }
  super.onUpdate();
  worldObj.theProfiler.endSection();
  }

@Override
protected boolean canDespawn()
  {
  return false;
  }

@Override
protected boolean isAIEnabled()
  {
  return true;
  }

@Override
protected void applyEntityAttributes()
  {
  super.applyEntityAttributes();  
  this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(20.d);//TODO figure out dynamic changing of max-health based on level from levelingStats
  this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(40.0D);//TODO check what pathfinding range is really needed, perhaps allow config option for longer paths
  this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.325D);//TODO check what entity speed is needed / feels right. perhaps vary depending upon level or type

  //TODO figure out how to dynamically reset attack damage attribute based on weapon equipped
  this.getAttributeMap().registerAttribute(SharedMonsterAttributes.attackDamage);
  this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(3.0D);
  }

public int getFoodRemaining()
  {
  return 0;
  }

public void setFoodRemaining(int food)
  {
  
  }

public int getUpkeepBlockSide()
  {
  return 0;
  }

public BlockPosition getUpkeepPoint()
  {
  return null;
  }

public int getUpkeepAmount()
  {
  return 0;
  }

public int getUpkeepDimensionId()
  {
  return 0;
  }

public boolean requiresUpkeep()
  {
  return false;
  }

@Override
public void setOwnerName(String name)
  {
  ownerName = name;
  }

@Override
public String getOwnerName()
  {
  return ownerName;
  }

@Override
public Team getTeam()
  {
  return worldObj.getScoreboard().getPlayersTeam(ownerName);
  }

public boolean isHostileTowards(Team team)
  {
  Team a = getTeam();
  if(a!=null && team!=null && a!=team){return true;}
  return false;
  }

public EntityLivingBase getFollowingEntity()
  {
  if(followingPlayerName==null){return null;}
  return worldObj.getPlayerEntityByName(followingPlayerName);
  }

public void setFollowingEntity(EntityLivingBase entity)
  {
  if(entity instanceof EntityPlayer)
    {
    this.followingPlayerName = entity.getCommandSenderName();        
    }
  }

@Override
public boolean allowLeashing()
  {
  return false;
  }

public void repackEntity(EntityPlayer player)
  {
  if(!player.worldObj.isRemote)
    {
    ItemStack item = this.getItemToSpawn();
    //TODO add repack button to npc-gui
    item = InventoryTools.mergeItemStack(player.inventory, item, -1);
    if(item!=null)
      {
      InventoryTools.dropItemInWorld(player.worldObj, item, player.posX, player.posY, player.posZ);    
      }    
    }
  }

@Override
public void readEntityFromNBT(NBTTagCompound tag)
  {
  super.readEntityFromNBT(tag);
  ownerName = tag.getString("owner");
  if(tag.hasKey("ordersItem")){ordersStack=ItemStack.loadItemStackFromNBT(tag.getCompoundTag("ordersItem"));}
  if(tag.hasKey("upkeepItem")){upkeepStack=ItemStack.loadItemStackFromNBT(tag.getCompoundTag("upkeepItem"));}
  if(tag.hasKey("home"))
    {
    int[] ccia = tag.getIntArray("home");
    setHomeArea(ccia[0], ccia[1], ccia[2], ccia[3]);
    }
  if(tag.hasKey("levelingStats")){levelingStats.readFromNBT(tag.getCompoundTag("levelingStats"));}
  //TODO
  }

@Override
public void writeEntityToNBT(NBTTagCompound tag)
  {
  super.writeEntityToNBT(tag);
  tag.setString("owner", ownerName);
  if(ordersStack!=null){tag.setTag("ordersItem", ordersStack.writeToNBT(new NBTTagCompound()));}
  if(upkeepStack!=null){tag.setTag("upkeepItem", upkeepStack.writeToNBT(new NBTTagCompound()));}
  if(getHomePosition()!=null)
    {
    ChunkCoordinates cc = getHomePosition();
    int[] ccia = new int[]{cc.posX,cc.posY,cc.posZ, (int)func_110174_bM()};
    tag.setIntArray("home", ccia);
    }
  tag.setTag("levelingStats", levelingStats.writeToNBT(new NBTTagCompound()));
  //TODO
  }

public final ResourceLocation getTexture()
  {  
  if(currentTexture==null)
    {
    updateTexture();
    }  
  return currentTexture==null ? getDefaultTexture() : currentTexture;
  }

public final void updateTexture()
  {
  currentTexture = NpcSkinManager.INSTANCE.getTextureFor(this);
  }

}
