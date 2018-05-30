package net.shadowmage.ancientwarfare.automation.registry;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;
import net.shadowmage.ancientwarfare.automation.tile.worksite.fruitfarm.FruitBreakOnly;
import net.shadowmage.ancientwarfare.automation.tile.worksite.fruitfarm.FruitCocoa;
import net.shadowmage.ancientwarfare.automation.tile.worksite.fruitfarm.FruitPicked;
import net.shadowmage.ancientwarfare.automation.tile.worksite.fruitfarm.FruitPickedDrop;
import net.shadowmage.ancientwarfare.automation.tile.worksite.fruitfarm.FruitPickedRemoveOne;
import net.shadowmage.ancientwarfare.automation.tile.worksite.fruitfarm.IFruit;
import net.shadowmage.ancientwarfare.core.registry.IRegistryDataParser;
import net.shadowmage.ancientwarfare.core.util.TriFunction;
import net.shadowmage.ancientwarfare.core.util.parsing.BlockStateMatcher;
import net.shadowmage.ancientwarfare.core.util.parsing.JsonHelper;
import net.shadowmage.ancientwarfare.core.util.parsing.PropertyState;
import net.shadowmage.ancientwarfare.core.util.parsing.PropertyStateMatcher;

import java.util.HashSet;
import java.util.Set;

public class FruitFarmRegistry {
	private FruitFarmRegistry() {
	}

	private static final String FRUIT_ELEMENT = "fruit";

	private static final Set<IFruit> fruits = new HashSet<>();

	static {
		registerFruit(new FruitCocoa());
	}

	private static void registerFruit(IFruit pickable) {
		fruits.add(pickable);
	}

	public static IFruit getPickable(IBlockState state) {
		return state.getMaterial() == Material.AIR ? NO_FRUIT : fruits.stream().filter(p -> p.matches(state)).findFirst().orElse(NO_FRUIT);
	}

	public static boolean isPlantable(ItemStack stack) {
		return fruits.stream().anyMatch(p -> p.isPlantable() && p.matches(stack));
	}

	public static IFruit getPlantable(ItemStack stack) {
		return stack.isEmpty() ? NO_FRUIT : fruits.stream().filter(p -> p.isPlantable() && p.matches(stack)).findFirst().orElse(NO_FRUIT);
	}

	public static class FruitParser implements IRegistryDataParser {

		@Override
		public String getName() {
			return "fruit_blocks";
		}

		@Override
		public void parse(JsonObject json) {
			JsonArray fruits = JsonUtils.getJsonArray(json, "fruits");
			for (JsonElement e : fruits) {
				JsonObject fruit = JsonUtils.getJsonObject(e, "");
				parseFruit(fruit);
			}
		}

		private void parseFruit(JsonObject json) {
			String type = JsonUtils.getString(json, "type");
			switch (type) {
				case "picked_remove_one":
					PickedRemoveOne.parse(json);
					return;
				case "picked":
					Picked.parse(json);
					return;
				case "picked_drop":
					PickedDrop.parse(json);
					return;
				case "break_only":
					BreakOnly.parse(json);
					return;
				default:
			}
		}

		private static class BreakOnly {
			private BreakOnly() {
			}
			public static void parse(JsonObject json) {
				registerFruit(new FruitBreakOnly(JsonHelper.getBlockStateMatcher(json, FRUIT_ELEMENT)));
			}
		}

		private static class PickedDrop {
			private PickedDrop() {
			}
			public static void parse(JsonObject json) {
				registerFruit(Picked.parsePicked(json, (f, r, n) -> new FruitPickedDrop(f, r, n, JsonHelper.getItemStack(json, "drop"))));
			}
		}

		private static class PickedRemoveOne {
			private PickedRemoveOne() {
			}
			public static void parse(JsonObject json) {
				registerFruit(Picked.parsePicked(json, FruitPickedRemoveOne::new));
			}
		}

		private static class Picked {
			private Picked() {
			}
			public static void parse(JsonObject json) {
				registerFruit(parsePicked(json, FruitPicked::new));
			}

			private static IFruit parsePicked(JsonObject json, TriFunction<BlockStateMatcher, PropertyStateMatcher, PropertyState, IFruit> instantiate) {
				IBlockState fruitState = JsonHelper.getBlockState(json, FRUIT_ELEMENT);
				BlockStateMatcher stateMatcher = JsonHelper.getBlockStateMatcher(json, FRUIT_ELEMENT);
				PropertyStateMatcher ripeStateMatcher = JsonHelper.getPropertyStateMatcher(fruitState, json, "ripe");
				PropertyState newState = JsonHelper.getPropertyState(fruitState, json, "new");

				return instantiate.apply(stateMatcher, ripeStateMatcher, newState);
			}
		}
	}

	private static final IFruit NO_FRUIT = new IFruit() {
		@Override
		public boolean isRipe(IBlockState state) {
			return false;
		}

		@Override
		public boolean matches(IBlockState state) {
			return false;
		}

		@Override
		public boolean pick(World world, IBlockState state, BlockPos pos, int fortune, IItemHandler inventory) {
			return false;
		}

		@Override
		public boolean isPlantable() {
			return false;
		}

		@Override
		public boolean canPlant(World world, BlockPos currentPos, IBlockState state) {
			return false;
		}

		@Override
		public boolean plant(World world, BlockPos plantPos) {
			return false;
		}
	};
}
