package net.shadowmage.ancientwarfare.core.network;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ServerCustomPacketEvent;
import net.minecraft.network.NetHandlerPlayServer;

public class PacketHandlerServer {

    @SubscribeEvent
    public void onServerPacket(ServerCustomPacketEvent evt) {
        PacketBase packet = PacketBase.readPacket(evt.packet.payload());
        packet.setPlayer(((NetHandlerPlayServer) evt.handler).playerEntity);
        packet.execute();
    }


}
