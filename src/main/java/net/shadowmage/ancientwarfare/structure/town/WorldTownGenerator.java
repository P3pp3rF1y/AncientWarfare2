package net.shadowmage.ancientwarfare.structure.town;

import cpw.mods.fml.common.IWorldGenerator;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.shadowmage.ancientwarfare.core.gamedata.AWGameData;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.structure.config.AWStructureStatics;
import net.shadowmage.ancientwarfare.structure.gamedata.StructureMap;
import net.shadowmage.ancientwarfare.structure.gamedata.TownMap;
import net.shadowmage.ancientwarfare.structure.template.build.StructureBB;
import net.shadowmage.ancientwarfare.structure.world_gen.StructureEntry;
import net.shadowmage.ancientwarfare.structure.world_gen.WorldGenTickHandler;

import java.util.Random;

public class WorldTownGenerator implements IWorldGenerator {

    public static final WorldTownGenerator INSTANCE = new WorldTownGenerator();

    private WorldTownGenerator() {
    }

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
        ChunkCoordinates cc = world.getSpawnPoint();
        float distSq = cc.getDistanceSquared(chunkX * 16, cc.posY, chunkZ * 16);
        if (AWStructureStatics.withinProtectionRange(distSq)) {
            return;
        }
        if (random.nextFloat() < AWStructureStatics.townGenerationChance) {
            WorldGenTickHandler.INSTANCE.addChunkForTownGeneration(world, chunkX, chunkZ);
        }
    }

    public void attemptGeneration(World world, int blockX, int blockZ) {
        TownBoundingArea area = TownPlacementValidator.findGenerationPosition(world, blockX, blockZ);
        if (area == null) {
            return;
        }
        TownTemplate template = TownTemplateManager.INSTANCE.selectTemplateForGeneration(world, blockX, blockZ, area);
        if (template == null) {
            return;
        }
        if (area.getChunkWidth() - 1 > template.getMaxSize())//shrink width down to town max size
        {
            area.chunkMaxX = area.chunkMinX + template.getMaxSize();
        }
        if (area.getChunkLength() - 1 > template.getMaxSize())//shrink length down to town max size
        {
            area.chunkMaxZ = area.chunkMinZ + template.getMaxSize();
        }
        if (!TownPlacementValidator.validateAreaForPlacement(world, area)) {
            return;
        }//cannot validate the area until bounds are possibly shrunk by selected template

        /**
         * add the town to the generated structure map, as a -really- large structure entry
         */
        StructureMap map = AWGameData.INSTANCE.getData(world, StructureMap.class);
        StructureBB bb = new StructureBB(new BlockPosition(area.getBlockMinX(), area.getMinY(), area.getBlockMinZ()), new BlockPosition(area.getBlockMaxX(), area.getMaxY(), area.getBlockMaxZ()));
        StructureEntry entry = new StructureEntry(bb, template.getTownTypeName(), template.getClusterValue());
        map.setGeneratedAt(world, area.getCenterX(), area.getSurfaceY(), area.getCenterZ(), 0, entry, false);

        /**
         * add the town to generated town map, to eliminate towns generating too close to eachother
         */
        AWGameData.INSTANCE.getPerWorldData(world, TownMap.class).setGenerated(world, bb);

        /**
         * and finally initialize generation.  The townGenerator will do borders, walls, roads, and add any structures to the world-gen tick handler for generation.
         */
        new TownGenerator(world, area, template).generate();
    }

}
