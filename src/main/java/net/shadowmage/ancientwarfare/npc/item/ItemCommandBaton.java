package net.shadowmage.ancientwarfare.npc.item;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.shadowmage.ancientwarfare.core.gamedata.AWGameData;
import net.shadowmage.ancientwarfare.core.interfaces.IItemClickable;
import net.shadowmage.ancientwarfare.core.interfaces.IItemKeyInterface;
import net.shadowmage.ancientwarfare.core.util.RayTraceUtils;
import net.shadowmage.ancientwarfare.core.util.WorldTools;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;
import net.shadowmage.ancientwarfare.npc.entity.NpcPlayerOwned;
import net.shadowmage.ancientwarfare.npc.gamedata.CommandData;
import net.shadowmage.ancientwarfare.npc.npc_command.NpcCommand;

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
  CommandData data = AWGameData.INSTANCE.getData(CommandData.name, player.worldObj, CommandData.class);
  if(!stack.hasTagCompound() || !stack.getTagCompound().hasKey("stackId"))
    {
    stack.setTagInfo("stackId", new NBTTagInt(data.getNextBatonId()));
    }
  if(player.isSneaking())
    {
    //openGUI
    }
  else
    {
    MovingObjectPosition pos = RayTraceUtils.getPlayerTarget(player, 120, 0);//TODO set range from config;
    if(pos!=null && pos.typeOfHit==MovingObjectType.ENTITY && pos.entityHit instanceof NpcPlayerOwned)
      {
      data.onNpcClicked((NpcPlayerOwned) pos.entityHit);
      }
    }
  }

@Override
public void onKeyAction(EntityPlayer player, ItemStack stack)
  {
  //noop  
  }

@Override
public boolean onKeyActionClient(EntityPlayer player, ItemStack stack)
  {
  //noop  
  return false;
  }

public static List<Entity> getCommandedEntities(World world, ItemStack stack, List<Entity> entities)
  {
  if(stack!=null && stack.getItem()==AWNpcItemLoader.commandBaton && stack.hasTagCompound() && stack.getTagCompound().hasKey("entityList"))
    {
    NBTTagCompound tag;
    NBTTagList list = stack.getTagCompound().getTagList("entityList", Constants.NBT.TAG_COMPOUND);
    long id1, id2;
    Entity e;
    for(int i = 0; i<list.tagCount();i++)
      {
      tag = list.getCompoundTagAt(i);
      id1=tag.getLong("idmsb");
      id2=tag.getLong("idlsb"); 
      e = WorldTools.getEntityByUUID(world, id1, id2);
      if(e!=null)
        {
        entities.add(e);
        }
      }
    }
  return entities;
  }

}
