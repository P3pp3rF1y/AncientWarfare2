package net.shadowmage.ancientwarfare.core.research;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;

public class ResearchTracker
{

private ResearchTracker(){};
private static ResearchTracker INSTANCE = new ResearchTracker();
public static ResearchTracker instance(){return INSTANCE;}

@SubscribeEvent
public void playerLogInEvent(PlayerEvent.PlayerLoggedInEvent evt)
  {
  //send players research to that client
  }

}
