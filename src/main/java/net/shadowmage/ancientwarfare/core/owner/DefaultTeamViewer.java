package net.shadowmage.ancientwarfare.core.owner;

import net.minecraft.scoreboard.Team;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.UUID;

public class DefaultTeamViewer implements ITeamViewer {
	@Override
	public boolean areTeamMates(World world, UUID player1, UUID player2, String playerName1, String playerName2) {
		return player1.equals(player2) || isSameTeam(world, playerName1, playerName2);
	}

	@Override
	public boolean areFriendly(World world, UUID player1, @Nullable UUID player2, String playerName1, String playerName2) {
		return playerName1.equals(playerName2) || player1.equals(player2) || isSameTeam(world, playerName1, playerName2);
	}

	private boolean isSameTeam(World world, String playerName1, String playerName2) {
		Team team = world.getScoreboard().getPlayersTeam(playerName1);
		return team != null && team.isSameTeam(world.getScoreboard().getPlayersTeam(playerName2));
	}

}
