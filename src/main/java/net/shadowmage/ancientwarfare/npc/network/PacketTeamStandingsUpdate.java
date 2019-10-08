package net.shadowmage.ancientwarfare.npc.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.shadowmage.ancientwarfare.core.gamedata.AWGameData;
import net.shadowmage.ancientwarfare.core.network.PacketBase;
import net.shadowmage.ancientwarfare.npc.faction.FactionEntry;
import net.shadowmage.ancientwarfare.npc.gamedata.TeamData;

import java.io.IOException;

public class PacketTeamStandingsUpdate extends PacketBase {
	private ResourceLocation teamName;
	private FactionEntry factionEntry;

	public PacketTeamStandingsUpdate() {}

	public PacketTeamStandingsUpdate(ResourceLocation teamName, FactionEntry factionEntry) {
		this.teamName = teamName;
		this.factionEntry = factionEntry;
	}

	@Override
	protected void writeToStream(ByteBuf data) {
		PacketBuffer buffer = new PacketBuffer(data);
		buffer.writeString(teamName.toString());
		buffer.writeCompoundTag(factionEntry.writeToNBT(new NBTTagCompound()));
	}

	@Override
	protected void readFromStream(ByteBuf data) throws IOException {
		PacketBuffer buffer = new PacketBuffer(data);
		teamName = new ResourceLocation(buffer.readString(100));
		NBTTagCompound tag = buffer.readCompoundTag();
		factionEntry = tag == null ? null : new FactionEntry(tag);
	}

	@Override
	protected void execute(EntityPlayer player) {
		TeamData teamData = AWGameData.INSTANCE.getData(player.world, TeamData.class);
		teamData.updateTeamStandings(teamName, factionEntry);
	}
}
