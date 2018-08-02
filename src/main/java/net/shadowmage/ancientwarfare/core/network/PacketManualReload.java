package net.shadowmage.ancientwarfare.core.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.manual.ManualContentRegistry;
import net.shadowmage.ancientwarfare.core.registry.RegistryLoader;

import java.io.IOException;

public class PacketManualReload extends PacketBase {
	@Override
	protected void writeToStream(ByteBuf data) {
		//noop
	}

	@Override
	protected void readFromStream(ByteBuf data) throws IOException {
		//noop
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected void execute() {
		ManualContentRegistry.clearContents();
		RegistryLoader.reload("manual_content");
		Minecraft.getMinecraft().player.sendMessage(new TextComponentString("Manual content reloaded"));
	}
}
