package net.shadowmage.ancientwarfare.npc.item;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.interfaces.IItemClickable;
import net.shadowmage.ancientwarfare.core.interfaces.IItemKeyInterface;
import net.shadowmage.ancientwarfare.core.util.RayTraceUtils;
import net.shadowmage.ancientwarfare.core.util.WorldTools;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;
import net.shadowmage.ancientwarfare.npc.entity.NpcPlayerOwned;

public class ItemCommandBaton extends Item implements IItemKeyInterface, IItemClickable 
{

public ItemCommandBaton(String name)
  {
  this.setUnlocalizedName(name);
  this.setCreativeTab(AWNpcItemLoader.npcTab);
  }

@Override
public boolean onRightClickClient(EntityPlayer player, ItemStack stack)
  {
  return true;
  }

@Override
public boolean onLeftClickClient(EntityPlayer player, ItemStack stack)
  {
  //noop
  return false;
  }

@Override
public void onLeftClick(EntityPlayer player, ItemStack stack)
  {
  //noop
  }

@Override
public void onRightClick(EntityPlayer player, ItemStack stack)
  {
  if(player.isSneaking())
    {
    //TODO openGUI
    }
  else
    {
    MovingObjectPosition pos = RayTraceUtils.getPlayerTarget(player, 120, 0);//TODO set range from config;
    AWLog.logDebug("pos..: "+pos);
    if(pos!=null && pos.typeOfHit==MovingObjectType.ENTITY && pos.entityHit instanceof NpcPlayerOwned)
      {
      AWLog.logDebug("npc clicked...");
      onNpcClicked(player, (NpcPlayerOwned) pos.entityHit, stack);
      }
    }
  }

@Override
public void onKeyAction(EntityPlayer player, ItemStack stack)
  {
  //noop ...or...??
  }

@Override
public boolean onKeyActionClient(EntityPlayer player, ItemStack stack)
  {
  //noop ...or...??
  return false;
  }

private void onNpcClicked(EntityPlayer player, NpcBase npc, ItemStack stack)
  {
  if(player==null || npc==null || stack==null || stack.getItem()!=this){return;}
  CommandSet set = new CommandSet();
  set.loadFromStack(stack);
  set.onNpcClicked(npc);
  set.validateEntities(player.worldObj);
  set.writeToStack(stack);
  }

public static void getCommandedEntities(World world, ItemStack stack, List<Entity> entities)
  {
  if(world==null || stack==null || entities==null || stack.getItem()!=AWNpcItemLoader.commandBaton){return;}
  CommandSet set = new CommandSet();
  set.loadFromStack(stack);
  set.getEntities(world, entities);
  }

/**
 * relies on NPCs transmitting their unique entity-id to client-side<br>
 * @author Shadowmage
 *
 */
private static class CommandSet
{
private Set<UUID> ids = new HashSet<UUID>();

public void loadFromStack(ItemStack stack)
  {
  if(stack.hasTagCompound() && stack.getTagCompound().hasKey("entityList"))
    {
    readFromNBT(stack.getTagCompound().getCompoundTag("entityList"));
    }
  }

public void writeToStack(ItemStack stack)
  {
  stack.setTagInfo("entityList", writeToNBT(new NBTTagCompound()));
  }

private void readFromNBT(NBTTagCompound tag)
  {
  NBTTagList entryList = tag.getTagList("entryList", Constants.NBT.TAG_COMPOUND);
  NBTTagCompound idTag;
  for(int i = 0; i <entryList.tagCount();i++)
    {
    idTag = entryList.getCompoundTagAt(i);
    ids.add(new UUID(idTag.getLong("idmsb"), idTag.getLong("idlsb")));
    }
  }

private NBTTagCompound writeToNBT(NBTTagCompound tag)
  {
  NBTTagList entryList = new NBTTagList();
  NBTTagCompound idTag;
  for(UUID id : ids)
    {
    idTag = new NBTTagCompound();
    idTag.setLong("idmsb", id.getMostSignificantBits());
    idTag.setLong("idlsb", id.getLeastSignificantBits());
    entryList.appendTag(idTag);
    }
  tag.setTag("entryList", entryList);
  return tag;
  }

public void onNpcClicked(NpcBase npc)
  {
  if(ids.contains(npc.getPersistentID()))
    {
    ids.remove(npc.getPersistentID());
    }
  else
    {
    ids.add(npc.getPersistentID());
    }
  }

public void getEntities(World world, List<Entity> in)
  {
  Entity e;
  for(UUID id : ids)
    {
    e = WorldTools.getEntityByUUID(world, id.getMostSignificantBits(), id.getLeastSignificantBits());
    if(e!=null){in.add(e);}
    }
  }

/**
 * should be called server side to clear out any old un-findable entity references.<br>
 * should probably only be called on-right click, as operation may be costly
 * @param world
 */
public void validateEntities(World world)
  {
  Iterator<UUID> it = ids.iterator();
  UUID id;
  while(it.hasNext() && (id=it.next())!=null)
    {
    if(WorldTools.getEntityByUUID(world, id.getMostSignificantBits(), id.getLeastSignificantBits())==null)
      {
      it.remove();
      }
    }
  }

}

}
