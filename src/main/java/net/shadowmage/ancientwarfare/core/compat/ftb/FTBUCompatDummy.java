package net.shadowmage.ancientwarfare.core.compat.ftb;

import java.util.UUID;

public class FTBUCompatDummy implements IFTBUCompat {
    @Override
    public boolean areTeamMates(UUID player1, UUID player2) {
        return player1 != null && player1.equals(player2);
    }

    @Override
    public boolean areFriendly(UUID player1, UUID player2) {
        return false;
    }
}
