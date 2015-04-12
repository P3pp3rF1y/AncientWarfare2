package net.shadowmage.ancientwarfare.core.network;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import net.minecraft.network.NetHandlerPlayServer;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;

public class PacketHandlerServer {

    @SubscribeEvent
    public void onServerPacket(FMLNetworkEvent.ServerCustomPacketEvent evt) {
        PacketBase.readPacket(evt.packet.payload()).execute(((NetHandlerPlayServer) evt.handler).playerEntity);
    }

    @SubscribeEvent
    public void onClientPacket(FMLNetworkEvent.ClientCustomPacketEvent evt) {
        PacketBase.readPacket(evt.packet.payload()).execute(AncientWarfareCore.proxy.getClientPlayer());
    }
}
