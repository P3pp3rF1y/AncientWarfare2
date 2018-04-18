package net.shadowmage.ancientwarfare.core.compat.ftb;

import com.feed_the_beast.ftblib.lib.data.ForgePlayer;
import com.feed_the_beast.ftblib.lib.data.Universe;
import net.minecraft.util.Tuple;

import java.util.UUID;
import java.util.function.Function;

public class FTBUCompat extends FTBUCompatDummy {
	@Override
	public boolean areTeamMates(UUID player1, UUID player2) {
		return areInTheSameTeam(player1, player2) || super.areTeamMates(player1, player2);
	}

	private boolean areInTheSameTeam(UUID playerUUID1, UUID playerUUID2) {
		return checkFTBPlayers(playerUUID1, playerUUID2, t -> t.getFirst().team != null && t.getFirst().team.isMember(t.getSecond()));
	}

	private boolean checkFTBPlayers(UUID playerUUID1, UUID playerUUID2, Function<Tuple<ForgePlayer, ForgePlayer>, Boolean> playerCheck) {
		if (!Universe.loaded()) {
			return false;
		}

		Universe uni = Universe.get();
		ForgePlayer player1 = uni.getPlayer(playerUUID1);
		ForgePlayer player2 = uni.getPlayer(playerUUID2);

		return player1 != null && player2 != null && playerCheck.apply(new Tuple<>(player1, player2));
	}

	@Override
	public boolean areFriendly(UUID player1, UUID player2) {
		return checkFTBPlayers(player1, player2, t -> t.getFirst().team != null && (t.getFirst().team.isMember(t.getSecond()) || t.getFirst().team.isAlly(t.getSecond()))) || super.areFriendly(player1, player2);
	}
}
