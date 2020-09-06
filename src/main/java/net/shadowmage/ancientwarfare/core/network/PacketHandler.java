package net.shadowmage.ancientwarfare.core.network;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.IOException;

public class PacketHandler {
	@SubscribeEvent
	public void onServerPacket(FMLNetworkEvent.ServerCustomPacketEvent evt) throws IOException {
		PacketBase packet = PacketBase.readPacket(evt.getPacket().payload());
		EntityPlayer player = ((NetHandlerPlayServer) evt.getHandler()).player;

		((WorldServer) player.world).addScheduledTask(() -> packet.execute(player));
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onClientPacket(FMLNetworkEvent.ClientCustomPacketEvent evt) throws IOException {
		PacketBase packet = PacketBase.readPacket(evt.getPacket().payload());

		Minecraft.getMinecraft().addScheduledTask(() -> packet.execute(Minecraft.getMinecraft().player));
	}
}
