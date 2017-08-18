package net.shadowmage.ancientwarfare.core.network;

import net.minecraft.network.NetHandlerPlayServer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;

public class PacketHandlerServer {

    @SubscribeEvent
    public void onServerPacket(FMLNetworkEvent.ServerCustomPacketEvent evt) {
        PacketBase.readPacket(evt.getPacket().payload()).execute(((NetHandlerPlayServer) evt.getHandler()).playerEntity);
    }

    @SubscribeEvent
    public void onClientPacket(FMLNetworkEvent.ClientCustomPacketEvent evt) {
        PacketBase.readPacket(evt.getPacket().payload()).execute(AncientWarfareCore.proxy.getClientPlayer());
    }
}
