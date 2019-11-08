package net.shadowmage.ancientwarfare.automation.tile.worksite.treefarm;

import com.google.common.collect.AbstractIterator;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class DefaultTreeScanner implements ITreeScanner {
	private final Predicate<IBlockState> trunkMatcher;
	private final Set<Predicate<IBlockState>> leafMatchers = new HashSet<>();
	private int maxLeafDistance;
	private INextPositionGetter nextPositionGetter;

	private static final int MAX_TRUNK_DISTANCE = 1;

	public Predicate<IBlockState> getTrunkMatcher() {
		return trunkMatcher;
	}

	public void addLeafMatcher(Predicate<IBlockState> leafMatcher) {
		leafMatchers.add(leafMatcher);
	}

	public DefaultTreeScanner(Predicate<IBlockState> trunkMatcher, Predicate<IBlockState> leafMatcher) {
		this(trunkMatcher, leafMatcher, CONNECTED_UP_OR_LEVEL, 5);
	}

	public DefaultTreeScanner(Predicate<IBlockState> trunkMatcher, Predicate<IBlockState> leafMatcher, INextPositionGetter nextPosGetter, int maxLeafDistance) {
		this.trunkMatcher = trunkMatcher;
		leafMatchers.add(leafMatcher);
		this.maxLeafDistance = maxLeafDistance;
		nextPositionGetter = nextPosGetter;
	}

	@Override
	public ITree scanTree(World world, BlockPos pos, int maxDistanceToInitial) {
		HorizontalAABB trunkBounds = new HorizontalAABB(pos);

		if (!isTrunk(world.getBlockState(pos))) {
			return Tree.EMPTY;
		}

		List<BlockPos> openList = new ArrayList<>();
		Set<BlockPos> alreadyScanned = new HashSet<>();
		openList.add(pos);
		alreadyScanned.add(pos);

		Tree tree = new Tree(pos);
		while (!openList.isEmpty()) {
			BlockPos current = openList.remove(0);
			Set<BlockPos> toScan = nextPositionGetter.getPositionsToScan(current).filter(p -> !alreadyScanned.contains(p))
					.collect(Collectors.toCollection(LinkedHashSet::new));
			openList.addAll(addTreeBlocks(toScan, world, tree, trunkBounds, pos, maxDistanceToInitial));
			alreadyScanned.addAll(toScan);
		}

		return tree;
	}

	private Collection<? extends BlockPos> addTreeBlocks(Set<BlockPos> toScan, World world, Tree tree, HorizontalAABB trunkBounds, BlockPos initialPos, int maxDistanceToInitial) {
		Set<BlockPos> treeBlocks = new HashSet<>();

		for (BlockPos pos : toScan) {
			if (horizontalDistance(pos, initialPos) > maxDistanceToInitial) {
				continue;
			}
			IBlockState state = world.getBlockState(pos);
			if (isTrunk(state)) {
				tree.addTrunkPosition(pos);
				treeBlocks.add(pos);
				if (trunkBounds.distanceTo(pos) <= MAX_TRUNK_DISTANCE) {
					trunkBounds.include(pos);
				}
			} else if (isLeaf(state) && trunkBounds.distanceTo(pos) <= maxLeafDistance) {
				tree.addLeafPosition(pos);
				treeBlocks.add(pos);
			}
		}

		return treeBlocks;
	}

	private int horizontalDistance(BlockPos pos, BlockPos initialPos) {
		int xDifference = Math.abs(pos.getX() - initialPos.getX());
		int zDifference = Math.abs(pos.getZ() - initialPos.getZ());
		return (int) Math.sqrt(xDifference * xDifference + zDifference * zDifference);
	}

	private boolean isLeaf(IBlockState state) {
		return leafMatchers.stream().anyMatch(m -> m.test(state));
	}

	private boolean isTrunk(IBlockState state) {
		return trunkMatcher.test(state);
	}

	private static Iterable<BlockPos> getPositionsInBoxOrderedByY(BlockPos corner1, BlockPos corner2) {
		return getPositionsInBoxOrderedByY(Math.min(corner1.getX(), corner2.getX()), Math.min(corner1.getY(), corner2.getY()),
				Math.min(corner1.getZ(), corner2.getZ()), Math.max(corner1.getX(), corner2.getX()), Math.max(corner1.getY(), corner2.getY()),
				Math.max(corner1.getZ(), corner2.getZ()));
	}

	private static Iterable<BlockPos> getPositionsInBoxOrderedByY(int x1, int y1, int z1, int x2, int y2, int z2) {
		return () -> new AbstractIterator<BlockPos>() {
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

	@Override
	public boolean matches(IBlockState state) {
		return trunkMatcher.test(state);
	}

	public interface INextPositionGetter {
		Stream<BlockPos> getPositionsToScan(BlockPos currentPos);
	}

	public static final INextPositionGetter ALL_AROUND = currentPos -> {
		Iterable<BlockPos> blocksInBox = getPositionsInBoxOrderedByY(currentPos.add(-1, -1, -1), currentPos.add(1, 1, 1));
		return StreamSupport.stream(blocksInBox.spliterator(), false);
	};

	public static final INextPositionGetter CONNECTED_AROUND = currentPos -> Arrays.stream(EnumFacing.VALUES).map(currentPos::offset);

	public static final INextPositionGetter CONNECTED_UP_OR_LEVEL = new INextPositionGetter() {
		private final EnumFacing[] offsets = new EnumFacing[] {EnumFacing.NORTH, EnumFacing.EAST, EnumFacing.SOUTH, EnumFacing.WEST, EnumFacing.UP};

		@Override
		public Stream<BlockPos> getPositionsToScan(BlockPos currentPos) {
			return Arrays.stream(offsets).map(currentPos::offset);
		}
	};

	public static final INextPositionGetter CONNECTED_DOWN_OR_LEVEL = new INextPositionGetter() {
		private final EnumFacing[] offsets = new EnumFacing[] {EnumFacing.NORTH, EnumFacing.EAST, EnumFacing.SOUTH, EnumFacing.WEST, EnumFacing.DOWN};

		@Override
		public Stream<BlockPos> getPositionsToScan(BlockPos currentPos) {
			return Arrays.stream(offsets).map(currentPos::offset);
		}
	};

	public static final INextPositionGetter ALL_UP_OR_LEVEL = currentPos -> {
		Iterable<BlockPos> blocksInBox = getPositionsInBoxOrderedByY(currentPos.add(-1, 0, -1), currentPos.add(1, 1, 1));
		return StreamSupport.stream(blocksInBox.spliterator(), false);
	};
}
