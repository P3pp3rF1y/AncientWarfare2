package net.shadowmage.ancientwarfare.core.network;

import net.shadowmage.ancientwarfare.core.research.ResearchTracker;
import net.shadowmage.ancientwarfare.core.util.StringTools;
import io.netty.buffer.ByteBuf;

public class PacketResearchStart extends PacketBase
{

String playerName;
int toAdd;
boolean start;

public PacketResearchStart(String playerName, int toAdd, boolean start)
  {
  this.playerName = playerName;
  this.toAdd = toAdd;
  this.start = start;
  }

public PacketResearchStart()
  {
  
  }

@Override
protected void writeToStream(ByteBuf data)
  {  
  data.writeInt(toAdd);
  StringTools.writeString(data, playerName);
  data.writeBoolean(start);
  }

@Override
protected void readFromStream(ByteBuf data)
  {
  toAdd = data.readInt();
  playerName = StringTools.readString(data);
  start = data.readBoolean();
  }

@Override
protected void execute()
  {
  if(start)
    {
    ResearchTracker.instance().startResearch(player.worldObj, playerName, toAdd);    
    }
  else
    {
    ResearchTracker.instance().finishResearch(player.worldObj, playerName, toAdd);
    }
  }

}
