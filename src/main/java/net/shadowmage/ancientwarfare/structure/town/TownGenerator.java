package net.shadowmage.ancientwarfare.structure.town;

import net.minecraft.block.Block;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplate;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplateManager;
import net.shadowmage.ancientwarfare.structure.template.build.StructureBB;
import net.shadowmage.ancientwarfare.structure.town.TownTemplate.TownStructureEntry;
import net.shadowmage.ancientwarfare.structure.world_gen.WorldGenTickHandler;
import net.shadowmage.ancientwarfare.structure.world_gen.WorldGenTickHandler.StructureGenerationCallbackTicket;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Responsible for constructing the town -- leveling the area, placing the structures, constructing walls
 *
 * @author Shadowmage
 */
public class TownGenerator {

    public final TownTemplate template;
    public final World world;
    public final Random rng;
    public final int blockSize;
    public final int plotSize;
    public final StructureBB maximalBounds;
    public final StructureBB exteriorBounds;//maximal, shrunk by borderSize (16 blocks), maximal area encompassing extents of exterior buffer zone
    public final StructureBB wallsBounds;//exterior shrunk by exteriorSize (configurable), maximal area encompassing extents of walls
    public final StructureBB townBounds;//walls shrunk by wallSize (configurable), town generation area
    public final TownPartQuadrant[] quadrants = new TownPartQuadrant[4];
    public final TownPartQuadrant[] externalQuadrants = new TownPartQuadrant[8];//may be null refs if no exterior area is denoted
    public final List<StructureTemplate> uniqueTemplatesToGenerate = new ArrayList<StructureTemplate>();//depleted as used
    public final List<StructureTemplate> mainTemplatesToGenerate = new ArrayList<StructureTemplate>();//depleted as used
    public final List<StructureTemplate> houseTemplatesToGenerate = new ArrayList<StructureTemplate>();//weighted list
    public final List<StructureTemplate> cosmeticTemplatesToGenerate = new ArrayList<StructureTemplate>();//weighted list
    public final List<StructureTemplate> exteriorTemplatesToGenerate = new ArrayList<StructureTemplate>();//weighted list
    public final List<BlockPosition> structureDoors = new ArrayList<BlockPosition>();//list of all positions for generated doors.  used during lamp-post generation to not generate directly in front of a door

    public TownGenerator(World world, TownBoundingArea area, TownTemplate template) {
        this.world = world;
        this.template = template;
        long seed = (area.townCenterX << 32) | area.townCenterZ;
        this.rng = new Random(seed);

        int y1 = area.getSurfaceY() + 1;
        int y2 = y1 + 20;

        area.wallSize = template.getWallSize();
        area.exteriorSize = template.getExteriorSize();

        this.maximalBounds = new StructureBB(area.getBlockMinX(), y1, area.getBlockMinZ(), area.getBlockMaxX(), y2, area.getBlockMaxZ());
        this.exteriorBounds = new StructureBB(area.getExteriorMinX(), y1, area.getExteriorMinZ(), area.getExteriorMaxX(), y2, area.getExteriorMaxZ());
        this.wallsBounds = new StructureBB(area.getWallMinX(), y1, area.getWallMinZ(), area.getWallMaxX(), y2, area.getWallMaxZ());
        this.townBounds = new StructureBB(area.getTownMinX(), y1, area.getTownMinZ(), area.getTownMaxX(), y2, area.getTownMaxZ());

        this.blockSize = template.getTownBlockSize();
        this.plotSize = template.getTownPlotSize();
    }

    /**
     * Call this to initialize and start the generation of the town
     */
    public void generate() {
        AWLog.logDebug("Generating town at: " + townBounds.getCenterX() + " : " + townBounds.getCenterZ());
        determineStructuresToGenerate();
        TownGeneratorBorders.generateBorders(world, exteriorBounds, wallsBounds, maximalBounds);
        TownGeneratorBorders.levelTownArea(world, wallsBounds);

        generateGrid();
        TownGeneratorWalls.generateWalls(world, this, template, rng);
        WorldGenTickHandler.INSTANCE.addStructureGenCallback(new StructureGenerationCallbackTicket() {
            @Override
            public void call() {
                generateRoads();
                TownGeneratorStructures.generateStructures(TownGenerator.this);
            }
        });
    }

    public void generateVillagers() {
        float villagers = template.getRandomVillagersPerChunk();
        if (villagers > 0)//at least a chance to generate a villager per-chunk
        {
            int wholeVillagersPerChunk = 0;
            while (villagers > 1) {
                wholeVillagersPerChunk++;
                villagers--;
            }

            //now, for each chunk within town bounds, attempt to add wholeVillagers + partial chance for additional villager
            int x = townBounds.min.x;
            int y = townBounds.min.y;//surface height?
            int z = townBounds.min.z;

            for (int bx = x; bx < x + townBounds.getXSize(); bx += 16) {
                for (int bz = z; bz < z + townBounds.getZSize(); bz += 16) {
                    for (int i = 0; i < wholeVillagersPerChunk; i++) {
                        spawnVillager(bx, y, bz);
                    }
                    if (rng.nextFloat() < villagers) {
                        spawnVillager(bx, y, bz);
                    }
                }
            }
        }
    }

    private void spawnVillager(int minX, int y, int minZ) {
        EntityVillager villager = new EntityVillager(world);
        villager.onSpawnWithEgg(null);
        for (int i = 0; i < 10; i++) {
            int x = minX + rng.nextInt(16);
            int z = minZ + rng.nextInt(16);
            if (world.isAirBlock(x, y, z) && world.isAirBlock(x, y + 1, z) && world.isSideSolid(x, y - 1, z, ForgeDirection.UP)) {
                villager.setPosition(x + 0.5d, y, z + 0.5d);
                world.spawnEntityInWorld(villager);
                return;
            }
        }
    }

    /**
     * add initial generation entries to list of structures to attempt to generate
     */
    private void determineStructuresToGenerate() {
        int gen;
        for (TownStructureEntry e : template.getUniqueStructureEntries()) {
            StructureTemplate t = StructureTemplateManager.INSTANCE.getTemplate(e.templateName);
            if (t != null)
                uniqueTemplatesToGenerate.add(t);
        }

        for (TownStructureEntry e : template.getMainStructureEntries()) {
            StructureTemplate t = StructureTemplateManager.INSTANCE.getTemplate(e.templateName);
            if (t != null)
                mainTemplatesToGenerate.add(t);
        }

        for (TownStructureEntry e : template.getHouseStructureEntries()) {
            StructureTemplate t = StructureTemplateManager.INSTANCE.getTemplate(e.templateName);
            if (t == null) {
                continue;
            }
            gen = e.min;
            for (int i = 0; i < gen; i++) {
                houseTemplatesToGenerate.add(t);
            }
        }

        for (TownStructureEntry e : template.getCosmeticEntries()) {
            StructureTemplate t = StructureTemplateManager.INSTANCE.getTemplate(e.templateName);
            if (t == null) {
                continue;
            }
            gen = e.min;
            for (int i = 0; i < gen; i++) {
                this.cosmeticTemplatesToGenerate.add(t);
            }
        }

        for (TownStructureEntry e : template.getExteriorStructureEntries()) {
            StructureTemplate t = StructureTemplateManager.INSTANCE.getTemplate(e.templateName);
            if (t == null) {
                continue;
            }
            gen = e.min;
            for (int i = 0; i < gen; i++) {
                exteriorTemplatesToGenerate.add(t);
            }
        }
    }

    /**
     * Splits up the town into four quadrants<br>
     * quadrants into blocks<br>
     * and blocks into plots<br>
     */
    private void generateGrid() {
        final int centerX = maximalBounds.getCenterX();
        final int centerZ = maximalBounds.getCenterZ();
        final int y1 = townBounds.min.y;
        final int y2 = townBounds.max.y;

        StructureBB bb;
        TownPartQuadrant tq;
        boolean[] roadBorders;

        //northwest quadrant, pre-shrunk for road borders
        roadBorders = new boolean[]{true, false, false, true};
        bb = new StructureBB(new BlockPosition(townBounds.min.x, y1, townBounds.min.z), new BlockPosition(centerX - 2, y2, centerZ - 2));
        tq = new TownPartQuadrant(Direction.WEST, Direction.NORTH, bb, roadBorders, this);
        tq.subdivide(template.getTownBlockSize(), template.getTownPlotSize(), true);
        quadrants[0] = tq;

        //northeast quadrant
        roadBorders = new boolean[]{true, true, false, false};
        bb = new StructureBB(new BlockPosition(centerX + 1, y1, townBounds.min.z), new BlockPosition(townBounds.max.x, y2, centerZ - 2));
        tq = new TownPartQuadrant(Direction.EAST, Direction.NORTH, bb, roadBorders, this);
        tq.subdivide(template.getTownBlockSize(), template.getTownPlotSize(), true);
        quadrants[1] = tq;

        //southeast quadrant
        roadBorders = new boolean[]{false, true, true, false};
        bb = new StructureBB(new BlockPosition(centerX + 1, y1, centerZ + 1), new BlockPosition(townBounds.max.x, y2, townBounds.max.z));
        tq = new TownPartQuadrant(Direction.EAST, Direction.SOUTH, bb, roadBorders, this);
        tq.subdivide(template.getTownBlockSize(), template.getTownPlotSize(), true);
        quadrants[2] = tq;

        //southwest quadrant
        roadBorders = new boolean[]{false, false, true, true};
        bb = new StructureBB(new BlockPosition(townBounds.min.x, y1, centerZ + 1), new BlockPosition(centerX - 2, y2, townBounds.max.z));
        tq = new TownPartQuadrant(Direction.WEST, Direction.SOUTH, bb, roadBorders, this);
        tq.subdivide(template.getTownBlockSize(), template.getTownPlotSize(), true);
        quadrants[3] = tq;

        if (template.getExteriorSize() > 0) {
            generateExteriorGrid();
        }
    }

    /**
     * Splits any exterior buffer zone into 8 not-quite-quadrants, and those further down into blocks and plots
     */
    private void generateExteriorGrid() {
        StructureBB bb;
        boolean[] roadBorders;
        int centerX = maximalBounds.getCenterX();
        int centerZ = maximalBounds.getCenterZ();

        int minX, minY, minZ, maxX, maxY, maxZ;

        int eSize = template.getExteriorSize() * 16;
        int pSize = template.getTownPlotSize();

        minY = maximalBounds.min.y;
        maxY = maximalBounds.max.y;

        //1, northwest
        minX = exteriorBounds.min.x;
        minZ = exteriorBounds.min.z;
        maxX = centerX - 3;
        maxZ = wallsBounds.min.z - 1;
        roadBorders = new boolean[]{false, false, false, false};//
        bb = new StructureBB(new BlockPosition(minX, minY, minZ), new BlockPosition(maxX, maxY, maxZ));
        externalQuadrants[0] = new TownPartQuadrant(Direction.WEST, Direction.NORTH, bb, roadBorders, this);

        //2, northeast
        minX = centerX + 2;
        minZ = exteriorBounds.min.z;
        maxX = exteriorBounds.max.x;
        maxZ = wallsBounds.min.z - 1;
        bb = new StructureBB(new BlockPosition(minX, minY, minZ), new BlockPosition(maxX, maxY, maxZ));
        externalQuadrants[1] = new TownPartQuadrant(Direction.EAST, Direction.NORTH, bb, roadBorders, this);

        //3 west, north-part
        minX = exteriorBounds.min.x;
        minZ = wallsBounds.min.z;
        maxX = wallsBounds.min.x - 1;
        maxZ = centerZ - 3;
        bb = new StructureBB(new BlockPosition(minX, minY, minZ), new BlockPosition(maxX, maxY, maxZ));
        externalQuadrants[2] = new TownPartQuadrant(Direction.WEST, Direction.NORTH, bb, roadBorders, this);

        //4 east, north-part
        minX = wallsBounds.max.x + 1;
        minZ = wallsBounds.min.z;
        maxX = exteriorBounds.max.x;
        maxZ = centerZ - 3;
        bb = new StructureBB(new BlockPosition(minX, minY, minZ), new BlockPosition(maxX, maxY, maxZ));
        externalQuadrants[3] = new TownPartQuadrant(Direction.EAST, Direction.NORTH, bb, roadBorders, this);

        //5 west, south-part
        minX = exteriorBounds.min.x;
        minZ = centerZ + 2;
        maxX = wallsBounds.min.x - 1;
        maxZ = wallsBounds.max.z;
        bb = new StructureBB(new BlockPosition(minX, minY, minZ), new BlockPosition(maxX, maxY, maxZ));
        externalQuadrants[4] = new TownPartQuadrant(Direction.WEST, Direction.NORTH, bb, roadBorders, this);

        //6 east, south-part
        minX = wallsBounds.max.x + 1;
        minZ = centerZ + 2;
        maxX = exteriorBounds.max.x;
        maxZ = wallsBounds.max.z;
        bb = new StructureBB(new BlockPosition(minX, minY, minZ), new BlockPosition(maxX, maxY, maxZ));
        externalQuadrants[5] = new TownPartQuadrant(Direction.EAST, Direction.NORTH, bb, roadBorders, this);

        //7 southwest
        minX = exteriorBounds.min.x;
        minZ = wallsBounds.max.z + 1;
        maxX = centerX - 3;
        maxZ = exteriorBounds.max.z;
        bb = new StructureBB(new BlockPosition(minX, minY, minZ), new BlockPosition(maxX, maxY, maxZ));
        externalQuadrants[6] = new TownPartQuadrant(Direction.WEST, Direction.SOUTH, bb, roadBorders, this);

        //8 southeast
        minX = centerX + 2;
        minZ = wallsBounds.max.z + 1;
        maxX = exteriorBounds.max.x;
        maxZ = exteriorBounds.max.z;
        bb = new StructureBB(new BlockPosition(minX, minY, minZ), new BlockPosition(maxX, maxY, maxZ));
        externalQuadrants[7] = new TownPartQuadrant(Direction.EAST, Direction.SOUTH, bb, roadBorders, this);

        for (TownPartQuadrant tq1 : externalQuadrants) {
            tq1.subdivide(eSize, pSize, false);
        }
    }

    private void generateRoads() {
        for (TownPartQuadrant tq : quadrants) {
            generateRoads(tq);
        }
        generateAdditionalRoads();
    }

    /**
     * generates roads on the extent of a townquadrant
     */
    private void generateRoads(TownPartQuadrant tq) {
        int minX = tq.bb.min.x;
        int maxX = tq.bb.max.x;
        if (tq.hasRoadBorder(Direction.WEST)) {
            minX--;
        }
        if (tq.hasRoadBorder(Direction.EAST)) {
            maxX++;
        }
        for (int x = minX; x <= maxX; x++) {
            if (tq.hasRoadBorder(Direction.NORTH)) {
                genRoadBlock(x, tq.bb.min.y - 1, tq.bb.min.z - 1);
            }//north
            if (tq.hasRoadBorder(Direction.SOUTH)) {
                genRoadBlock(x, tq.bb.min.y - 1, tq.bb.max.z + 1);
            }//south
        }
        int minZ = tq.bb.min.z;
        int maxZ = tq.bb.max.z;
        if (tq.hasRoadBorder(Direction.NORTH)) {
            minZ--;
        }
        if (tq.hasRoadBorder(Direction.SOUTH)) {
            maxZ++;
        }
        for (int z = minZ; z <= maxZ; z++) {
            if (tq.hasRoadBorder(Direction.WEST)) {
                genRoadBlock(tq.bb.min.x - 1, tq.bb.min.y - 1, z);
            }//west
            if (tq.hasRoadBorder(Direction.EAST)) {
                genRoadBlock(tq.bb.max.x + 1, tq.bb.min.y - 1, z);
            }//east
        }
        for (TownPartBlock tb : tq.blocks) {
            generateRoads(tb);
        }
    }

    /**
     * Generates roads on the extends of a townblock
     */
    private void generateRoads(TownPartBlock tb) {
        int minX = tb.bb.min.x;
        int maxX = tb.bb.max.x;
        if (tb.hasRoadBorder(Direction.WEST)) {
            minX--;
        }
        if (tb.hasRoadBorder(Direction.EAST)) {
            maxX++;
        }
        for (int x = minX; x <= maxX; x++) {
            if (tb.hasRoadBorder(Direction.NORTH)) {
                genRoadBlock(x, tb.bb.min.y - 1, tb.bb.min.z - 1);
            }//north
            if (tb.hasRoadBorder(Direction.SOUTH)) {
                genRoadBlock(x, tb.bb.min.y - 1, tb.bb.max.z + 1);
            }//south
        }
        int minZ = tb.bb.min.z;
        int maxZ = tb.bb.max.z;
        if (tb.hasRoadBorder(Direction.NORTH)) {
            minZ--;
        }
        if (tb.hasRoadBorder(Direction.SOUTH)) {
            maxZ++;
        }
        for (int z = minZ; z <= maxZ; z++) {
            if (tb.hasRoadBorder(Direction.WEST)) {
                genRoadBlock(tb.bb.min.x - 1, tb.bb.min.y - 1, z);
            }//west
            if (tb.hasRoadBorder(Direction.EAST)) {
                genRoadBlock(tb.bb.max.x + 1, tb.bb.min.y - 1, z);
            }//east
        }
    }

    /**
     * generates roads running from the edges of the 'townBounds' to the edges of the 'wallBounds'
     */
    private void generateAdditionalRoads() {
        int minX, minZ, maxX, maxZ, y;
        y = maximalBounds.min.y - 1;

        //northern road
        minX = maximalBounds.getCenterX() - 2;
        maxX = minX + 3;
        minZ = exteriorBounds.min.z;
        maxZ = townBounds.min.z - 1;
        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                genRoadBlock(x, y, z);
            }
        }

        //eastern road
        minX = townBounds.max.x + 1;
        minZ = maximalBounds.getCenterZ() - 2;
        maxX = exteriorBounds.max.x;
        maxZ = minZ + 3;
        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                genRoadBlock(x, y, z);
            }
        }

        //southern road
        minX = maximalBounds.getCenterX() - 2;
        minZ = townBounds.max.z + 1;
        maxX = minX + 3;
        maxZ = exteriorBounds.max.z;
        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                genRoadBlock(x, y, z);
            }
        }

        //western road
        minX = exteriorBounds.min.x;
        minZ = maximalBounds.getCenterZ() - 2;
        maxX = townBounds.min.x - 1;
        maxZ = minZ + 3;
        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                genRoadBlock(x, y, z);
            }
        }
    }

    private void genRoadBlock(int x, int y, int z) {
        Block block = template.getRoadFillBlock();
        int meta = template.getRoadFillMeta();
        world.setBlock(x, y, z, block, meta, 3);
        world.setBlock(x, y - 1, z, Blocks.cobblestone, 0, 3);
    }

}
