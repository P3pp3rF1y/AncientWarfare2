package net.shadowmage.ancientwarfare.core.datafixes;

import net.minecraft.util.datafix.FixTypes;
import net.minecraftforge.common.util.CompoundDataFixer;
import net.minecraftforge.common.util.ModFixs;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.shadowmage.ancientwarfare.automation.datafixes.ItemMapDataWalker;
import net.shadowmage.ancientwarfare.automation.tile.warehouse2.TileWarehouseStorage;
import net.shadowmage.ancientwarfare.automation.tile.warehouse2.TileWarehouseStorageLarge;
import net.shadowmage.ancientwarfare.automation.tile.warehouse2.TileWarehouseStorageMedium;
import net.shadowmage.ancientwarfare.npc.datafixes.FactionEntityFixer;
import net.shadowmage.ancientwarfare.npc.datafixes.FactionExpansionEntityFixer;
import net.shadowmage.ancientwarfare.npc.datafixes.FactionExpansionItemFixer;
import net.shadowmage.ancientwarfare.npc.datafixes.FactionSpawnerItemFixer;
import net.shadowmage.ancientwarfare.npc.datafixes.FoodBundleDataFixer;
import net.shadowmage.ancientwarfare.npc.datafixes.NpcSkinFixer;
import net.shadowmage.ancientwarfare.npc.datafixes.RoutingOrderFilterCountsFixer;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;
import net.shadowmage.ancientwarfare.npc.init.AWNPCEntities;
import net.shadowmage.ancientwarfare.structure.datafixes.LootSettingsPotionRegistryNameFixer;
import net.shadowmage.ancientwarfare.structure.datafixes.TileLootFixer;

import static net.shadowmage.ancientwarfare.core.AncientWarfareCore.MOD_ID;

public class AWDataFixes {
	private AWDataFixes() {}

	private static final int DATA_FIXER_VERSION = 10;

	public static void registerDataFixes() {
		CompoundDataFixer dataFixer = FMLCommonHandler.instance().getDataFixer();
		AWNPCEntities.getNpcMap().values().forEach(npc -> NpcBase.registerFixesNpc(dataFixer, npc.getEntityClass()));
		ModFixs fixes = dataFixer.init(MOD_ID, DATA_FIXER_VERSION);
		fixes.registerFix(FixTypes.ENTITY, new VehicleOwnerFixer());
		fixes.registerFix(FixTypes.BLOCK_ENTITY, new TileOwnerFixer());
		fixes.registerFix(FixTypes.BLOCK_ENTITY, new TileIdFixer());
		fixes.registerFix(FixTypes.ENTITY, new FactionEntityFixer());
		fixes.registerFix(FixTypes.ITEM_INSTANCE, new FactionSpawnerItemFixer());
		fixes.registerFix(FixTypes.ITEM_INSTANCE, new ResearchNoteFixer());
		fixes.registerFix(FixTypes.ENTITY, new FactionExpansionEntityFixer());
		fixes.registerFix(FixTypes.ITEM_INSTANCE, new FactionExpansionItemFixer());
		fixes.registerFix(FixTypes.ITEM_INSTANCE, new RoutingOrderFilterCountsFixer());
		fixes.registerFix(FixTypes.ITEM_INSTANCE, new RoutingOrderFilterCountsFixer());
		fixes.registerFix(FixTypes.ITEM_INSTANCE, new FoodBundleDataFixer());
		fixes.registerFix(FixTypes.BLOCK_ENTITY, new TileLootFixer());
		fixes.registerFix(FixTypes.ENTITY, new NpcSkinFixer());
		dataFixer.registerWalker(FixTypes.BLOCK_ENTITY, new ItemMapDataWalker(
				new Class<?>[] {TileWarehouseStorage.class, TileWarehouseStorageMedium.class, TileWarehouseStorageLarge.class},
				"inventory/itemMap/entryList"));
		fixes.registerFix(FixTypes.BLOCK_ENTITY, new LootSettingsPotionRegistryNameFixer());
	}
}
