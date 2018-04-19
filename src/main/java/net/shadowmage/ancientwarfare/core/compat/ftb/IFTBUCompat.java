package net.shadowmage.ancientwarfare.core.compat.ftb;

import java.util.UUID;

public interface IFTBUCompat {
	boolean areTeamMates(UUID player1, UUID player2);

	boolean areFriendly(UUID player1, UUID player2);
}
