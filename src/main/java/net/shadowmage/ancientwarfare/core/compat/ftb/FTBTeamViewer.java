package net.shadowmage.ancientwarfare.core.compat.ftb;

import com.feed_the_beast.ftblib.lib.data.ForgePlayer;
import com.feed_the_beast.ftblib.lib.data.ForgeTeam;
import com.feed_the_beast.ftblib.lib.data.Universe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.owner.ITeamViewer;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiPredicate;

public class FTBTeamViewer implements ITeamViewer {
	private static final String FTBLIB_MOD_ID = "ftblib";

	@Override
	public boolean areTeamMates(World world, UUID player1, UUID player2, String playerName1, String playerName2) {
		return areInTheSameTeam(player1, player2);
	}

	private boolean areInTheSameTeam(UUID playerUUID1, UUID playerUUID2) {
		return checkFTBPlayers(playerUUID1, playerUUID2, (player1, player2) -> player1.team != null && player1.team.isMember(player2));
	}

	private boolean checkFTBPlayers(UUID playerUUID1, @Nullable UUID playerUUID2, BiPredicate<ForgePlayer, ForgePlayer> playerCheck) {
		if (!Universe.loaded()) {
			return false;
		}

		Universe uni = Universe.get();
		ForgePlayer player1 = uni.getPlayer(playerUUID1);
		ForgePlayer player2 = uni.getPlayer(playerUUID2);

		return player1 != null && player2 != null && playerCheck.test(player1, player2);
	}

	@Override
	public boolean areFriendly(World world, UUID playerUUID1, @Nullable UUID playerUUID2, String playerName1, String playerName2) {
		return checkFTBPlayers(playerUUID1, playerUUID2, (player1, player2) -> player1.team != null && player1.team.isAlly(player2));
	}

	@Override
	public Set<ResourceLocation> getPlayerTeamNames(World world, UUID playerId, String playerName) {
		if (!Universe.loaded()) {
			return Collections.emptySet();
		}

		Universe uni = Universe.get();

		ForgePlayer player = uni.getPlayer(playerId);
		if (player == null) {
			return Collections.emptySet();
		}

		ForgeTeam team = player.team;
		return team == null ? Collections.emptySet() : Collections.singleton(new ResourceLocation(FTBLIB_MOD_ID, team.getId()));
	}

	@Override
	public boolean needsRegularMembershipRecheck() {
		return false;
	}

	@Override
	public String getName() {
		return FTBLIB_MOD_ID;
	}
}
