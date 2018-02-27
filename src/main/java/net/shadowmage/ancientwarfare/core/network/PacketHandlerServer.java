package net.shadowmage.ancientwarfare.core.network;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;

public class PacketHandlerServer {

    @SubscribeEvent
    public void onServerPacket(FMLNetworkEvent.ServerCustomPacketEvent evt) {
		PacketBase packet = PacketBase.readPacket(evt.getPacket().payload());
		EntityPlayer player  = ((NetHandlerPlayServer) evt.getHandler()).player;

        ((WorldServer) player.world).addScheduledTask(() -> packet.execute(player));
    }

    @SubscribeEvent
    public void onClientPacket(FMLNetworkEvent.ClientCustomPacketEvent evt) {
		PacketBase packet = PacketBase.readPacket(evt.getPacket().payload());

        Minecraft.getMinecraft().addScheduledTask(() -> packet.execute(AncientWarfareCore.proxy.getClientPlayer()));
    }
}
