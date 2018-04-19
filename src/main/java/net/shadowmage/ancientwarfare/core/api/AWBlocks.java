package net.shadowmage.ancientwarfare.core.api;

import net.minecraft.block.Block;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;

@ObjectHolder(AncientWarfareCore.modID)
public class AWBlocks {

	/*
	 * CORE module
	 */
	@ObjectHolder("engineering_station")
	public static Block engineeringStation;
	@ObjectHolder("research_station")
	public static Block researchStation;
}
