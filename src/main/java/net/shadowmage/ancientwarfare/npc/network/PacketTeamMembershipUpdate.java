package net.shadowmage.ancientwarfare.npc.network;

import com.google.common.collect.ImmutableMap;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.shadowmage.ancientwarfare.core.gamedata.AWGameData;
import net.shadowmage.ancientwarfare.core.network.PacketBase;
import net.shadowmage.ancientwarfare.npc.gamedata.TeamData;

import java.io.IOException;
import java.util.Map;

public class PacketTeamMembershipUpdate extends PacketBase {
	private ResourceLocation teamName;
	private String playerName;
	private Action action;

	public PacketTeamMembershipUpdate() {}

	public PacketTeamMembershipUpdate(ResourceLocation teamName, String playerName, Action action) {
		this.teamName = teamName;
		this.playerName = playerName;
		this.action = action;
	}

	@Override
	protected void writeToStream(ByteBuf data) {
		PacketBuffer buffer = new PacketBuffer(data);
		buffer.writeString(teamName.toString());
		buffer.writeString(playerName);
		buffer.writeShort(action.getId());
	}

	@Override
	protected void readFromStream(ByteBuf data) throws IOException {
		PacketBuffer buffer = new PacketBuffer(data);
		teamName = new ResourceLocation(buffer.readString(100));
		playerName = buffer.readString(100);
		action = Action.fromId(buffer.readShort());
	}

	@Override
	protected void execute(EntityPlayer player) {
		TeamData teamData = AWGameData.INSTANCE.getData(player.world, TeamData.class);
		if (action == Action.ADD) {
			teamData.addTeamMember(teamName, playerName);
		} else if (action == Action.REMOVE) {
			teamData.removeTeamMember(teamName, playerName, player.world.getTotalWorldTime(), false);
		}
	}

	public enum Action {
		ADD(0),
		REMOVE(1);

		private int id;

		Action(int id) {
			this.id = id;
		}

		public int getId() {
			return id;
		}

		private static final Map<Integer, Action> VALUES;

		static {
			ImmutableMap.Builder<Integer, Action> builder = new ImmutableMap.Builder<>();
			for (Action action : values()) {
				builder.put(action.getId(), action);
			}
			VALUES = builder.build();
		}

		public static Action fromId(int id) {
			return VALUES.get(id);
		}
	}
}
