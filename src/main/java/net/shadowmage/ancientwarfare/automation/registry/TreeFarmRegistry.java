package net.shadowmage.ancientwarfare.automation.registry;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.block.Block;
import net.minecraft.block.BlockMushroom;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JsonUtils;
import net.shadowmage.ancientwarfare.automation.block.AWAutomationBlocks;
import net.shadowmage.ancientwarfare.automation.tile.worksite.treefarm.DefaultTreeScanner;
import net.shadowmage.ancientwarfare.automation.tile.worksite.treefarm.ITreeScanner;
import net.shadowmage.ancientwarfare.core.registry.IRegistryDataParser;
import net.shadowmage.ancientwarfare.core.util.parsing.BlockStateMatcher;
import net.shadowmage.ancientwarfare.core.util.parsing.JsonHelper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public class TreeFarmRegistry {
	public static final ITreeScanner DEFAULT_TREE_SCANNER =
			new DefaultTreeScanner(st -> st.getMaterial() == Material.WOOD && st.getBlock() != AWAutomationBlocks.worksiteTreeFarm
					, sl -> sl.getMaterial() == Material.LEAVES);

	private static Set<Predicate<Block>> plantables = new HashSet<>();
	private static Set<BlockStateMatcher> soilBlocks = new HashSet<>();
	private static List<ITreeScanner> treeScanners = new ArrayList<>();

	static {
		plantables.add(b -> b instanceof BlockSapling);
		plantables.add(b -> b instanceof BlockMushroom);
	}

	private static void registerTreeScanner(ITreeScanner treeScanner) {
		treeScanners.add(0, treeScanner);
	}

	static {
		registerTreeScanner(DEFAULT_TREE_SCANNER);
	}

	public static ITreeScanner getTreeScanner(IBlockState state) {
		return treeScanners.stream().filter(ts -> ts.matches(state)).findFirst().orElse(DEFAULT_TREE_SCANNER);
	}

	public static boolean isPlantable(ItemStack stack) {
		return stack.getItem() instanceof ItemBlock && plantables.stream().anyMatch(p -> p.test(((ItemBlock) stack.getItem()).getBlock()));
	}

	public static boolean isPlantable(Block block) {
		return plantables.stream().anyMatch(p -> p.test(block));
	}

	public static boolean isSoil(IBlockState state) {
		return soilBlocks.stream().anyMatch(m -> m.test(state));
	}

	public static class SoilParser implements IRegistryDataParser {

		@Override
		public String getName() {
			return "tree_soil_blocks";
		}

		@Override
		public void parse(JsonObject json) {
			JsonArray plantables = JsonUtils.getJsonArray(json, "soils");

			for (JsonElement t : plantables) {
				JsonObject soil = JsonUtils.getJsonObject(t, "");
				soilBlocks.add(JsonHelper.getBlockStateMatcher(soil, "soil"));
			}
		}
	}

	public static class TreeScannerParser implements IRegistryDataParser {
		@Override
		public String getName() {
			return "tree_scanners";
		}

		@Override
		public void parse(JsonObject json) {
			JsonArray treeScanners = JsonUtils.getJsonArray(json, "tree_scanners");

			for (JsonElement ts : treeScanners) {
				JsonObject treeScanner = JsonUtils.getJsonObject(ts, "");
				switch (JsonUtils.getString(treeScanner, "type")) {
					case "default":
					default:
						DefaultSearchParser.parse(treeScanner);
				}
			}
		}

		private static class DefaultSearchParser {
			public static void parse(JsonObject treeScanner) {
				Predicate<IBlockState> trunkMatcher = JsonHelper.getBlockStateMatcher(treeScanner, "trunks", "trunk");
				Predicate<IBlockState> leafMatcher = JsonHelper.getBlockStateMatcher(treeScanner, "leaves", "leaf");

				int maxLeafDistance = JsonUtils.getInt(treeScanner, "max_leaf_distance");

				DefaultTreeScanner.INextPositionGetter nextPosGetter = parseNextPositionGetter(treeScanner);

				registerTreeScanner(new DefaultTreeScanner(trunkMatcher, leafMatcher, nextPosGetter, maxLeafDistance));
			}

			private static DefaultTreeScanner.INextPositionGetter parseNextPositionGetter(JsonObject treeScanner) {
				switch (JsonUtils.getString(treeScanner, "next_block_search")) {
					case "connected_up_or_level":
						return DefaultTreeScanner.CONNECTED_UP_OR_LEVEL;
					case "all_up_or_level":
						return DefaultTreeScanner.ALL_UP_OR_LEVEL;
					case "all_around":
						return DefaultTreeScanner.ALL_AROUND;
					case "connected_down_or_level":
						return DefaultTreeScanner.CONNECTED_DOWN_OR_LEVEL;
					default:
						return DefaultTreeScanner.CONNECTED_UP_OR_LEVEL;
				}
			}
		}
	}
}
