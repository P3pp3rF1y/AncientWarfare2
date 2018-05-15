package net.shadowmage.ancientwarfare.automation.tile.worksite.treefarm;

import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class Tree implements ITree {
	public static final Tree EMPTY = new Tree();
	private final List<BlockPos> trunkPositions = new ArrayList<>();
	private final List<BlockPos> leafPositions = new ArrayList<>();

	private Tree() {
	}

	public Tree(BlockPos initialTrunkPos) {
		trunkPositions.add(initialTrunkPos);
	}

	public void addTrunkPosition(BlockPos trunkPosition) {
		trunkPositions.add(0, trunkPosition);
	}

	public void addLeafPosition(BlockPos leafPosition) {
		leafPositions.add(0, leafPosition);
	}

	@Override
	public List<BlockPos> getTrunkPositions() {
		return trunkPositions;
	}

	@Override
	public List<BlockPos> getLeafPositions() {
		return leafPositions;
	}
}
