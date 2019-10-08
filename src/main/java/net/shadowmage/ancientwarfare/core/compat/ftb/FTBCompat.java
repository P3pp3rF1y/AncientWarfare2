package net.shadowmage.ancientwarfare.core.compat.ftb;

import net.shadowmage.ancientwarfare.core.compat.ICompat;
import net.shadowmage.ancientwarfare.core.owner.TeamViewerRegistry;

public class FTBCompat implements ICompat {
	@Override
	public String getModId() {
		return "ftblib";
	}

	@Override
	public void init() {
		TeamViewerRegistry.registerTeamViewer(new FTBTeamViewer());
	}
}
