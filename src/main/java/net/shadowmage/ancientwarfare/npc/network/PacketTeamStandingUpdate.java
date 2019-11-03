package net.shadowmage.ancientwarfare.npc.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.shadowmage.ancientwarfare.core.gamedata.AWGameData;
import net.shadowmage.ancientwarfare.core.network.PacketBase;
import net.shadowmage.ancientwarfare.npc.gamedata.TeamData;

import java.io.IOException;

public class PacketTeamStandingUpdate extends PacketBase {
	private ResourceLocation teamName;
	private String factionName;
	private int standing;

	public PacketTeamStandingUpdate() {}

	public PacketTeamStandingUpdate(ResourceLocation teamName, String factionName, int standing) {
		this.teamName = teamName;
		this.factionName = factionName;
		this.standing = standing;
	}

	@Override
	protected void writeToStream(ByteBuf data) {
		PacketBuffer buffer = new PacketBuffer(data);
		buffer.writeString(teamName.toString());
		buffer.writeString(factionName);
		buffer.writeInt(standing);
	}

	@Override
	protected void readFromStream(ByteBuf data) throws IOException {
		PacketBuffer buffer = new PacketBuffer(data);
		teamName = new ResourceLocation(buffer.readString(100));
		factionName = buffer.readString(100);
		standing = buffer.readInt();
	}

	@Override
	protected void execute(EntityPlayer player) {
		TeamData teamData = AWGameData.INSTANCE.getData(player.world, TeamData.class);
		teamData.updateTeamStanding(teamName, factionName, standing);
	}
}
