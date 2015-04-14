package net.shadowmage.ancientwarfare.structure.town;

import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.core.util.Trig;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplate;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplateManager;
import net.shadowmage.ancientwarfare.structure.template.build.StructureBB;
import net.shadowmage.ancientwarfare.structure.template.build.StructureBuilder;
import net.shadowmage.ancientwarfare.structure.town.TownTemplate.TownStructureEntry;
import net.shadowmage.ancientwarfare.structure.world_gen.WorldGenTickHandler;
import net.shadowmage.ancientwarfare.structure.world_gen.WorldGenTickHandler.StructureGenerationCallbackTicket;

import java.util.*;

public class TownGeneratorStructures {

    public static void generateStructures(final TownGenerator gen) {
        final List<TownPartBlock> blocks = new ArrayList<TownPartBlock>();
        for (TownPartQuadrant tq : gen.quadrants) {
            tq.addBlocks(blocks);
        }
        sortBlocksByDistance(blocks);
        generateUniques(blocks, gen.uniqueTemplatesToGenerate, gen);
        generateMains(blocks, gen.mainTemplatesToGenerate, gen);
        generateHouses(blocks, gen.houseTemplatesToGenerate, gen);
        generateCosmetics(blocks, gen.cosmeticTemplatesToGenerate, gen);

        if (gen.template.getExteriorSize() > 0) {
            blocks.clear();
            for (TownPartQuadrant tq : gen.externalQuadrants) {
                tq.addBlocks(blocks);
            }
            generateExteriorStructures(blocks, gen.exteriorTemplatesToGenerate, gen);
        }

        WorldGenTickHandler.INSTANCE.addStructureGenCallback(new StructureGenerationCallbackTicket() {
            @Override
            public void call() {
                TownGeneratorStructures.generateLamps(blocks, gen.template.getLamp(), gen);
            }
        });
    }

    private static void generateUniques(List<TownPartBlock> blocks, List<StructureTemplate> templatesToGenerate, TownGenerator gen) {
        outer:
        for (TownPartBlock block : blocks) {
            for (TownPartPlot plot : block.plots)//iterate through plots, gen on the first valid plot for the block, then break to the next block
            {
                if (plot.closed) {
                    continue;
                }
                if (!plot.hasRoadBorder()) {
                    continue;
                }//no borders
                if (templatesToGenerate.isEmpty()) {
                    break outer;
                }
                if (generateStructureForPlot(gen, plot, templatesToGenerate.get(0), false)) {
                    templatesToGenerate.remove(0);
                }
            }
        }
    }

    private static void generateMains(List<TownPartBlock> blocks, List<StructureTemplate> templatesToGenerate, TownGenerator gen) {
        outer:
        for (TownPartBlock block : blocks) {
            for (TownPartPlot plot : block.plots)//iterate through plots, gen on the first valid plot for the block, then break to the next block
            {
                if (plot.closed) {
                    continue;
                }
                if (!plot.hasRoadBorder()) {
                    continue;
                }//no borders
                if (templatesToGenerate.isEmpty()) {
                    break outer;
                }
                if (generateStructureForPlot(gen, plot, templatesToGenerate.get(0), false)) {
                    templatesToGenerate.remove(0);
                }
            }
        }
    }

    private static void generateHouses(List<TownPartBlock> blocks, List<StructureTemplate> templatesToGenerate, TownGenerator gen) {
        outer:
        for (TownPartBlock block : blocks) {
            for (TownPartPlot plot : block.plots) {
                if (templatesToGenerate.isEmpty()) {
                    break outer;
                }
                if (plot.closed || !plot.hasRoadBorder()) {
                    continue;
                }
                if (gen.template.getInteriorEmtpyPlotChance() > 0) {
                    if (gen.rng.nextInt(100) < gen.template.getInteriorEmtpyPlotChance()) {
                        plot.skipped = true;//mark skipped, so it is skipped by cosmetic generation as well (do not close, allow expansion onto this plot).
                        continue;
                    }
                }
                generateStructureForPlot(gen, plot, getRandomTemplate(templatesToGenerate, gen.rng), false);
            }
        }
    }

    private static void generateCosmetics(List<TownPartBlock> blocks, List<StructureTemplate> templatesToGenerate, TownGenerator gen) {
        outer:
        for (TownPartBlock block : blocks) {
            for (TownPartPlot plot : block.plots) {
                if (plot.closed || plot.skipped) {
                    continue;
                }
                if (templatesToGenerate.isEmpty()) {
                    break outer;
                }
                generateStructureForPlot(gen, plot, getRandomTemplate(templatesToGenerate, gen.rng), true);
            }
        }
    }

    private static void generateExteriorStructures(List<TownPartBlock> blocks, List<StructureTemplate> templatesToGenerate, TownGenerator gen) {
        float l1 = gen.exteriorBounds.getXSize() / 2.f;
        float l2 = gen.exteriorBounds.getZSize() / 2.f;
        float maxDistance = Trig.getDistance(l1, 0, l2, 0, 0, 0);
        l1 = gen.wallsBounds.getXSize() / 2.f;
        l2 = gen.wallsBounds.getZSize() / 2.f;
        float minDistance = l1 < l2 ? l1 : l2;
        float minMaxDelta = maxDistance - minDistance;
        float plotDistance, distPercent;

        outer:
        for (TownPartBlock block : blocks) {
            for (TownPartPlot plot : block.plots) {
                if (plot.closed) {
                    continue;
                }
                if (templatesToGenerate.isEmpty()) {
                    break outer;
                }
                plotDistance = Trig.getDistance(plot.bb.getCenterX(), 0, plot.bb.getCenterZ(), gen.maximalBounds.getCenterX(), 0, gen.maximalBounds.getCenterZ()) - minDistance;
                distPercent = plotDistance / minMaxDelta;
                distPercent = 1.f - distPercent;
                distPercent *= distPercent;
                if (gen.rng.nextFloat() < distPercent) {
                    generateStructureForPlot(gen, plot, getRandomTemplate(templatesToGenerate, gen.rng), true);
                }
            }
        }
    }

    public static void generateLamps(List<TownPartBlock> blocks, TownStructureEntry templateToGenerate, final TownGenerator gen) {
        if (templateToGenerate == null) {
            return;
        }
        StructureTemplate lamp = StructureTemplateManager.INSTANCE.getTemplate(templateToGenerate.templateName);
        if (lamp == null) {
            return;
        }
        for (TownPartBlock block : blocks) {
            generateLamps(block, lamp, gen);
        }
        WorldGenTickHandler.INSTANCE.addStructureGenCallback(new StructureGenerationCallbackTicket() {
            @Override
            public void call() {
                gen.generateVillagers();
            }
        });
    }

    private static void generateLamps(TownPartBlock block, StructureTemplate lamp, TownGenerator gen) {
        Direction xDir = block.quadrant.getXDir();
        Direction zDir = block.quadrant.getZDir();
        int xStart, zStart, xMove, zMove, size, x, z, xBits, zBits;

        size = 5;
        xBits = (block.bb.getXSize() - 1) / size;
        zBits = (block.bb.getZSize() - 1) / size;

        if (block.bb.getXSize() % size == size - 1) {
            xBits--;
        }//ensures two lamps are not adjacent near the corner of the road
        if (block.bb.getZSize() % size == size - 1) {
            zBits--;
        }//ensures two lamps are not adjacent near the corner of the road

        if (xDir == Direction.WEST) {
            xStart = block.bb.max.x;
            xMove = -size;
        } else {
            xStart = block.bb.min.x;
            xMove = size;
        }

        if (zDir == Direction.NORTH) {
            zStart = block.bb.max.z;
            zMove = -size;
        } else {
            zStart = block.bb.min.z;
            zMove = size;
        }

        if (block.hasRoadBorder(Direction.NORTH)) {
            for (int xBit = 0; xBit <= xBits; xBit++) {
                x = xBit * xMove + xStart;
                generateLamp(gen.world, lamp, gen, x, block.bb.min.y, block.bb.min.z, Direction.EAST);
            }
        }

        if (block.hasRoadBorder(Direction.SOUTH)) {
            for (int xBit = 0; xBit <= xBits; xBit++) {
                x = xBit * xMove + xStart;
                generateLamp(gen.world, lamp, gen, x, block.bb.min.y, block.bb.max.z, Direction.EAST);
            }
        }

        if (block.hasRoadBorder(Direction.WEST)) {
            for (int zBit = 0; zBit <= zBits; zBit++) {
                z = zBit * zMove + zStart;
                generateLamp(gen.world, lamp, gen, block.bb.min.x, block.bb.min.y, z, Direction.EAST);
            }
        }

        if (block.hasRoadBorder(Direction.EAST)) {
            for (int zBit = 0; zBit <= zBits; zBit++) {
                z = zBit * zMove + zStart;
                generateLamp(gen.world, lamp, gen, block.bb.max.x, block.bb.min.y, z, Direction.EAST);
            }
        }
    }

    private static void generateLamp(World world, StructureTemplate t, TownGenerator gen, int x, int y, int z, Direction streetSide) {
        if (checkForNeighboringDoor(gen, x, y, z, streetSide.getOpposite())) {
            return;
        }
        int minX = x;
        int minZ = z;
        int minY = y;
        int maxX = x + t.xSize - 1;
        int maxY = y + (t.ySize - 1 - t.yOffset);
        int maxZ = z + t.zSize - 1;

        for (int x1 = minX; x1 <= maxX; x1++) {
            for (int z1 = minZ; z1 <= maxZ; z1++) {
                for (int y1 = minY; y1 <= maxY; y1++) {
                    if (!world.isAirBlock(x1, y1, z1)) {
                        return;
                    }//skip construction if it would overwrite any non-valid block (should ALL be air)
                }
            }
        }
        x -= (t.xSize / 2);
        z -= (t.zSize / 2);
        x += t.xOffset;
        z += t.zOffset;
        WorldGenTickHandler.INSTANCE.addStructureForGeneration(new StructureBuilder(world, t, 0, x, y, z));
    }

    private static boolean checkForNeighboringDoor(TownGenerator gen, int x, int y, int z, Direction dir) {
        final int len = gen.structureDoors.size();
        int x1 = x;
        int z1 = z;
        x1 += dir.xDirection;
        z1 += dir.zDirection;
        BlockPosition p;
        for (int i = 0; i < len; i++) {
            p = gen.structureDoors.get(i);
            if (p.x == x && p.z == z) {
                return true;
            } else if (p.x == x1 && p.z == z1) {
                return true;
            }
        }
        return false;
    }

//************************************************* UTILITY METHODS *******************************************************//

    /**
     * attempt to generate a structure at the given plot
     *
     * @return true if generated
     */
    private static boolean generateStructureForPlot(TownGenerator gen, TownPartPlot plot, StructureTemplate template, boolean centerLength) {
        int expansion = gen.template.getTownBuildingWidthExpansion();
        int face = gen.rng.nextInt(4);//select random face
        for (int i = 0, f = face; i < 4; i++, f++)//and then iterate until a valid face is found
        {
            if (f > 3) {
                f = 0;
            }
            if (plot.roadBorders[f]) {
                face = f;
                break;
            }
        }
        face = (face + 2) % 4;//reverse face from road edge...
        int width = face == 0 || face == 2 ? template.xSize : template.zSize;
        int length = face == 0 || face == 2 ? template.zSize : template.xSize;
        if (face == 0 || face == 2) {
            width += expansion;
        }//temporarily expand the size of the bb by the town-template building expansion size, ensures there is room around buildings
        else {
            length += expansion;
        }
        if (plot.getWidth() < width || plot.getLength() < length) {
            if (!plot.expand(width, length)) {
                return false;
            }
        }
        plot.markClosed();
        if (face == 0 || face == 2) {
            width -= expansion;
        } else {
            length -= expansion;
        }
        generateStructure(gen, plot, template, face, width, length, centerLength);
        return true;
    }

    /**
     * @param world    the world object that is currently being generated
     * @param plot     the pre-expanded plot that will have the structure generated on it
     * @param template the template to be generated
     * @param face     generation orientation for the structure
     * @param width    rotated structure x-dimension
     * @param length   rotated structure z-dimension
     * @param center   should the structure be centered in plot, or placed along road-edge?
     */
    private static void generateStructure(TownGenerator gen, TownPartPlot plot, StructureTemplate template, int face, int width, int length, boolean center) {
        int plotWidth = plot.getWidth();
        int plotLength = plot.getLength();
        int extraWidth = plotWidth - width;//unused width portion of the plot
        int extraLength = plotLength - length;//unused length portion of the plot

        int wAdj;
        int lAdj;

        if (center) {
            wAdj = extraWidth / 2;
            lAdj = extraLength / 2;
        } else {
            wAdj = (face == 0 || face == 2) ? extraWidth / 2 : face == 1 ? extraWidth : 0;
            lAdj = (face == 1 || face == 3) ? extraLength / 2 : face == 2 ? extraLength : 0;
        }

        //find corners of the bb for the structure
        BlockPosition min = new BlockPosition(plot.bb.min.x + wAdj, gen.townBounds.min.y, plot.bb.min.z + lAdj);
        BlockPosition max = new BlockPosition(min.x + (width - 1), min.y + template.ySize, min.z + (length - 1));
        StructureBB bb = new StructureBB(min, max);

        BlockPosition buildKey = bb.getRLCorner(face, new BlockPosition());
        buildKey.moveRight(face, template.xOffset);
        buildKey.moveBack(face, template.zOffset);
        buildKey.y -= template.yOffset;
        buildKey.y += gen.townBounds.min.y;
        bb.offset(0, -template.yOffset, 0);
        gen.structureDoors.add(buildKey.copy());
        WorldGenTickHandler.INSTANCE.addStructureForGeneration(new StructureBuilder(gen.world, template, face, buildKey, bb));
//  AWLog.logDebug("added structure to tick handler for generation: "+template.name +" at: "+buildKey+" town bounds: "+gen.townBounds);
    }

    /**
     * pull a random template from the input generation list, does not remove
     */
    public static StructureTemplate getRandomTemplate(List<StructureTemplate> templatesToGenerate, Random rng) {
        if (templatesToGenerate.size() == 0) {
            return null;
        }
        int roll = rng.nextInt(templatesToGenerate.size());
        return templatesToGenerate.get(roll);
    }

    public static void sortBlocksByDistance(List<TownPartBlock> blocks) {
        Collections.sort(blocks, new TownPartBlockComparator());
    }

    public static class TownPartBlockComparator implements Comparator<TownPartBlock> {

        @Override
        public int compare(TownPartBlock o1, TownPartBlock o2) {
            if (o1.distFromTownCenter < o2.distFromTownCenter) {
                return -1;
            } else if (o1.distFromTownCenter > o2.distFromTownCenter) {
                return 1;
            }
            return 0;
        }
    }
}
