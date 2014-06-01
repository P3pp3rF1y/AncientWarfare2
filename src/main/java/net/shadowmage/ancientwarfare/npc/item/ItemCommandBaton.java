package net.shadowmage.ancientwarfare.npc.item;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.shadowmage.ancientwarfare.core.interfaces.IItemClickable;
import net.shadowmage.ancientwarfare.core.interfaces.IItemKeyInterface;
import net.shadowmage.ancientwarfare.core.util.RayTraceUtils;
import net.shadowmage.ancientwarfare.core.util.WorldTools;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;
import net.shadowmage.ancientwarfare.npc.entity.NpcPlayerOwned;
import net.shadowmage.ancientwarfare.npc.npc_command.NpcCommand;
import net.shadowmage.ancientwarfare.npc.npc_command.NpcCommand.CommandType;

import com.google.common.collect.Multimap;

public class ItemCommandBaton extends Item implements IItemKeyInterface, IItemClickable 
{

double attackDamage = 5.d;

private ToolMaterial material;

public ItemCommandBaton(String name, ToolMaterial material)
  {
  this.setUnlocalizedName(name);
  this.setCreativeTab(AWNpcItemLoader.npcTab);
  this.setTextureName("ancientwarfare:npc/"+name);
  this.attackDamage = 4.f + material.getDamageVsEntity();
  this.material = material;
  this.maxStackSize = 1;
  this.setMaxDamage(material.getMaxUses());
  }

/**
 * Return the enchantability factor of the item, most of the time is based on material.
 */
@Override
public int getItemEnchantability()
  {
  return this.material.getEnchantability();
  }

/**
 * Return whether this item is repairable in an anvil.
 */
@Override
public boolean getIsRepairable(ItemStack par1ItemStack, ItemStack par2ItemStack)
  {
  return this.material.func_150995_f() == par2ItemStack.getItem() ? true : super.getIsRepairable(par1ItemStack, par2ItemStack);
  }

/**
 * Current implementations of this method in child classes do not use the entry argument beside ev. They just raise
 * the damage on the stack.
 */
@Override
public boolean hitEntity(ItemStack par1ItemStack, EntityLivingBase par2EntityLivingBase, EntityLivingBase par3EntityLivingBase)
  {
  par1ItemStack.damageItem(1, par3EntityLivingBase);
  return true;
  }

@Override
public boolean onBlockDestroyed(ItemStack p_150894_1_, World p_150894_2_, Block p_150894_3_, int p_150894_4_, int p_150894_5_, int p_150894_6_, EntityLivingBase p_150894_7_)
  {
  if ((double)p_150894_3_.getBlockHardness(p_150894_2_, p_150894_4_, p_150894_5_, p_150894_6_) != 0.0D)
    {
    p_150894_1_.damageItem(2, p_150894_7_);
    }
  return true;
  }

/**
 * Gets a map of item attribute modifiers, used by ItemSword to increase hit damage.
 */
@Override
public Multimap getItemAttributeModifiers()
  {
  Multimap multimap = super.getItemAttributeModifiers();
  multimap.put(SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName(), new AttributeModifier(field_111210_e, "Weapon modifier", (double)this.attackDamage, 0));
  return multimap;
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
    if(pos!=null && pos.typeOfHit==MovingObjectType.ENTITY && pos.entityHit instanceof NpcPlayerOwned)
      {
      NpcPlayerOwned npc = (NpcPlayerOwned)pos.entityHit;
      if(npc.canBeCommandedBy(player.getCommandSenderName()))
        {      
        onNpcClicked(player, (NpcPlayerOwned) pos.entityHit, player.getHeldItem());        
        }
      }
    }  
  }

@Override
public boolean isFull3D()
  {
  return true;
  }

@Override
public void onKeyAction(EntityPlayer player, ItemStack stack)
  {
  //noop ...or...??
  }

@Override
public boolean onKeyActionClient(EntityPlayer player, ItemStack stack)
  {
  MovingObjectPosition hit = new MovingObjectPosition(player);
  NpcCommand.handleCommandClient(CommandType.CLEAR_COMMAND, hit);
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
  if(world==null || stack==null || entities==null || !(stack.getItem() instanceof ItemCommandBaton)){return;}
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
