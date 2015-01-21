package net.shadowmage.ancientwarfare.core.network;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ClientCustomPacketEvent;
import net.minecraft.client.Minecraft;

public class PacketHandlerClient {

    @SubscribeEvent
    public void onClientPacket(ClientCustomPacketEvent evt) {
        PacketBase packet = PacketBase.readPacket(evt.packet.payload());
        packet.setPlayer(Minecraft.getMinecraft().thePlayer);
        packet.execute();
    }

}
