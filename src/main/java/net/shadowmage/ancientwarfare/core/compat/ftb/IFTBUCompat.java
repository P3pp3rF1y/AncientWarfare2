package net.shadowmage.ancientwarfare.core.compat.ftb;

import javax.annotation.Nullable;
import java.util.UUID;

public interface IFTBUCompat {
	boolean areTeamMates(UUID player1, UUID player2);

	boolean areFriendly(@Nullable UUID player1, UUID player2);
}
