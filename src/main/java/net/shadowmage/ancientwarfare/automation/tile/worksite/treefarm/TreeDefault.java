package net.shadowmage.ancientwarfare.automation.tile.worksite.treefarm;

import com.google.common.collect.AbstractIterator;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;
import net.shadowmage.ancientwarfare.automation.block.AWAutomationBlocks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class TreeDefault implements ITree {
	private boolean searchDone = false;

	private List<BlockPos> trunkBlocks = new ArrayList<>();
	private List<BlockPos> leafBlocks = new ArrayList<>();

	@Override
	public List<BlockPos> getTrunkBlocks(IBlockState blockType, World world, BlockPos pos) {
		searchTreeBlocks(pos, world);
		return trunkBlocks;
	}

	private void searchTreeBlocks(BlockPos pos, World world) {
		if (searchDone) {
			return;
		}
		searchDone = true;

		List<BlockPos> openList = new ArrayList<>();
		Set<BlockPos> alreadyScanned = new HashSet<>();
		openList.add(pos);
		alreadyScanned.add(pos);
		trunkBlocks.add(pos);
		while (!openList.isEmpty()) {
			BlockPos current = openList.remove(0);
			Set<BlockPos> toScan = getPositionsToScan(current, alreadyScanned);
			openList.addAll(addTreeBlocks(toScan, world));
			alreadyScanned.addAll(toScan);
		}

	}

	private Collection<? extends BlockPos> addTreeBlocks(Set<BlockPos> toScan, World world) {
		Set<BlockPos> treeBlocks = new HashSet<>();

		for (BlockPos pos : toScan) {
			IBlockState state = world.getBlockState(pos);
			if (isTrunk(state)) {
				trunkBlocks.add(0, pos);
				treeBlocks.add(pos);
			} else if (isLeaf(state)) {
				leafBlocks.add(0, pos);
				treeBlocks.add(pos);
			}
		}

		return treeBlocks;
	}

	private boolean isLeaf(IBlockState state) {
		return state.getBlock() instanceof IShearable;
	}

	private boolean isTrunk(IBlockState state) {
		return state.getMaterial() == Material.WOOD && state.getBlock() != AWAutomationBlocks.worksiteTreeFarm;
	}

	private Set<BlockPos> getPositionsToScan(BlockPos currentPos, Set<BlockPos> alreadyScanned) {
		Iterable<BlockPos> blocksInBox = getPositionsInBoxOrderedByY(currentPos.add(-1, -1, -1), currentPos.add(1, 1, 1));
		return StreamSupport.stream(blocksInBox.spliterator(), false).filter(bp -> !alreadyScanned.contains(bp))
				.collect(Collectors.toCollection(LinkedHashSet::new));
	}

	private Iterable<BlockPos> getPositionsInBoxOrderedByY(BlockPos corner1, BlockPos corner2) {
		return getPositionsInBoxOrderedByY(Math.min(corner1.getX(), corner2.getX()), Math.min(corner1.getY(), corner2.getY()),
				Math.min(corner1.getZ(), corner2.getZ()), Math.max(corner1.getX(), corner2.getX()), Math.max(corner1.getY(), corner2.getY()),
				Math.max(corner1.getZ(), corner2.getZ()));
	}

	private Iterable<BlockPos> getPositionsInBoxOrderedByY(int x1, int y1, int z1, int x2, int y2, int z2) {
		return new Iterable<BlockPos>() {
			public Iterator<BlockPos> iterator() {
				return new AbstractIterator<BlockPos>() {
					private boolean first = true;
					private int lastPosX;
					private int lastPosY;
					private int lastPosZ;

					protected BlockPos computeNext() {
						if (this.first) {
							this.first = false;
							this.lastPosX = x1;
							this.lastPosY = y1;
							this.lastPosZ = z1;
							return new BlockPos(x1, y1, z1);
						} else if (this.lastPosX == x2 && this.lastPosY == y2 && this.lastPosZ == z2) {
							return this.endOfData();
						} else {
							if (this.lastPosX < x2) {
								++this.lastPosX;
							} else if (this.lastPosZ < z2) {
								this.lastPosX = x1;
								++this.lastPosZ;
							} else if (this.lastPosY < y2) {
								this.lastPosX = x1;
								this.lastPosZ = z1;
								++this.lastPosY;
							}

							return new BlockPos(this.lastPosX, this.lastPosY, this.lastPosZ);
						}
					}
				};
			}
		};
	}

	@Override
	public List<BlockPos> getLeafBlocks(IBlockState blockType, World world, BlockPos pos) {
		searchTreeBlocks(pos, world);
		return leafBlocks;
	}

	@Override
	public boolean bottomBlockMatches(IBlockState state) {
		return state.getMaterial() == Material.WOOD;
	}
}
