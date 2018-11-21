package net.shadowmage.ancientwarfare.structure.registry;

import com.google.gson.JsonObject;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JsonUtils;
import net.shadowmage.ancientwarfare.core.registry.IRegistryDataParser;
import net.shadowmage.ancientwarfare.core.util.parsing.BlockStateMatcher;
import net.shadowmage.ancientwarfare.core.util.parsing.JsonHelper;

import java.util.HashMap;
import java.util.Map;

public class StructureBlockRegistry {
	private StructureBlockRegistry() {}

	private static final Map<BlockStateMatcher, ItemStack> STATE_TO_ITEM = new HashMap<>();
	private static final Map<BlockStateMatcher, ItemStack> STATE_TO_REMAINING_ITEM = new HashMap<>();
	private static final Map<BlockStateMatcher, Integer> STATE_PASS = new HashMap<>();

	public static ItemStack getItemStackFrom(IBlockState state) {
		for (Map.Entry<BlockStateMatcher, ItemStack> stateItem : STATE_TO_ITEM.entrySet()) {
			if (stateItem.getKey().test(state)) {
				return stateItem.getValue();
			}
		}

		try {
			return state.getBlock().getItem(null, null, state);
		}
		catch (NullPointerException ex) {
			return new ItemStack(Items.AIR);
		}
	}

	public static ItemStack getRemainingStackFrom(IBlockState state) {
		for (Map.Entry<BlockStateMatcher, ItemStack> stateItem : STATE_TO_REMAINING_ITEM.entrySet()) {
			if (stateItem.getKey().test(state)) {
				return stateItem.getValue();
			}
		}
		return ItemStack.EMPTY;
	}

	public static int getBuildPass(IBlockState state) {
		for (Map.Entry<BlockStateMatcher, Integer> statePass : STATE_PASS.entrySet()) {
			if (statePass.getKey().test(state)) {
				return statePass.getValue();
			}
		}
		return 0;
	}

	public static class Parser implements IRegistryDataParser {

		public static final String BLOCK_PROPERTY = "block";

		@Override
		public String getName() {
			return "structure_blocks";
		}

		@Override
		public void parse(JsonObject json) {
			STATE_TO_ITEM.putAll(JsonHelper.mapFromObjectArray(JsonUtils.getJsonArray(json, "blockstate_to_item"),
					BLOCK_PROPERTY, "item", JsonHelper::getBlockStateMatcher, JsonHelper::getItemStack));
			STATE_TO_REMAINING_ITEM.putAll(JsonHelper.mapFromObjectArray(JsonUtils.getJsonArray(json, "blockstate_to_item"),
					BLOCK_PROPERTY, "remaining_item", JsonHelper::getBlockStateMatcher, e -> e != null ? JsonHelper.getItemStack(e) : ItemStack.EMPTY));
			STATE_PASS.putAll(JsonHelper.mapFromObjectArray(JsonUtils.getJsonArray(json, "block_passes"),
					BLOCK_PROPERTY, "build_pass", JsonHelper::getBlockStateMatcher, e -> Integer.parseInt(JsonUtils.getString(e, "build_pass"))));
		}
	}
}
