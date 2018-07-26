package net.shadowmage.ancientwarfare.core.compat.ftb;

import com.feed_the_beast.ftblib.lib.data.ForgePlayer;
import com.feed_the_beast.ftblib.lib.data.Universe;
import net.minecraft.util.Tuple;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.owner.DefaultTeamViewer;

import javax.annotation.Nullable;
import java.util.UUID;
import java.util.function.Function;

public class FTBTeamViewer extends DefaultTeamViewer {
	@Override
	public boolean areTeamMates(World world, UUID player1, UUID player2, String playerName1, String playerName2) {
		return super.areTeamMates(world, player1, player2, playerName1, playerName2) || areInTheSameTeam(player1, player2);
	}

	private boolean areInTheSameTeam(UUID playerUUID1, UUID playerUUID2) {
		return checkFTBPlayers(playerUUID1, playerUUID2, t -> t.getFirst().team != null && t.getFirst().team.isMember(t.getSecond()));
	}

	private boolean checkFTBPlayers(UUID playerUUID1, @Nullable UUID playerUUID2, Function<Tuple<ForgePlayer, ForgePlayer>, Boolean> playerCheck) {
		if (!Universe.loaded()) {
			return false;
		}

		Universe uni = Universe.get();
		ForgePlayer player1 = uni.getPlayer(playerUUID1);
		ForgePlayer player2 = uni.getPlayer(playerUUID2);

		return player1 != null && player2 != null && playerCheck.apply(new Tuple<>(player1, player2));
	}

	@Override
	public boolean areFriendly(World world, UUID player1, @Nullable UUID player2, String playerName1, String playerName2) {
		return super.areFriendly(world, player1, player2, playerName1, playerName2) ||
				checkFTBPlayers(player1, player2, t -> t.getFirst().team != null && (t.getFirst().team.isMember(t.getSecond()) || t.getFirst().team.isAlly(t.getSecond())));
	}
}
