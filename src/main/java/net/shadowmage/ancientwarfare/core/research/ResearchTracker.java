package net.shadowmage.ancientwarfare.core.research;

import java.util.Set;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.gamedata.AWGameData;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.network.PacketResearchInit;
import net.shadowmage.ancientwarfare.core.network.PacketResearchUpdate;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;

public class ResearchTracker
{

private ResearchTracker(){};
private static ResearchTracker INSTANCE = new ResearchTracker();
public static ResearchTracker instance(){return INSTANCE;}

private ResearchData clientData = new ResearchData();

/**
 * SERVER ONLY
 * @param evt
 */
@SubscribeEvent
public void playerLogInEvent(PlayerEvent.PlayerLoggedInEvent evt)
  {
  PacketResearchInit init = new PacketResearchInit(getResearchData(evt.player.worldObj));
  NetworkHandler.sendToPlayer((EntityPlayerMP) evt.player, init);
  }

public void addResearch(World world, String playerName, int research)
  {
  if(world.isRemote)
    {
    clientData.addResearchTo(playerName, research);
    }
  else
    {
    getResearchData(world).addResearchTo(playerName, research);
    PacketResearchUpdate pkt = new PacketResearchUpdate(playerName, research);
    NetworkHandler.sendToAllPlayers(pkt);
    }
  }

/**
 * @param world
 * @param player
 * @param research
 * @return
 */
public boolean hasPlayerCompleted(World world, String player, int research)
  {
  if(world.isRemote)
    {
    return clientData.hasPlayerCompletedResearch(player, research);
    }
  return getResearchData(world).hasPlayerCompletedResearch(player, research);
  }

/**
 * @param world
 * @param player
 * @param research
 * @return
 */
public boolean canPlayerLearn(World world, String player, int research)
  {
  if(world.isRemote)
    {
    return clientData.canPlayerLearn(player, research);
    } 
  return getResearchData(world).canPlayerLearn(player, research);
  }

/**
 * @param world
 * @param playerName
 * @return
 */
public Set<Integer> getCompletedResearchFor(World world, String playerName)
  {
  if(world.isRemote)
    {
    return clientData.getResearchFor(playerName);
    }
  return getResearchData(world).getResearchFor(playerName);
  }

/**
 * @param world
 * @return
 */
private ResearchData getResearchData(World world)
  {
  if(world.isRemote)
    {
    return clientData;
    }
  return AWGameData.INSTANCE.getData(ResearchData.name, world, ResearchData.class);
  }

/**
 * CLIENT ONLY
 * @param research
 */
public void onClientResearchReceived(NBTTagCompound researchDataTag)
  {
  AWLog.logDebug("receiving client research set of: "+researchDataTag);
  this.clientData.readFromNBT(researchDataTag);
  }


}
