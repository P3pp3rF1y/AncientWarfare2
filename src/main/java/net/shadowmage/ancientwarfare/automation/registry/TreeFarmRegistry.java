package net.shadowmage.ancientwarfare.automation.registry;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.block.Block;
import net.minecraft.block.BlockMushroom;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JsonUtils;
import net.shadowmage.ancientwarfare.automation.tile.worksite.treefarm.ITree;
import net.shadowmage.ancientwarfare.automation.tile.worksite.treefarm.TreeDefault;
import net.shadowmage.ancientwarfare.core.registry.IRegistryDataParser;
import net.shadowmage.ancientwarfare.core.util.parsing.BlockStateMatcher;
import net.shadowmage.ancientwarfare.core.util.parsing.JsonHelper;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

public class TreeFarmRegistry {
	private static Set<Predicate<Block>> plantables = new HashSet<>();
	private static Set<BlockStateMatcher> soilBlocks = new HashSet<>();

	static {
		plantables.add(b -> b instanceof BlockSapling);
		plantables.add(b -> b instanceof BlockMushroom);
	}

	public static ITree getTree(IBlockState state) {
		return new TreeDefault();
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
}
