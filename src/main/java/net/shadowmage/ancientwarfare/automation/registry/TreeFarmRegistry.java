package net.shadowmage.ancientwarfare.automation.registry;

import net.minecraft.block.Block;
import net.minecraft.block.BlockMushroom;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.shadowmage.ancientwarfare.automation.tile.worksite.treefarm.ITree;
import net.shadowmage.ancientwarfare.automation.tile.worksite.treefarm.TreeDefault;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

public class TreeFarmRegistry {
	private static Set<Predicate<Block>> plantables = new HashSet<>();

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
}
