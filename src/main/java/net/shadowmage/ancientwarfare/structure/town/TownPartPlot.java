package net.shadowmage.ancientwarfare.structure.town;

import net.minecraft.util.math.BlockPos;
import net.shadowmage.ancientwarfare.structure.template.build.StructureBB;

public class TownPartPlot {

	int x, z;//indices in array for that block
	private int minX, minZ, maxX, maxZ;

	TownPartBlock block;//the owning block
	StructureBB bb;//bb of the plot
	boolean[] roadBorders;//what directions are adjacent to a road, can be 0-2 total sides (0=center plot, cannot have struct, can only merge with other plots or be 'cosmetic' structs)
	boolean closed;//has been used or not (if true, plot has been used by a structure)
	boolean skipped;//has been marked for skip by empty plot setting

	public TownPartPlot(TownPartBlock block, StructureBB bb, int x, int z) {
		this.block = block;
		this.bb = bb;
		roadBorders = new boolean[4];
		this.x = x;
		this.z = z;
		reseatMinMax();
	}

	/*
	 * mark this plot and any merged plots as closed
	 */
	public void markClosed() {
		for (int x = minX; x <= maxX; x++) {
			for (int z = minZ; z <= maxZ; z++) {
				block.getPlot(x, z).closed = true;
			}
		}
	}

	public boolean hasRoadBorder() {
		for (int i = 0; i < 4; i++) {
			if (roadBorders[i]) {
				return true;
			}
		}
		return false;
	}

	private void reseatMinMax() {
		this.minX = x;
		this.minZ = z;
		this.maxX = x;
		this.maxZ = z;
	}

	/*
	 * Expands THIS plot to include the passed in plot.<br>
	 * The passed-in plot should be discarded as it is no longer valid
	 */
	public void merge(TownPartPlot other) {
		int x = bb.min.getX();
		int z = bb.min.getZ();
		if (other.bb.min.getX() < bb.min.getX()) {
			x = other.bb.min.getX();
		}
		if (other.bb.min.getZ() < bb.min.getZ()) {
			z = other.bb.min.getZ();
		}
		bb.min = new BlockPos(x, bb.min.getY(), z);
		x = bb.max.getX();
		z = bb.max.getZ();
		if (other.bb.max.getX() > bb.max.getX()) {
			x = other.bb.max.getX();
		}
		if (other.bb.max.getZ() > bb.max.getZ()) {
			z = other.bb.max.getZ();
		}
		bb.max = new BlockPos(x, bb.max.getY(), z);
		for (int i = 0; i < 4; i++) {
			if (other.roadBorders[i]) {
				this.roadBorders[i] = true;
			}
		}
	}

	public int getWidth() {
		return (bb.max.getX() - bb.min.getX()) + 1;
	}

	public int getLength() {
		return (bb.max.getZ() - bb.min.getZ()) + 1;
	}

	public boolean expand(int xSize, int zSize) {
		StructureBB bb = this.bb.copy();//will revert to this bb if expansion fails for any reason
		boolean val = tryExpand(xSize, zSize);
		if (!val)//no expansion...reset bb and min/max indices
		{
			reseatMinMax();
			this.bb = bb;
		}
		return val;
	}

	private boolean tryExpand(int xSize, int zSize) {
		while (getWidth() < xSize) {
			if (!expandEast() && !expandWest()) {
				return false;
			}
		}
		while (getLength() < zSize) {
			if (!expandNorth() && !expandSouth()) {
				return false;
			}
		}
		return true;
	}

	private boolean expandNorth() {
		if (minZ <= 0) {
			return false;
		}
		for (int x = minX; x <= maxX; x++) {
			if (block.getPlot(x, minZ - 1).closed) {
				return false;
			}
		}
		minZ--;
		TownPartPlot p = block.getPlot(x, minZ);
		this.bb.min = new BlockPos(this.bb.min.getX(), this.bb.min.getY(), p.bb.min.getZ());
		return true;
	}

	private boolean expandSouth() {
		if (maxZ + 1 >= block.plotsLength) {
			return false;
		}
		for (int x = minX; x <= maxX; x++) {
			if (block.getPlot(x, maxZ + 1).closed) {
				return false;
			}
		}
		maxZ++;
		TownPartPlot p = block.getPlot(x, maxZ);
		this.bb.max = new BlockPos(this.bb.max.getX(), this.bb.max.getY(), p.bb.max.getZ());
		return true;
	}

	private boolean expandEast() {
		if (minX <= 0) {
			return false;
		}
		for (int z = minZ; z <= maxZ; z++) {
			if (block.getPlot(minX - 1, z).closed) {
				return false;
			}
		}
		minX--;
		TownPartPlot p = block.getPlot(minX, z);
		this.bb.min = new BlockPos(p.bb.min.getX(), this.bb.min.getY(), this.bb.min.getZ());
		return true;
	}

	private boolean expandWest() {
		if (maxX + 1 >= block.plotsWidth) {
			return false;
		}
		for (int z = minZ; z <= maxZ; z++) {
			if (block.getPlot(maxX + 1, z).closed) {
				return false;
			}
		}
		maxX++;
		TownPartPlot p = block.getPlot(maxX, z);
		this.bb.max = new BlockPos(p.bb.max.getX(), this.bb.max.getY(), this.bb.max.getZ());
		return true;
	}

}
