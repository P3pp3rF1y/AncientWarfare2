package net.shadowmage.ancientwarfare.npc.compat.ebwizardry;

import net.minecraftforge.common.MinecraftForge;
import net.shadowmage.ancientwarfare.core.compat.ICompat;

import java.util.function.Supplier;

public class EBWizardryCompat implements ICompat {

	@Override
	public String getModId() {
		return "ebwizardry";
	}

	@Override
	public void init() {
		Supplier<Runnable> init = () -> () -> MinecraftForge.EVENT_BUS.register(FactionAllyDesignation.class);
		init.get().run();
	}
}
