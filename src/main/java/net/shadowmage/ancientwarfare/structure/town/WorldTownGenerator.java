package net.shadowmage.ancientwarfare.structure.town;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;
import net.shadowmage.ancientwarfare.core.gamedata.AWGameData;
import net.shadowmage.ancientwarfare.structure.config.AWStructureStatics;
import net.shadowmage.ancientwarfare.structure.gamedata.StructureEntry;
import net.shadowmage.ancientwarfare.structure.gamedata.StructureMap;
import net.shadowmage.ancientwarfare.structure.gamedata.TownEntry;
import net.shadowmage.ancientwarfare.structure.gamedata.TownMap;
import net.shadowmage.ancientwarfare.structure.template.build.StructureBB;
import net.shadowmage.ancientwarfare.structure.worldgen.WorldGenTickHandler;

import java.util.List;
import java.util.Optional;
import java.util.Random;

public class WorldTownGenerator implements IWorldGenerator {

	public static final WorldTownGenerator INSTANCE = new WorldTownGenerator();

	private WorldTownGenerator() {
	}

	@Override
	@SuppressWarnings("squid:S2184") //coordinates are capped by int so they are not going to overflow max int before converted to double
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
		BlockPos cc = world.getSpawnPoint();
		double distSq = cc.distanceSq(chunkX * 16, cc.getY(), chunkZ * 16);
		if (AWStructureStatics.withinProtectionRange(distSq)) {
			return;
		}
		if (random.nextFloat() < AWStructureStatics.townGenerationChance) {
			WorldGenTickHandler.INSTANCE.addChunkForTownGeneration(world, chunkX, chunkZ);
		}
	}

	public void attemptGeneration(World world, int blockX, int blockZ) {
		List<TownTemplate> templates = TownTemplateManager.INSTANCE.getTemplatesValidAtPosition(world, blockX, blockZ);
		if (templates.isEmpty()) {
			return;
		}

		TownBoundingArea area = TownPlacementValidator.findGenerationPosition(world, blockX, blockZ);
		if (area == null) {
			return;
		}

		Optional<TownTemplate> t = TownTemplateManager.INSTANCE.selectTemplateFittingArea(world, area, templates);
		if (!t.isPresent()) {
			return;
		}
		TownTemplate template = t.get();
		if (area.getChunkWidth() - 1 > template.getMaxSize())//shrink width down to town max size
		{
			area.chunkMaxX = area.chunkMinX + template.getMaxSize();
		}
		if (area.getChunkLength() - 1 > template.getMaxSize())//shrink length down to town max size
		{
			area.chunkMaxZ = area.chunkMinZ + template.getMaxSize();
		}
		generate(world, area, template);

	}

	public void generate(World world, TownBoundingArea area, TownTemplate template) {
		/*
		 * add the town to the generated structure map, as a -really- large structure entry
		 */
		//TODO figure out how to remove this from here - probably only used for cluster value and for that we need to add something to territory anyway
		StructureMap map = AWGameData.INSTANCE.getPerWorldData(world, StructureMap.class);
		StructureBB bb = new StructureBB(new BlockPos(area.getBlockMinX(), area.getMinY(), area.getBlockMinZ()), new BlockPos(area.getBlockMaxX(), area.getMaxY(), area.getBlockMaxZ()));
		StructureEntry entry = new StructureEntry(bb, template.getTownTypeName(), template.getClusterValue(), area.getCenterX() >> 4, area.getCenterZ() >> 4);
		map.setGeneratedAt(world, area.getCenterX(), area.getCenterZ(), entry, false);

		/*
		 * add the town to generated town map, to eliminate towns generating too close to eachother
		 */
		AWGameData.INSTANCE.getPerWorldData(world, TownMap.class).setGenerated(new TownEntry(bb, template.shouldPreventNaturalHostileSpawns()));

		/*
		 * and finally initialize generation.  The townGenerator will do borders, walls, roads, and add any structures to the world-gen tick handler for generation.
		 */
		new TownGenerator(world, area, template).generate();
	}

}
