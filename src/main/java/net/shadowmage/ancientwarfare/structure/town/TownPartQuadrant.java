package net.shadowmage.ancientwarfare.structure.town;

import net.minecraft.util.math.BlockPos;
import net.shadowmage.ancientwarfare.core.util.Trig;
import net.shadowmage.ancientwarfare.structure.template.build.StructureBB;

import java.util.Collections;
import java.util.List;

public class TownPartQuadrant {

    protected StructureBB bb;
    public final TownGenerator gen;
    private Direction xDir, zDir;
    protected int xDivs, zDivs;
    private boolean roadBorders[];
    protected TownPartBlock blocks[];

    public TownPartQuadrant(Direction xDir, Direction zDir, StructureBB bb, boolean[] borders, TownGenerator gen) {
        this.xDir = xDir;
        this.zDir = zDir;
        this.bb = bb;
        this.roadBorders = borders;
        this.gen = gen;
    }

    public boolean hasRoadBorder(Direction d) {
        return roadBorders[d.ordinal()];
    }

    protected void setRoadBorder(Direction d, boolean val) {
        roadBorders[d.ordinal()] = val;
    }

    public void subdivide(int blockSize, int plotSize, boolean gridRoads) {
        int totalWidth = (bb.max.getX() - bb.min.getX());
        int totalLength = (bb.max.getZ() - bb.min.getZ());
        int widthToUse = totalWidth;
        int lengthToUse = totalLength;

        int y1 = gen.maximalBounds.min.getY();
        int y2 = gen.maximalBounds.max.getY();

        widthToUse--;//the forced road edge for first block
        lengthToUse--;//the forced road edge for first block
        while (widthToUse > 0) {
            widthToUse -= blockSize;
            widthToUse -= 2;//end edge of block + front edge of next block
            xDivs++;
        }
        while (lengthToUse > 0) {
            lengthToUse -= blockSize;
            lengthToUse -= 2;
            zDivs++;
        }

        blocks = new TownPartBlock[xDivs * zDivs];
        int xStart, xEnd;
        int zStart, zEnd;
        int xSize, zSize;
        int xIndex, zIndex;
        TownPartBlock block;
        float distFromTownCenter = 0;
        StructureBB sbb;
        boolean[] borders;

        widthToUse = totalWidth;
        xStart = xDir.xDirection < 0 ? bb.max.getX() - 1 : bb.min.getX() + 1;
        for (int x = 0; x < xDivs; x++) {
            xSize = widthToUse > blockSize ? blockSize : widthToUse;
            xEnd = xStart + xDir.xDirection * (xSize - 1);
            xIndex = xDir == Direction.WEST ? (xDivs - 1) - x : x;

            zStart = zDir.zDirection < 0 ? bb.max.getZ() - 1 : bb.min.getZ() + 1;
            lengthToUse = (bb.max.getZ() - bb.min.getZ());
            for (int z = 0; z < zDivs; z++) {
                zSize = lengthToUse > blockSize ? blockSize : lengthToUse;
                zEnd = zStart + zDir.zDirection * (zSize - 1);
                zIndex = zDir == Direction.NORTH ? (zDivs - 1) - z : z;

                sbb = new StructureBB(new BlockPos(xStart, y1, zStart), new BlockPos(xEnd, y2, zEnd));
                borders = gridRoads ? getBordersGrid(xIndex, zIndex) : getBordersExterior(x, z);
                distFromTownCenter = Trig.getDistance(sbb.getCenterX(), y1, sbb.getCenterZ(), gen.maximalBounds.getCenterX(), y1, gen.maximalBounds.getCenterZ());
                block = new TownPartBlock(this, sbb, xIndex, zIndex, borders, distFromTownCenter);

                setBlock(block, xIndex, zIndex);
                block.subdivide(plotSize);

                lengthToUse -= (blockSize + 2);
                zStart = zEnd + zDir.zDirection * 3;
            }

            widthToUse -= (blockSize + 2);
            xStart = xEnd + xDir.xDirection * 3;
        }
    }

    private void setBlock(TownPartBlock tb, int x, int z) {
        blocks[getIndex(x, z)] = tb;
    }

    protected TownPartBlock getBlock(int x, int z) {
        return blocks[getIndex(x, z)];
    }

    private int getIndex(int x, int z) {
        return z * xDivs + x;
    }

    private boolean[] getBordersGrid(int x, int z) {
        boolean[] borders = new boolean[4];
        if (zDir == Direction.NORTH) {
            borders[Direction.SOUTH.ordinal()] = true;//has south
            borders[Direction.NORTH.ordinal()] = z > 0;//not on northern edge
        } else//zDir==Direction.SOUTH
        {
            borders[Direction.NORTH.ordinal()] = true;//has south
            borders[Direction.SOUTH.ordinal()] = z < zDivs - 1;//not on souther edge
        }
        if (xDir == Direction.WEST) {
            borders[Direction.EAST.ordinal()] = true;//has east
            borders[Direction.WEST.ordinal()] = x > 0;
        } else {
            borders[Direction.WEST.ordinal()] = true;
            borders[Direction.EAST.ordinal()] = x < xDivs - 1;
        }
        return borders;
    }

    private boolean[] getBordersExterior(int x, int z) {
        boolean[] borders = new boolean[4];
        borders[Direction.WEST.ordinal()] = roadBorders[Direction.WEST.ordinal()] && x == 0;
        borders[Direction.EAST.ordinal()] = roadBorders[Direction.EAST.ordinal()] && x == 0;
        borders[Direction.NORTH.ordinal()] = roadBorders[Direction.NORTH.ordinal()] && z == 0;
        borders[Direction.SOUTH.ordinal()] = roadBorders[Direction.SOUTH.ordinal()] && z == 0;
        return borders;
    }

    public void addBlocks(List<TownPartBlock> blocks) {
        Collections.addAll(blocks, this.blocks);
    }

    public Direction getXDir() {
        return xDir;
    }

    public Direction getZDir() {
        return zDir;
    }

}
