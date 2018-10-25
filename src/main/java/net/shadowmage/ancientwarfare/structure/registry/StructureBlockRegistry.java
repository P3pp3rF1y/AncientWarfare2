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

	public static class Parser implements IRegistryDataParser {
		@Override
		public String getName() {
			return "structure_blocks";
		}

		@Override
		public void parse(JsonObject json) {
			STATE_TO_ITEM.putAll(JsonHelper.mapFromObjectArray(JsonUtils.getJsonArray(json, "blockstate_to_item"),
					"block", "item", JsonHelper::getBlockStateMatcher, JsonHelper::getItemStack));
			STATE_TO_REMAINING_ITEM.putAll(JsonHelper.mapFromObjectArray(JsonUtils.getJsonArray(json, "blockstate_to_item"),
					"block", "remaining_item", JsonHelper::getBlockStateMatcher, e -> e != null ? JsonHelper.getItemStack(e) : ItemStack.EMPTY));
		}
	}
}
