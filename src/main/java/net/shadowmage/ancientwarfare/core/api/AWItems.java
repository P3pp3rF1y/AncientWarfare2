package net.shadowmage.ancientwarfare.core.api;

import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.core.item.ItemInfoTool;
import net.shadowmage.ancientwarfare.core.item.ItemMulti;
import net.shadowmage.ancientwarfare.core.util.InjectionTools;
import net.shadowmage.ancientwarfare.structure.item.ItemGateSpawner;
import net.shadowmage.ancientwarfare.structure.item.ItemStructureScanner;

@ObjectHolder(AncientWarfareCore.MOD_ID)
@SuppressWarnings("squid:S1444")
public class AWItems {
	public static final ItemInfoTool INFO_TOOL = InjectionTools.nullValue();

	private AWItems() {}

	@ObjectHolder("research_book")
	public static Item researchBook;
	@ObjectHolder("iron_hammer")
	public static Item automationHammerIron;
	@ObjectHolder("backpack")
	public static Item backpack;
	@ObjectHolder("iron_quill")
	public static Item quillIron;
	@ObjectHolder("component")
	public static ItemMulti componentItem;
	@ObjectHolder("steel_ingot")
	public static Item steelIngot;

	/*
	 * AUTOMATION module
	 */
	public static ItemMulti worksiteUpgrade;

	/*
	 * STRUCTURE module
	 */
	@ObjectHolder("ancientwarfarestructure:gate_spawner")
	public static ItemGateSpawner gateSpawner;
	@ObjectHolder("ancientwarfarestructure:structure_scanner")
	public static ItemStructureScanner structureScanner;

}
