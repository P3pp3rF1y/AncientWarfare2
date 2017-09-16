package net.shadowmage.ancientwarfare.structure.block;

import net.minecraft.block.Block;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.shadowmage.ancientwarfare.structure.AncientWarfareStructures;

@ObjectHolder(AncientWarfareStructures.modID)
public class AWStructuresBlocks {
	@ObjectHolder("advanced_spawner")
	public static Block advancedSpawner;
	@ObjectHolder("gate_proxy")
	public static Block gateProxy;
	@ObjectHolder("drafting_station")
	public static Block draftingStation;
	@ObjectHolder("structure_builder_ticked")
	public static Block builderBlock;
	@ObjectHolder("sound_block")
	public static Block soundBlock;
}
