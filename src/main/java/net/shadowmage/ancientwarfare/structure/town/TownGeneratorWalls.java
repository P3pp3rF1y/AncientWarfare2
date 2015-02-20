package net.shadowmage.ancientwarfare.structure.town;

import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplate;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplateManager;
import net.shadowmage.ancientwarfare.structure.template.build.StructureBuilder;
import net.shadowmage.ancientwarfare.structure.town.TownTemplate.TownWallEntry;
import net.shadowmage.ancientwarfare.structure.world_gen.WorldGenTickHandler;

import java.util.Random;

public class TownGeneratorWalls {

    public static void generateWalls(World world, TownGenerator gen, TownTemplate template, Random rng) {
        if (template.getWallStyle() <= 0) {
            return;
        }//no walls
        int minX = gen.wallsBounds.min.x;
        int minZ = gen.wallsBounds.min.z;
        int maxX = gen.wallsBounds.max.x;
        int maxZ = gen.wallsBounds.max.z;
        int chunkWidth = (maxX - minX + 1) / 16;
        int chunkLength = (maxZ - minZ + 1) / 16;
        int minY = gen.wallsBounds.min.y;
        int x, z;
        int facingDirection;

        if (template.getWallStyle() > 0)//has at least corner sections
        {
            //construct NW corner
            facingDirection = Direction.SOUTH.ordinal();
            constructTemplate(world, getCornerSection(rng, template), facingDirection, minX, minY, minZ);

            //construct NE corner
            facingDirection = Direction.WEST.ordinal();
            constructTemplate(world, getCornerSection(rng, template), facingDirection, maxX, minY, minZ);

            //construct SE corner
            facingDirection = Direction.NORTH.ordinal();
            constructTemplate(world, getCornerSection(rng, template), facingDirection, maxX, minY, maxZ);

            //construct SW corner
            facingDirection = Direction.EAST.ordinal();
            constructTemplate(world, getCornerSection(rng, template), facingDirection, minX, minY, maxZ);
        }

        if (template.getWallStyle() > 1)//has wall sections
        {
            //construct N wall
            facingDirection = Direction.SOUTH.ordinal();
            for (int i = 1; i < chunkWidth - 1; i++) {
                x = minX + 16 * i;
                z = minZ;
                constructTemplate(world, getWallSection(rng, template, i, chunkWidth), facingDirection, x, minY, z);
            }

            //construct E wall
            facingDirection = Direction.WEST.ordinal();
            for (int i = 1; i < chunkLength - 1; i++) {
                x = maxX;
                z = minZ + 16 * i;
                constructTemplate(world, getWallSection(rng, template, i, chunkLength), facingDirection, x, minY, z);
            }

            //construct S wall
            facingDirection = Direction.NORTH.ordinal();
            for (int i = 1; i < chunkWidth - 1; i++) {
                x = maxX - 16 * i;
                z = maxZ;
                constructTemplate(world, getWallSection(rng, template, i, chunkWidth), facingDirection, x, minY, z);
            }

            //construct W wall
            facingDirection = Direction.EAST.ordinal();
            for (int i = 1; i < chunkLength - 1; i++) {
                x = minX;
                z = maxZ - 16 * i;
                constructTemplate(world, getWallSection(rng, template, i, chunkLength), facingDirection, x, minY, z);
            }
        }
    }

    private static StructureTemplate getWallSection(Random rng, TownTemplate template, int index, int wallLength) {
        if (template.getWallStyle() == 2)//random weighted
        {
            if (wallLength % 2 == 0)//even sized
            {
                int middle = (wallLength / 2);
                if (index == middle)//return a rgate piece
                {
                    return StructureTemplateManager.INSTANCE.getTemplate(template.getRandomWeightedGateLeft(rng));
                } else if (index == middle - 1) {
                    return StructureTemplateManager.INSTANCE.getTemplate(template.getRandomWeightedGateRight(rng));
                } else {
                    return StructureTemplateManager.INSTANCE.getTemplate(template.getRandomWeightedWall(rng));
                }
            } else {
                int middle = (wallLength / 2);
                if (index == middle)//return a gate piece
                {
                    return StructureTemplateManager.INSTANCE.getTemplate(template.getRandomWeightedGate(rng));
                } else {
                    return StructureTemplateManager.INSTANCE.getTemplate(template.getRandomWeightedWall(rng));
                }
            }
        } else if (template.getWallStyle() == 3)//patterned
        {
            int[] pattern = template.getWallPattern(wallLength);
//    AWLog.logDebug("found pattern for wall size: "+wallLength+ "::" +Arrays.toString(pattern)+" op index: "+index);
            if (pattern != null && wallLength <= pattern.length) {
                TownWallEntry entry = template.getWall(template.getWallPattern(wallLength)[index]);
                if (entry != null) {
//        AWLog.logDebug("found wall entry for index: "+entry.templateName+" :: "+entry.typeName);
                    return StructureTemplateManager.INSTANCE.getTemplate(entry.templateName);
                } else {
//        AWLog.logDebug("Could not locate wall entry for index: "+index);
                }
            }
        }
        return null;
    }

    private static StructureTemplate getCornerSection(Random rng, TownTemplate template) {
        return StructureTemplateManager.INSTANCE.getTemplate(template.getRandomWeightedCorner(rng));
    }

    private static void constructTemplate(World world, StructureTemplate template, int face, int x, int y, int z) {
        if (template == null) {
            return;
        }
        WorldGenTickHandler.INSTANCE.addStructureForGeneration(new StructureBuilder(world, template, face, x, y, z));
    }

}
