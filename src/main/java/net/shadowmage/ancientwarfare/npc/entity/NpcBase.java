package net.shadowmage.ancientwarfare.npc.entity;

import io.netty.buffer.ByteBuf;

import java.util.UUID;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.util.Constants;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.interfaces.IOwnable;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;
import net.shadowmage.ancientwarfare.npc.config.AWNPCStatics;
import net.shadowmage.ancientwarfare.npc.item.ItemNpcSpawner;
import net.shadowmage.ancientwarfare.npc.npc_command.NpcCommand.Command;
import net.shadowmage.ancientwarfare.npc.skin.NpcSkinManager;
import net.shadowmage.ancientwarfare.npc.tile.TileTownHall;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;

public abstract class NpcBase extends EntityCreature implements IEntityAdditionalSpawnData, IOwnable
{

private String ownerName = "";//the owner of this NPC, used for checking teams

protected String followingPlayerName;//set/cleared onInteract from player if player.team==this.team

protected NpcLevelingStats levelingStats;



/**
 * a single base texture for ALL npcs to share, used in case other textures were not set
 */
private final ResourceLocation baseDefaultTexture;

private ResourceLocation currentTexture = null;

public ItemStack ordersStack;

public ItemStack upkeepStack;

public NpcBase(World par1World)
  {
  super(par1World);
  baseDefaultTexture = new ResourceLocation("ancientwarfare:textures/entity/npc/npc_default.png");
  levelingStats = new NpcLevelingStats(this);
  
  this.getNavigator().setBreakDoors(true);
  this.getNavigator().setAvoidsWater(true);
  this.equipmentDropChances = new float[]{1.f, 1.f, 1.f, 1.f, 1.f};
  this.width = 0.6f;
  }

@Override
protected void entityInit()
  {
  super.entityInit();
  this.getDataWatcher().addObject(20, Integer.valueOf(0));//ai tasks, TODO load/save from nbt
  }

public void setTownHallPosition(BlockPosition pos)
  {
  
  }

public BlockPosition getTownHallPosition()
  {
  return null;
  }

public TileTownHall getTownHall()
  {
  return null;
  }

public void handleTownHallBroadcast(TileTownHall tile, BlockPosition position)
  {
 
  }

/**
 * Used by command baton and town-hall to determine if this NPC is commandable by a player / team
 * @param playerName
 * @return
 */
public boolean canBeCommandedBy(String playerName)
  {
  if(ownerName.isEmpty()){return false;}
  if(playerName==null){return false;}
  Team team = getTeam();
  if(team==null)
    {
    return playerName.equals(ownerName);
    }
  else
    {
    return team==worldObj.getScoreboard().getPlayersTeam(playerName);
    }
  }

@Override
public boolean attackEntityFrom(DamageSource source, float par2)
  {
  if(source.getEntity() instanceof NpcBase)
    {
    if(!isHostileTowards(source.getEntity()))
      {
      return false;
      }
    }
  return super.attackEntityFrom(source, par2);
  }

@Override
public void setRevengeTarget(EntityLivingBase par1EntityLivingBase)
  {
  if(par1EntityLivingBase instanceof NpcBase)
    {
    if(!isHostileTowards(par1EntityLivingBase))
      {
      return;
      }
    }
  super.setRevengeTarget(par1EntityLivingBase);
  }

@Override
protected void dropEquipment(boolean par1, int par2)
  {
  if(!worldObj.isRemote)
    {
    ItemStack stack;
    for(int i = 0; i < 5; i++)
      {
      stack = getEquipmentInSlot(i);
      if(stack!=null){entityDropItem(stack, 0.f);}
      setCurrentItemOrArmor(i, null);
      }
    if(ordersStack!=null){entityDropItem(ordersStack, 0.f);}
    if(upkeepStack!=null){entityDropItem(upkeepStack, 0.f);}
    ordersStack=null;
    upkeepStack=null;
    }
  }

@Override
public void onKillEntity(EntityLivingBase par1EntityLivingBase)
  {  
  super.onKillEntity(par1EntityLivingBase);
  if(!worldObj.isRemote)
    {
    addExperience(AWNPCStatics.npcXpFromKill);
    if(par1EntityLivingBase==this.getAttackTarget())
      {
      this.setAttackTarget(null);
      }
    }
  }

public Command getCurrentCommand()
  {
  return null;
  }

public void handlePlayerCommand(Command cmd)
  {
  
  }

public int getAITasks()
  {
  return getDataWatcher().getWatchableObjectInt(20);
  }

public void addAITask(int task)
  {
//  AWLog.logDebug("adding ai task: "+task);
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
//  AWLog.logDebug("removing ai task: "+task);
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
//  AWLog.logDebug("setting npc ai tasks to: "+tasks);
  this.getDataWatcher().updateObject(20, Integer.valueOf(tasks));  
  }

@Override
public void setHomeArea(int par1, int par2, int par3, int par4)
  {  
  super.setHomeArea(par1, par2, par3, par4);
//  AWLog.logDebug("setting home position...");
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
public final void readAdditionalItemData(NBTTagCompound tag)
  {
  NBTTagList equipmentList = tag.getTagList("equipment", Constants.NBT.TAG_COMPOUND);
  ItemStack stack;
  NBTTagCompound equipmentTag;
  for(int i = 0; i < equipmentList.tagCount(); i++)
    {
    equipmentTag = equipmentList.getCompoundTagAt(i);
    stack = InventoryTools.readItemStack(equipmentTag);
    if(equipmentTag.hasKey("slotNum")){setCurrentItemOrArmor(equipmentTag.getInteger("slotNum"), stack);}
    else if(equipmentTag.hasKey("orders")){ordersStack=stack;}
    else if(equipmentTag.hasKey("upkeep")){upkeepStack=stack;}
    }
  if(tag.hasKey("levelingStats")){getLevelingStats().readFromNBT(tag.getCompoundTag("levelingStats"));}
  if(tag.hasKey("maxHealth")){getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(tag.getFloat("maxHealth"));}
  if(tag.hasKey("health")){setHealth(tag.getFloat("health"));}
  if(tag.hasKey("name")){setCustomNameTag(tag.getString("name"));}
  if(tag.hasKey("food")){setFoodRemaining(tag.getInteger("food"));}
  }

/**
 * Implementations should write out any persistent entity-data needed to restore entity-state from an item-stack.<br>
 * This should include inventory, levels, orders, faction / etc
 * @param tag
 */
public final NBTTagCompound writeAdditionalItemData(NBTTagCompound tag)
  {
  /**
   * write out:
   * equipment (including orders items)
   * leveling stats
   * current health
   */
  NBTTagList equipmentList = new NBTTagList();
  ItemStack stack;
  NBTTagCompound equipmentTag;
  for(int i = 0; i < 5; i++)
    {
    stack = getEquipmentInSlot(i);
    if(stack==null){continue;}
    equipmentTag = InventoryTools.writeItemStack(stack, new NBTTagCompound());
    equipmentTag.setInteger("slotNum", i);
    equipmentList.appendTag(equipmentTag);
    }
  if(ordersStack!=null)
    {
    equipmentTag = InventoryTools.writeItemStack(ordersStack, new NBTTagCompound());
    equipmentTag.setBoolean("orders", true);
    equipmentList.appendTag(equipmentTag);
    }
  if(upkeepStack!=null)
    {
    equipmentTag = InventoryTools.writeItemStack(upkeepStack, new NBTTagCompound());
    equipmentTag.setBoolean("upkeep", true);
    equipmentList.appendTag(equipmentTag);
    }  
  tag.setTag("equipment", equipmentList);
  
  tag.setTag("levelingStats", getLevelingStats().writeToNBT(new NBTTagCompound()));
  tag.setFloat("maxHealth", getMaxHealth());
  tag.setFloat("health", getHealth());
  tag.setInteger("food", getFoodRemaining());
  if(hasCustomNameTag()){tag.setString("name", getCustomNameTag());}
  return tag;
  }

public abstract boolean isValidOrdersStack(ItemStack stack);

public abstract void onOrdersInventoryChanged();

public abstract void onWeaponInventoryChanged();

public abstract String getNpcSubType();

public abstract String getNpcType();

public abstract void handleAlertBroadcast(NpcBase broadcaster, EntityLivingBase target);

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
  return this.entityUniqueID.getLeastSignificantBits();
  }

@Override
public ItemStack getPickedResult(MovingObjectPosition target)
  {
  return getItemToSpawn();
  }

@Override
public void writeSpawnData(ByteBuf buffer)
  {
  buffer.writeLong(getUniqueID().getMostSignificantBits());
  buffer.writeLong(getUniqueID().getLeastSignificantBits());
  ByteBufUtils.writeUTF8String(buffer, ownerName);
  }

@Override
public void readSpawnData(ByteBuf additionalData)
  {
  long l1, l2;
  l1 = additionalData.readLong();
  l2 = additionalData.readLong();
  this.entityUniqueID = new UUID(l1, l2);
  ownerName=ByteBufUtils.readUTF8String(additionalData);
  }

@Override
public void onUpdate()
  {
  worldObj.theProfiler.startSection("AWNpcTick");
  updateArmSwingProgress();
  if(ticksExisted%200==0 && getHealth()<getMaxHealth() && (!requiresUpkeep() || getFoodRemaining()>0))
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
  this.getAttributeMap().registerAttribute(SharedMonsterAttributes.attackDamage);
  }

/**
 * called whenever level changes, to update the damage-done stat for the entity
 */
public void updateDamageFromLevel()
  {
  float dmg = AWNPCStatics.npcAttackDamage;
  float lvl = getLevelingStats().getLevel(getNpcFullType());
  dmg += dmg*lvl*AWNPCStatics.npcLevelDamageMultiplier;
  this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(dmg);  
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

public abstract boolean isHostileTowards(Entity e);

public EntityLivingBase getFollowingEntity()
  {
  if(followingPlayerName==null){return null;}
  return worldObj.getPlayerEntityByName(followingPlayerName);
  }

public void setFollowingEntity(EntityLivingBase entity)
  {
  if(entity instanceof EntityPlayer && canBeCommandedBy(entity.getCommandSenderName()))
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
    item = InventoryTools.mergeItemStack(player.inventory, item, -1);
    if(item!=null)
      {
      InventoryTools.dropItemInWorld(player.worldObj, item, player.posX, player.posY, player.posZ);    
      }    
    setDead();
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
