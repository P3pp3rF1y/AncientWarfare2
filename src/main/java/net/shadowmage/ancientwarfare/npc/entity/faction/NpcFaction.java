package net.shadowmage.ancientwarfare.npc.entity.faction;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.npc.AncientWarfareNPC;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIAlertFaction;
import net.shadowmage.ancientwarfare.npc.config.AWNPCStatics;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;
import net.shadowmage.ancientwarfare.npc.entity.NpcPlayerOwned;
import net.shadowmage.ancientwarfare.npc.faction.FactionTracker;
import net.shadowmage.ancientwarfare.npc.item.ItemCommandBaton;

public abstract class NpcFaction extends NpcBase
{

protected NpcAIAlertFaction alertAI;

public NpcFaction(World par1World)
  {
  super(par1World);  
  String type = this.getNpcFullType();
  ItemStack eqs;
  for(int i = 0; i < 8; i++)
    {
    eqs = AncientWarfareNPC.statics.getStartingEquipmentForSlot(type, i);
    if(eqs!=null)
      {
      if(i<5){setCurrentItemOrArmor(i, eqs);}
      else if(i==5){ordersStack = eqs;}
      else if(i==6){upkeepStack = eqs;}
      else if(i==7){setShieldStack(eqs);}
      }
    }
  }

@Override
protected boolean interact(EntityPlayer player)
  {
  if(player.worldObj.isRemote){return false;}
  boolean baton = player.getCurrentEquippedItem()!=null && player.getCurrentEquippedItem().getItem() instanceof ItemCommandBaton;
  if(player.capabilities.isCreativeMode && !baton)
    {
    if(player.isSneaking())
      {
      if(this.followingPlayerName==null)
        {
        this.followingPlayerName = player.getCommandSenderName();   
        }
      else if(this.followingPlayerName.equals(player.getCommandSenderName()))
        {
        this.followingPlayerName = null;
        }
      else
        {
        this.followingPlayerName = player.getCommandSenderName();     
        }
      }
    else
      {
      openGUI(player);
      }
    } 
  return true;
  }

@Override
public void openGUI(EntityPlayer player)
  {
  NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_NPC_INVENTORY, getEntityId(), 0, 0);
  }

@Override
public void handleAlertBroadcast(NpcBase broadcaster, EntityLivingBase target)
  {
  if(alertAI!=null)
    {
    alertAI.handleAlert(broadcaster, target);
    }
  }

@Override
public boolean canBeCommandedBy(String playerName)
  {
  return false;
  }

@Override
public boolean isHostileTowards(Entity e)
  {
  if(e instanceof EntityPlayer)
    {
    int standing = FactionTracker.INSTANCE.getStandingFor(worldObj, e.getCommandSenderName(), getFaction());
    if(getNpcFullType().endsWith("elite")){standing-=50;}
    return standing<0;
    }
  else if(e instanceof NpcPlayerOwned)
    {
    NpcBase npc = (NpcBase)e;
    int standing = FactionTracker.INSTANCE.getStandingFor(worldObj, npc.getOwnerName(), getFaction());
    if(getNpcFullType().endsWith("elite")){standing-=50;}
    return standing<0;
    }
  else if(e instanceof NpcFaction)
    {
    NpcFaction npc = (NpcFaction)e;
    return AncientWarfareNPC.statics.shouldFactionBeHostileTowards(getFaction(), npc.getFaction());
    }
  else
    {
    List <String> targets = AncientWarfareNPC.statics.getValidTargetsFor(getNpcFullType(), "");
    String t = EntityList.getEntityString(e);
    if(targets.contains(t))
      {
      return true;
      }
    }
  return false;
  }

@Override
public boolean canTarget(Entity e)
  {
  if(e instanceof NpcFaction)
    {
    NpcFaction npc = (NpcFaction)e;
    return !npc.getFaction().equals(getFaction());
    }
  return e instanceof EntityLivingBase;
  }

@Override
public boolean canBeAttackedBy(Entity e)
  {
  if(e instanceof NpcFaction)
    {
    NpcFaction npc = (NpcFaction)e;
    return !getFaction().equals(npc.getFaction());//can only be attacked by other factions, not your own...disable friendly fire
    }
  return true;
  }

@Override
public void onDeath(DamageSource damageSource)
  {  
  super.onDeath(damageSource);
  if(damageSource.getEntity() instanceof EntityPlayer)
    {
    String faction = getFaction();
    EntityPlayer player = (EntityPlayer)damageSource.getEntity();
    FactionTracker.INSTANCE.adjustStandingFor(worldObj, player.getCommandSenderName(), faction, -AWNPCStatics.factionLossOnDeath);
    }  
  else if(damageSource.getEntity() instanceof NpcPlayerOwned)
    {
    NpcBase npc = (NpcBase)damageSource.getEntity();
    String faction = getFaction();
    String playerName = npc.getOwnerName();
    if(!playerName.isEmpty())
      {
      FactionTracker.INSTANCE.adjustStandingFor(worldObj, playerName, faction, -AWNPCStatics.factionLossOnDeath);
      }
    }
  }

@Override
public String getNpcSubType()
  {
  return "";
  }

public String getFaction()
  {
  String type = getNpcType();
  String faction = type.substring(0, type.indexOf("."));
  return faction;
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
