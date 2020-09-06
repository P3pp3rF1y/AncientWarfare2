package net.shadowmage.ancientwarfare.npc.faction;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.shadowmage.ancientwarfare.core.gamedata.AWGameData;
import net.shadowmage.ancientwarfare.npc.gamedata.FactionData;
import net.shadowmage.ancientwarfare.npc.gamedata.TeamData;

import java.util.UUID;
import java.util.function.ToIntFunction;

public class FactionTracker {
	public static final FactionTracker INSTANCE = new FactionTracker();

	@SubscribeEvent
	public void onPlayerLogin(PlayerLoggedInEvent evt) {
		onPlayerLogin(evt.player);
	}

	private void onPlayerLogin(EntityPlayer player) {
		FactionData data = AWGameData.INSTANCE.getData(player.world, FactionData.class);
		data.onPlayerLogin(player);

		TeamData teamData = AWGameData.INSTANCE.getData(player.world, TeamData.class);
		teamData.checkAndUpdatePlayerTeamMemberships(player);
	}

	public void adjustStandingFor(World world, String playerName, String factionName, int adjustment) {
		if (world.isRemote) {
			throw new IllegalArgumentException("Cannot adjust standing on client world!");
		}
		FactionData data = AWGameData.INSTANCE.getData(world, FactionData.class);
		TeamData teamData = AWGameData.INSTANCE.getData(world, TeamData.class);
		teamData.adjustStanding(world, data, playerName, factionName, adjustment);
	}

	public void setStandingFor(World world, String playerName, String factionName, int standing) {
		if (world.isRemote) {
			throw new IllegalArgumentException("Cannot set standing on client world!");
		}
		FactionData data = AWGameData.INSTANCE.getData(world, FactionData.class);
		data.setStandingFor(playerName, factionName, standing);
	}

	public boolean isHostileToPlayer(World world, UUID playerUUID, String playerName, String factionName) {
		return getStandingFor(world, playerUUID, playerName, factionName) < 0;
	}

	private int getStandingFor(World world, UUID playerUUID, String playerName, String factionName) {
		return getStandingFor(world, playerName, factionName, teamData -> teamData.getWorstStandingFor(world, playerUUID, playerName, factionName, world.getTotalWorldTime()));
	}

	public int getStandingFor(World world, String playerName, String factionName) {
		return getStandingFor(world, playerName, factionName, teamData -> teamData.getWorstStandingFor(playerName, factionName, world.getTotalWorldTime()));
	}

	private int getStandingFor(World world, String playerName, String factionName, ToIntFunction<TeamData> getTeamStanding) {
		FactionData data = AWGameData.INSTANCE.getData(world, FactionData.class);

		int teamStanding = getTeamStanding.applyAsInt(AWGameData.INSTANCE.getData(world, TeamData.class));
		int playerStanding = data.getStandingFor(playerName, factionName);
		return playerStanding < teamStanding ? playerStanding : teamStanding;
	}
}
