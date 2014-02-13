package net.shadowmage.ancientwarfare.core.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.NetHandlerPlayServer;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ClientCustomPacketEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ServerCustomPacketEvent;

public class PacketHandler
{

@SubscribeEvent
public void onServerPacket(ServerCustomPacketEvent evt)
  {
  AWLog.logDebug("server packet received");
  PacketBase packet = PacketBase.readPacket(evt.packet.payload());
  packet.setPlayer(((NetHandlerPlayServer)evt.handler).playerEntity);
  packet.execute();
  }

@SubscribeEvent
public void onClientPacket(ClientCustomPacketEvent evt)
  {
  AWLog.logDebug("client packet received");
  PacketBase packet = PacketBase.readPacket(evt.packet.payload());
  packet.setPlayer(Minecraft.getMinecraft().thePlayer);
  packet.execute();
  }

}
