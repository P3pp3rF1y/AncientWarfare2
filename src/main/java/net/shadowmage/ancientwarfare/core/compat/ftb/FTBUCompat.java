package net.shadowmage.ancientwarfare.core.compat.ftb;

import com.feed_the_beast.ftbl.api.FTBLibAPI;
import com.feed_the_beast.ftbl.api.IForgePlayer;
import com.feed_the_beast.ftbl.api.IUniverse;
import net.minecraft.util.Tuple;

import java.util.UUID;
import java.util.function.Function;

public class FTBUCompat extends FTBUCompatDummy {
    @Override
    public boolean areTeamMates(UUID player1, UUID player2) {
        return areInTheSameTeam(player1, player2) || super.areTeamMates(player1, player2);
    }

    private boolean areInTheSameTeam(UUID playerUUID1, UUID playerUUID2) {
        return checkFTBPlayers(playerUUID1, playerUUID2, t -> t.getFirst().getTeam() != null && t.getFirst().getTeam().isMember(t.getSecond()));
    }

    private boolean checkFTBPlayers(UUID playerUUID1, UUID playerUUID2, Function<Tuple<IForgePlayer, IForgePlayer>, Boolean> playerCheck) {
        FTBLibAPI ftb = FTBLibAPI.API;

        if (ftb.hasUniverse()) {
            IUniverse uni = ftb.getUniverse();
            IForgePlayer player1 = uni.getPlayer(playerUUID1);
            IForgePlayer player2 = uni.getPlayer(playerUUID2);

            return player1 != null && player2 != null && playerCheck.apply(new Tuple<>(player1, player2));
        }
        return false;
    }

    @Override
    public boolean areFriendly(UUID player1, UUID player2) {
        return checkFTBPlayers(player1, player2, t -> t.getFirst().getTeam()!= null && (t.getFirst().getTeam().isMember(t.getSecond()) || t.getFirst().getTeam().isAlly(t.getSecond())));
    }
}
