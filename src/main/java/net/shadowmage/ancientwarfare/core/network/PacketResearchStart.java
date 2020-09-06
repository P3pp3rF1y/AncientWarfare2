package net.shadowmage.ancientwarfare.core.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.shadowmage.ancientwarfare.core.research.ResearchTracker;

public class PacketResearchStart extends PacketBase {
	private String playerName;
	private String toAdd;
	private boolean start;

	public PacketResearchStart(String playerName, String toAdd, boolean start) {
		this.playerName = playerName;
		this.toAdd = toAdd;
		this.start = start;
	}

	public PacketResearchStart() {}

	@Override
	protected void writeToStream(ByteBuf data) {
		PacketBuffer buffer = new PacketBuffer(data);
		buffer.writeString(toAdd);
		buffer.writeString(playerName);
		buffer.writeBoolean(start);
	}

	@Override
	protected void readFromStream(ByteBuf data) {
		PacketBuffer buffer = new PacketBuffer(data);
		toAdd = buffer.readString(40);
		playerName = buffer.readString(16);
		start = buffer.readBoolean();
	}

	@Override
	protected void execute(EntityPlayer player) {
		if (start) {
			ResearchTracker.INSTANCE.startResearch(player.world, playerName, toAdd);
		} else {
			ResearchTracker.INSTANCE.finishResearch(player.world, playerName, toAdd);
		}
	}

}
