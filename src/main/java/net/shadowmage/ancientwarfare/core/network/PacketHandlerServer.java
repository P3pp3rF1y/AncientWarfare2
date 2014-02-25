package net.shadowmage.ancientwarfare.core.network;

import net.minecraft.network.NetHandlerPlayServer;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ServerCustomPacketEvent;

public class PacketHandlerServer
{

@SubscribeEvent
public void onServerPacket(ServerCustomPacketEvent evt)
  {
  AWLog.logDebug("server packet received");
  PacketBase packet = PacketBase.readPacket(evt.packet.payload());
  packet.setPlayer(((NetHandlerPlayServer)evt.handler).playerEntity);
  packet.execute();
  }


}
