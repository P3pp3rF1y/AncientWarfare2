package net.shadowmage.ancientwarfare.core.research;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.gamedata.AWGameData;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.network.PacketResearchInit;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;

public class ResearchTracker
{

private ResearchTracker(){};
private static ResearchTracker INSTANCE = new ResearchTracker();
public static ResearchTracker instance(){return INSTANCE;}

private Set<Integer> clientResearch = new HashSet<Integer>();

/**
 * SERVER ONLY
 * @param evt
 */
@SubscribeEvent
public void playerLogInEvent(PlayerEvent.PlayerLoggedInEvent evt)
  {
  PacketResearchInit init = new PacketResearchInit(getCompletedResearchFor(evt.player.worldObj, evt.player.getCommandSenderName()));
  NetworkHandler.sendToPlayer((EntityPlayerMP) evt.player, init);
  }

/**
 * SERVER ONLY
 * @param world
 * @param player
 * @param research
 * @return
 */
public boolean hasPlayerCompleted(World world, String player, int research)
  {
  if(world.isRemote){return false;}
  return getResearchData(world).hasPlayerCompletedResearch(player, research);
  }

/**
 * SERVER ONLY
 * @param world
 * @param player
 * @param research
 * @return
 */
public boolean canPlayerLearn(World world, String player, int research)
  {
  if(world.isRemote){return false;}  
  return getResearchData(world).canPlayerLearn(player, research);
  }

/**
 * SERVER ONLY
 * @param world
 * @param playerName
 * @return
 */
public Set<Integer> getCompletedResearchFor(World world, String playerName)
  {
  if(world.isRemote){return Collections.emptySet();}
  return getResearchData(world).getResearchFor(playerName);
  }

/**
 * SERVER ONLY
 * @param world
 * @return
 */
private ResearchData getResearchData(World world)
  {
  if(world.isRemote){return null;}
  return AWGameData.INSTANCE.getData(ResearchData.name, world, ResearchData.class);
  }

/**
 * CLIENT ONLY
 * @return
 */
public Set<Integer> getClientResearch()
  {
  return clientResearch;
  }

/**
 * CLIENT ONLY
 * @param research
 */
public void onClientResearchReceived(Set<Integer> research)
  {
  this.clientResearch.clear();
  this.clientResearch.addAll(research);
  AWLog.logDebug("receiving client research set of: "+research);
  }

/**
 * CLIENT ONLY
 * @param research
 */
public void addClientResearch(int research)
  {
  this.clientResearch.add(research);
  }

public boolean hasClientCompleted(int research)
  {
  return this.clientResearch.contains(research);
  }

}
