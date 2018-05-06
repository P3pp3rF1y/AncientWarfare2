package net.shadowmage.ancientwarfare.automation.registry;

import com.google.gson.JsonObject;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;
import net.shadowmage.ancientwarfare.automation.tile.worksite.fruitfarm.CocoaFruit;
import net.shadowmage.ancientwarfare.automation.tile.worksite.fruitfarm.IFruit;
import net.shadowmage.ancientwarfare.core.registry.IRegistryDataParser;

import java.util.HashSet;
import java.util.Set;

public class FruitFarmRegistry {
	private static final Set<IFruit> fruits = new HashSet<>();

	static {
		registerFruit(new CocoaFruit());
	}

	public static void registerFruit(IFruit pickable) {
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

	public static class PickableParser implements IRegistryDataParser {

		@Override
		public String getName() {
			return "pickable_blocks";
		}

		@Override
		public void parse(JsonObject json) {

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
		public boolean pick(World world, IBlockState state, BlockPos pos, EntityPlayer player, int fortune, IItemHandler inventory) {
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
