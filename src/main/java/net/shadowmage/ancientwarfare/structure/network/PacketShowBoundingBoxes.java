package net.shadowmage.ancientwarfare.structure.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.network.PacketBase;

import static net.shadowmage.ancientwarfare.structure.render.StructureEntryBBRenderer.SHOW_BBS_TAG;

public class PacketShowBoundingBoxes extends PacketBase {
	private boolean shouldShow;

	public PacketShowBoundingBoxes() {}

	public PacketShowBoundingBoxes(boolean shouldShow) {
		this.shouldShow = shouldShow;
	}

	@Override
	protected void writeToStream(ByteBuf data) {
		data.writeBoolean(shouldShow);
	}

	@Override
	protected void readFromStream(ByteBuf data) {
		shouldShow = data.readBoolean();
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected void execute() {
		EntityPlayerSP player = Minecraft.getMinecraft().player;
		if (shouldShow) {
			player.addTag(SHOW_BBS_TAG);
		} else {
			player.removeTag(SHOW_BBS_TAG);
		}
	}
}
