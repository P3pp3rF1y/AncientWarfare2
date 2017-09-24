package net.shadowmage.ancientwarfare.structure.town;

import net.minecraft.util.math.BlockPos;
import net.shadowmage.ancientwarfare.structure.template.build.StructureBB;

import java.util.ArrayList;
import java.util.List;

public class TownPartBlock {

    int x, z;//index of block in the quadrant
    float distFromTownCenter;
    private boolean[] roadBorders;
    TownPartQuadrant quadrant;
    StructureBB bb;
    List<TownPartPlot> plots;

    private TownPartPlot[] plotsArray;
    int plotsWidth, plotsLength;

    public TownPartBlock(TownPartQuadrant quadrant, StructureBB bb, int x, int z, boolean[] roadBorders, float dist) {
        this.quadrant = quadrant;
        this.bb = bb;
        this.x = x;
        this.z = z;
        plots = new ArrayList<>();
        this.roadBorders = roadBorders;
        distFromTownCenter = dist;
    }

    public boolean hasRoadBorder(Direction d) {
        return roadBorders[d.ordinal()];
    }

    protected void setRoadBorder(Direction d, boolean val) {
        roadBorders[d.ordinal()] = val;
    }

    public void subdivide(int plotSize) {
        int y1 = quadrant.gen.maximalBounds.min.getY();
        int y2 = quadrant.gen.maximalBounds.max.getY();
        int xWidth = (bb.max.getX() - bb.min.getX()) + 1;
        int zLength = (bb.max.getZ() - bb.min.getZ()) + 1;
        int xDivs, zDivs;
        xDivs = xWidth / plotSize;
        if (xWidth % plotSize != 0) {
            xDivs++;
        }
        zDivs = zLength / plotSize;
        if (zLength % plotSize != 0) {
            zDivs++;
        }
        plotsWidth = xDivs;
        plotsLength = zDivs;

        plotsArray = new TownPartPlot[xDivs * zDivs];
        int widthToUse, lengthToUse;
        int xStart, xEnd;
        int zStart, zEnd;
        int xSize, zSize;
        int xIndex, zIndex;

        TownPartPlot plot;

        xStart = quadrant.getXDir() == Direction.WEST ? bb.max.getX() : bb.min.getX();
        widthToUse = xWidth;
        for (int x = 0; x < xDivs; x++) {
            xSize = widthToUse > plotSize ? plotSize : widthToUse;
            xEnd = xStart + (xSize - 1) * quadrant.getXDir().xDirection;
            xIndex = quadrant.getXDir() == Direction.WEST ? (xDivs - 1) - x : x;

            zStart = quadrant.getZDir() == Direction.NORTH ? bb.max.getZ() : bb.min.getZ();
            lengthToUse = zLength;
            for (int z = 0; z < zDivs; z++) {
                zSize = lengthToUse > plotSize ? plotSize : lengthToUse;
                zEnd = zStart + quadrant.getZDir().zDirection * (zSize - 1);
                zIndex = quadrant.getZDir() == Direction.NORTH ? (zDivs - 1) - z : z;

                plot = new TownPartPlot(this, new StructureBB(new BlockPos(xStart, y1, zStart), new BlockPos(xEnd, y2, zEnd)), xIndex, zIndex);
                setRoadBorders(plot);

                plots.add(plot);
                setPlot(plot, xIndex, zIndex);

                lengthToUse -= plotSize;
                zStart = zEnd + quadrant.getZDir().zDirection;
            }

            widthToUse -= plotSize;
            xStart = xEnd + quadrant.getXDir().xDirection;
        }
    }

    private void setPlot(TownPartPlot plot, int x, int z) {
        plotsArray[getIndex(x, z)] = plot;
    }

    private int getIndex(int x, int z) {
        return z * plotsWidth + x;
    }

    public TownPartPlot getPlot(int x, int z) {
        if (x < 0 || z < 0 || x >= plotsWidth || z >= plotsLength) {
            return null;
        }
        return plotsArray[getIndex(x, z)];
    }

    private void setRoadBorders(TownPartPlot plot) {
        //check north side
        if (roadBorders[2] && plot.z == 0) {
            plot.roadBorders[2] = true;
        }
        //check south side
        if (roadBorders[0] && plot.z == plotsLength - 1) {
            plot.roadBorders[0] = true;
        }
        //check west side
        if (roadBorders[1] && plot.x == 0) {
            plot.roadBorders[1] = true;
        }
        //check east side
        if (roadBorders[3] && plot.x == plotsWidth - 1) {
            plot.roadBorders[3] = true;
        }
    }

}
