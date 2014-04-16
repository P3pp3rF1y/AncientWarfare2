package net.shadowmage.ancientwarfare.core.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import net.shadowmage.ancientwarfare.core.research.ResearchTracker;
import net.shadowmage.ancientwarfare.core.util.StringTools;

public class PacketResearchUpdate extends PacketBase
{

String playerName;
int toAdd;

public PacketResearchUpdate(String playerName, int toAdd)
  {
  this.playerName = playerName;
  this.toAdd = toAdd;
  }

public PacketResearchUpdate()
  {
  
  }

@Override
protected void writeToStream(ByteBuf data)
  {  
  data.writeInt(toAdd);
  StringTools.writeString(data, playerName);
  }

@Override
protected void readFromStream(ByteBuf data)
  {
  toAdd = data.readInt();
  playerName = StringTools.readString(data);
  }

@Override
protected void execute()
  {
  ResearchTracker.instance().addResearch(player.worldObj, playerName, toAdd);
  }

}
