package net.shadowmage.ancientwarfare.structure.town;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
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
        int minX = gen.wallsBounds.min.getX();
        int minZ = gen.wallsBounds.min.getZ();
        int maxX = gen.wallsBounds.max.getX();
        int maxZ = gen.wallsBounds.max.getZ();
        int minY = gen.wallsBounds.min.getY();

        //construct NW corner
        constructTemplate(world, getCornerSection(rng, template), EnumFacing.SOUTH, new BlockPos(minX, minY, minZ));

        //construct NE corner
        constructTemplate(world, getCornerSection(rng, template), EnumFacing.WEST, new BlockPos(maxX, minY, minZ));

        //construct SE corner
        constructTemplate(world, getCornerSection(rng, template), EnumFacing.NORTH, new BlockPos(maxX, minY, maxZ));

        //construct SW corner
        constructTemplate(world, getCornerSection(rng, template), EnumFacing.EAST, new BlockPos(minX, minY, maxZ));

        if (template.getWallStyle() > 1)//has wall sections
        {
            int chunkWidth = (maxX - minX + 1) / 16;
            int chunkLength = (maxZ - minZ + 1) / 16;
            int x, z;
            EnumFacing facingDirection;
            //construct N wall
            facingDirection = EnumFacing.SOUTH;
            for (int i = 1; i < chunkWidth - 1; i++) {
                x = minX + 16 * i;
                z = minZ;
                constructTemplate(world, getWallSection(rng, template, i, chunkWidth), facingDirection, new BlockPos(x, minY, z));
            }

            //construct E wall
            facingDirection = EnumFacing.WEST;
            for (int i = 1; i < chunkLength - 1; i++) {
                x = maxX;
                z = minZ + 16 * i;
                constructTemplate(world, getWallSection(rng, template, i, chunkLength), facingDirection, new BlockPos(x, minY, z));
            }

            //construct S wall
            facingDirection = EnumFacing.NORTH;
            for (int i = 1; i < chunkWidth - 1; i++) {
                x = maxX - 16 * i;
                z = maxZ;
                constructTemplate(world, getWallSection(rng, template, i, chunkWidth), facingDirection, new BlockPos(x, minY, z));
            }

            //construct W wall
            facingDirection = EnumFacing.EAST;
            for (int i = 1; i < chunkLength - 1; i++) {
                x = minX;
                z = maxZ - 16 * i;
                constructTemplate(world, getWallSection(rng, template, i, chunkLength), facingDirection, new BlockPos(x, minY, z));
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
            if (pattern != null && wallLength <= pattern.length) {
                TownWallEntry entry = template.getWall(template.getWallPattern(wallLength)[index]);
                if (entry != null) {
                    return StructureTemplateManager.INSTANCE.getTemplate(entry.templateName);
                } else {
                }
            }
        }
        return null;
    }

    private static StructureTemplate getCornerSection(Random rng, TownTemplate template) {
        return StructureTemplateManager.INSTANCE.getTemplate(template.getRandomWeightedCorner(rng));
    }

    private static void constructTemplate(World world, StructureTemplate template, EnumFacing face, BlockPos pos) {
        if (template == null) {
            return;
        }
        WorldGenTickHandler.INSTANCE.addStructureForGeneration(new StructureBuilder(world, template, face, pos));
    }

}
