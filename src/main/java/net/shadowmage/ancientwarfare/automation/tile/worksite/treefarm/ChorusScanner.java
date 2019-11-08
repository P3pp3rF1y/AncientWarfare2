package net.shadowmage.ancientwarfare.automation.tile.worksite.treefarm;

import net.minecraft.block.Block;
import net.minecraft.block.BlockChorusFlower;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ChorusScanner implements ITreeScanner {
	@Override
	public ITree scanTree(World world, BlockPos pos, int maxDistanceToInitial) {
		Branch branch = new Branch();
		Set<BlockPos> scannedPositions = new HashSet<>();
		scanBranchHarvestableBlocks(scannedPositions, branch, world, pos, EnumFacing.DOWN);
		return branch;
	}

	private void scanBranchHarvestableBlocks(Set<BlockPos> scannedPositions, Branch parentBranch, World world, BlockPos startPos, EnumFacing avoidDirection) {
		if (!scannedPositions.add(startPos)) {
			return;
		}

		boolean continueSearch;
		BlockPos currentPos = startPos;
		EnumFacing avoidNext = avoidDirection;

		Branch childBranch = new Branch();
		parentBranch.addChildBranch(childBranch);

		do {
			continueSearch = false;

			childBranch.addTrunkPos(currentPos);

			IBlockState state = world.getBlockState(currentPos);
			if (state.getBlock() == Blocks.CHORUS_FLOWER) {
				if (state.getValue(BlockChorusFlower.AGE) == 5) {
					childBranch.setMature();
				}
				return;
			}

			List<EnumFacing> connectedSides = getConnectedSides(avoidNext, world, currentPos);

			if (connectedSides.size() == 1) {
				continueSearch = true;
				EnumFacing nextFacing = connectedSides.get(0);
				currentPos = currentPos.offset(nextFacing);
				if (!scannedPositions.add(currentPos)) {
					return;
				}

				avoidNext = nextFacing.getOpposite();
			} else if (connectedSides.size() > 1) {
				//multiple branches attached
				scanConnectedBranchsBlocks(scannedPositions, childBranch, world, currentPos, connectedSides);
				childBranch.updateMature();
				return;
			}
		} while (continueSearch);

		//there's no chorus flower at the end of this branch so let's harvest it
		childBranch.setMature();
	}

	private void scanConnectedBranchsBlocks(Set<BlockPos> scannedPositions, Branch parentBranch, World world, BlockPos currentPos, List<EnumFacing> connectedSides) {
		for (EnumFacing side : connectedSides) {
			scanBranchHarvestableBlocks(scannedPositions, parentBranch, world, currentPos.offset(side), side.getOpposite());
		}
	}

	private List<EnumFacing> getConnectedSides(EnumFacing avoidDirection, World world, BlockPos pos) {
		List<EnumFacing> connectedSides = new ArrayList<>();
		for (EnumFacing facing : EnumFacing.VALUES) {
			if (facing != EnumFacing.DOWN && facing != avoidDirection && plantIsConnectedOnSide(world, pos, facing)) {
				connectedSides.add(facing);
			}
		}
		return connectedSides;
	}

	private boolean plantIsConnectedOnSide(World world, BlockPos pos, EnumFacing side) {
		Block block = world.getBlockState(pos.offset(side)).getBlock();

		return block == Blocks.CHORUS_PLANT || block == Blocks.CHORUS_FLOWER;
	}

	@Override
	public boolean matches(IBlockState state) {
		return state.getBlock() == Blocks.CHORUS_PLANT;
	}
}
