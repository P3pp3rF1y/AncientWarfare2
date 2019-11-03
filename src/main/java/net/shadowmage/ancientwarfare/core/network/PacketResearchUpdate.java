package net.shadowmage.ancientwarfare.core.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.shadowmage.ancientwarfare.core.research.ResearchTracker;

public class PacketResearchUpdate extends PacketBase {

	private String playerName;
	private String toAdd;
	private boolean add;
	private boolean live;

	public PacketResearchUpdate(String playerName, String toAdd, boolean add, boolean live) {
		this.playerName = playerName;
		this.toAdd = toAdd;
		this.add = add;
		this.live = live;
	}

	public PacketResearchUpdate() {}

	@Override
	protected void writeToStream(ByteBuf data) {
		PacketBuffer buffer = new PacketBuffer(data);
		buffer.writeString(toAdd);
		buffer.writeBoolean(add);
		buffer.writeBoolean(live);
		buffer.writeString(playerName);
	}

	@Override
	protected void readFromStream(ByteBuf data) {
		PacketBuffer buffer = new PacketBuffer(data);
		toAdd = buffer.readString(40);
		add = buffer.readBoolean();
		live = buffer.readBoolean();
		playerName = buffer.readString(16);
	}

	@Override
	protected void execute(EntityPlayer player) {
		if (live) {
			if (add) {
				ResearchTracker.INSTANCE.addResearch(player.world, playerName, toAdd);
			}
		} else {
			if (add) {
				ResearchTracker.INSTANCE.addQueuedGoal(player.world, playerName, toAdd);
			} else {
				ResearchTracker.INSTANCE.removeQueuedGoal(player.world, playerName, toAdd);
			}
		}
	}

}
