package net.shadowmage.ancientwarfare.structure.town;

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
        int x = bb.min.x;
        int z = bb.min.z;
        if (other.bb.min.x < bb.min.x) {
            x = other.bb.min.x;
        }
        if (other.bb.min.z < bb.min.z) {
            z = other.bb.min.z;
        }
        bb.min = new BlockPos(x, bb.min.y, z);
        x = bb.max.x;
        z = bb.max.z;
        if (other.bb.max.x > bb.max.x) {
            x = other.bb.max.x;
        }
        if (other.bb.max.z > bb.max.z) {
            z = other.bb.max.z;
        }
        bb.max = new BlockPos(x, bb.max.y, z);
        for (int i = 0; i < 4; i++) {
            if (other.roadBorders[i]) {
                this.roadBorders[i] = true;
            }
        }
    }

    public int getWidth() {
        return (bb.max.x - bb.min.x) + 1;
    }

    public int getLength() {
        return (bb.max.z - bb.min.z) + 1;
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
        this.bb.min = new BlockPos(this.bb.min.x, this.bb.min.y, p.bb.min.z);
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
        this.bb.max = new BlockPos(this.bb.max.x, this.bb.max.y, p.bb.max.z);
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
        this.bb.min = new BlockPos(p.bb.min.x, this.bb.min.y, this.bb.min.z);
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
        this.bb.max = new BlockPos(p.bb.max.x, this.bb.max.y, this.bb.max.z);
        return true;
    }

}
