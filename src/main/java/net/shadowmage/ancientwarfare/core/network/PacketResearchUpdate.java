package net.shadowmage.ancientwarfare.core.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.shadowmage.ancientwarfare.core.research.ResearchTracker;
import net.shadowmage.ancientwarfare.core.util.StringTools;

public class PacketResearchUpdate extends PacketBase {

	String playerName;
	int toAdd;
	boolean add, live;

	public PacketResearchUpdate(String playerName, int toAdd, boolean add, boolean live) {
		this.playerName = playerName;
		this.toAdd = toAdd;
		this.add = add;
		this.live = live;
	}

	public PacketResearchUpdate() {

	}

	@Override
	protected void writeToStream(ByteBuf data) {
		data.writeInt(toAdd);
		data.writeBoolean(add);
		data.writeBoolean(live);
		StringTools.writeString(data, playerName);
	}

	@Override
	protected void readFromStream(ByteBuf data) {
		toAdd = data.readInt();
		add = data.readBoolean();
		live = data.readBoolean();
		playerName = StringTools.readString(data);
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
