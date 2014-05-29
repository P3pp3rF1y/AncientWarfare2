package net.shadowmage.ancientwarfare.npc.entity.faction;

import io.netty.buffer.ByteBuf;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.scoreboard.Team;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.npc.AncientWarfareNPC;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;
import net.shadowmage.ancientwarfare.npc.entity.NpcPlayerOwned;
import cpw.mods.fml.common.network.ByteBufUtils;

public abstract class NpcFactionBase extends NpcBase
{

String subType = "";

public NpcFactionBase(World par1World)
  {
  super(par1World);
  }

@Override
public boolean isHostileTowards(Entity e)
  {
  if(e instanceof EntityPlayer)
    {
    return true;//TODO
    }
  else if(e instanceof NpcPlayerOwned)
    {
    return true;//TODO
    }
  else
    {
    List <String> targets = AncientWarfareNPC.statics.getValidTargetsFor(getFaction(), "");
    String t = EntityList.getEntityString(e);
    return targets.contains(t);
    }
  }

@Override
public boolean isHostileTowards(Team team)
  {
  return super.isHostileTowards(team);
  }

public void setSubtype(String subtype)
  {
  if(subtype==null){subtype="";}
  this.subType = subtype;
  }

@Override
public String getNpcSubType()
  {
  return subType;
  }

public String getFaction()
  {
  return getNpcType().split(".")[0];
  }

@Override
public void writeEntityToNBT(NBTTagCompound tag)
  {
  super.writeEntityToNBT(tag);
  tag.setString("subType", subType);
  }

@Override
public void readEntityFromNBT(NBTTagCompound tag)
  {  
  super.readEntityFromNBT(tag);
  subType = tag.getString("subType");
  }

@Override
public void writeSpawnData(ByteBuf buffer)
  {  
  super.writeSpawnData(buffer);
  ByteBufUtils.writeUTF8String(buffer, subType);
  }

@Override
public void readSpawnData(ByteBuf additionalData)
  {
  super.readSpawnData(additionalData);
  subType = ByteBufUtils.readUTF8String(additionalData);
  }
  
@Override
public void readAdditionalItemData(NBTTagCompound tag)
  {
  //TODO
  }

@Override
public void writeAdditionalItemData(NBTTagCompound tag)
  {
   //TODO
  }

@Override
public boolean isValidOrdersStack(ItemStack stack)
  {
  //noop, no orders
  return false;
  }

@Override
public void onOrdersInventoryChanged()
  {
  //noop, no orders
  }

@Override
public void onWeaponInventoryChanged()
  {
  //noop, no inventory changing
  }

}
