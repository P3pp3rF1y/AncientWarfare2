package net.shadowmage.ancientwarfare.core.owner;

import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;

public class ScoreboardTeamViewer implements ITeamViewer {
	@Override
	public boolean areTeamMates(World world, UUID player1, UUID player2, String playerName1, String playerName2) {
		return player1.equals(player2) || isSameTeam(world, playerName1, playerName2);
	}

	@Override
	public boolean areFriendly(World world, UUID player1, @Nullable UUID player2, String playerName1, String playerName2) {
		return playerName1.equals(playerName2) || player1.equals(player2) || isSameTeam(world, playerName1, playerName2);
	}

	@Override
	public Set<ResourceLocation> getPlayerTeamNames(World world, UUID playerId, String playerName) {
		ScorePlayerTeam team = world.getScoreboard().getPlayersTeam(playerName);
		return team == null ? Collections.emptySet() : Collections.singleton(new ResourceLocation(team.getName()));
	}

	@Override
	public String getName() {
		return "minecraft";
	}

	private boolean isSameTeam(World world, String playerName1, String playerName2) {
		Team team = world.getScoreboard().getPlayersTeam(playerName1);
		return team != null && team.isSameTeam(world.getScoreboard().getPlayersTeam(playerName2));
	}
}
