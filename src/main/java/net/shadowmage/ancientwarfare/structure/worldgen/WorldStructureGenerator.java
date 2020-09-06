package net.shadowmage.ancientwarfare.structure.worldgen;

import net.minecraft.block.Block;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;
import net.shadowmage.ancientwarfare.core.gamedata.AWGameData;
import net.shadowmage.ancientwarfare.structure.AncientWarfareStructure;
import net.shadowmage.ancientwarfare.structure.config.AWStructureStatics;
import net.shadowmage.ancientwarfare.structure.gamedata.StructureEntry;
import net.shadowmage.ancientwarfare.structure.gamedata.StructureMap;
import net.shadowmage.ancientwarfare.structure.gamedata.TownMap;
import net.shadowmage.ancientwarfare.structure.registry.TerritorySettingRegistry;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplate;
import net.shadowmage.ancientwarfare.structure.template.WorldGenStructureManager;
import net.shadowmage.ancientwarfare.structure.template.build.StructureBB;
import net.shadowmage.ancientwarfare.structure.template.build.StructureBuilderWorldGen;
import net.shadowmage.ancientwarfare.structure.worldgen.stats.PlacementRejectionReason;
import net.shadowmage.ancientwarfare.structure.worldgen.stats.WorldGenStatistics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.Random;

public class WorldStructureGenerator implements IWorldGenerator {

	public static final WorldStructureGenerator INSTANCE = new WorldStructureGenerator();

	private static final int MAX_DISTANCE_WITHIN_CLUSTER = 150;

	private final Random rng;
	private boolean debugTerritoryBorders = false;

	private WorldStructureGenerator() {
		rng = new Random();
	}

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
		TerritoryManager.getTerritory(chunkX, chunkZ, world).ifPresent(territory -> {
			if (debugTerritoryBorders) {
				generateTerritoryBorders(chunkX, chunkZ, world, territory.getTerritoryId());
			}
			BlockPos cc = world.getSpawnPoint();
			double distSq = cc.distanceSq((double) chunkX * 16, cc.getY(), (double) chunkZ * 16);
			if (AWStructureStatics.withinProtectionRange(distSq)) {
				return;
			}
			if (rng.nextFloat() < (AWStructureStatics.randomGenerationChance
					* TerritorySettingRegistry.getTerritorySettings(territory.getTerritoryName()).getStructureGenerationChanceMultiplier())) {
				WorldGenTickHandler.INSTANCE.addChunkForGeneration(world, chunkX, chunkZ);
			}
		});
	}

	void generateAt(int chunkX, int chunkZ, World world) {
		long t1 = System.currentTimeMillis();
		long seed = (((long) chunkX) << 32) | (((long) chunkZ) & 0xffffffffL);
		rng.setSeed(seed);
		int x = chunkX * 16 + rng.nextInt(16);
		int z = chunkZ * 16 + rng.nextInt(16);
		int y = getTargetY(world, x, z, false) + 1;
		if (y <= 0) {
			return;
		}

		TerritoryManager.getTerritory(chunkX, chunkZ, world).ifPresent(territory -> {
			EnumFacing face = EnumFacing.HORIZONTALS[rng.nextInt(4)];
			world.profiler.startSection("AWTemplateSelection");
			Optional<StructureTemplate> t = WorldGenStructureManager.INSTANCE.selectTemplateForGeneration(world, rng, x, y, z, face, territory);
			world.profiler.endSection();
			AncientWarfareStructure.LOG.debug("Template selection took: {} ms.", System.currentTimeMillis() - t1);
			if (!t.isPresent()) {
				return;
			}
			StructureTemplate template = t.get();
			StructureMap map = AWGameData.INSTANCE.getPerWorldData(world, StructureMap.class);

			world.profiler.startSection("AWTemplateGeneration");
			if (attemptStructureGenerationAt(world, new BlockPos(x, y, z), face, template, map)) {
				territory.addClusterValue(template.getValidationSettings().getClusterValue());
				AncientWarfareStructure.LOG.info("Generated structure: {} at {}, {}, {}, time: {}ms", template.name, x, y, z, System.currentTimeMillis() - t1);
			}
			world.profiler.endSection();
		});
	}

	private void generateTerritoryBorders(int chunkX, int chunkZ, World world, String territoryId) {
		ITerritoryData territoryData = world.getCapability(CapabilityTerritoryData.TERRITORY_DATA, null);
		if (territoryData.isDifferentTerritory(territoryId, chunkX - 1, chunkZ)) {
			for (int z = chunkZ * 16 + 1; z < (chunkZ * 16 + 15); z++) {
				int x = chunkX * 16 + 1;
				world.setBlockState(new BlockPos(x, WorldStructureGenerator.getTargetY(world, x, z, false), z), Blocks.CONCRETE.getDefaultState(), 2);
			}
		}

		if (territoryData.isDifferentTerritory(territoryId, chunkX + 1, chunkZ)) {
			for (int z = chunkZ * 16 + 1; z < (chunkZ * 16 + 15); z++) {
				int x = chunkX * 16 + 14;
				world.setBlockState(new BlockPos(x, WorldStructureGenerator.getTargetY(world, x, z, false), z), Blocks.CONCRETE.getDefaultState(), 2);
			}
		}

		if (territoryData.isDifferentTerritory(territoryId, chunkX, chunkZ - 1)) {
			for (int x = chunkX * 16 + 1; x < (chunkX * 16 + 15); x++) {
				int z = chunkZ * 16 + 1;
				world.setBlockState(new BlockPos(x, WorldStructureGenerator.getTargetY(world, x, z, false), z), Blocks.CONCRETE.getDefaultState(), 2);
			}
		}

		if (territoryData.isDifferentTerritory(territoryId, chunkX, chunkZ + 1)) {
			for (int x = chunkX * 16 + 1; x < (chunkX * 16 + 15); x++) {
				int z = chunkZ * 16 + 14;
				world.setBlockState(new BlockPos(x, WorldStructureGenerator.getTargetY(world, x, z, false), z), Blocks.CONCRETE.getDefaultState(), 2);
			}
		}
	}

	public static int getTargetY(World world, int x, int z, boolean skipWater) {
		return getTargetY(world, x, z, skipWater, world.getActualHeight());
	}

	public static int getTargetY(World world, int x, int z, boolean skipWater, int startAtY) {
		Block block;
		for (int y = startAtY; y > 0; y--) {
			IBlockState state = world.getBlockState(new BlockPos(x, y, z));
			block = state.getBlock();
			if (AWStructureStatics.isSkippable(state) || skipWater && (block == Blocks.WATER || block == Blocks.FLOWING_WATER)) {
				continue;
			}
			return y;
		}
		return -1;
	}

	public static void sprinkleSnow(World world, StructureBB bb, int border) {
		BlockPos p1 = bb.min.add(-border, 0, -border);
		BlockPos p2 = bb.max.add(border, 0, border);
		for (int x = p1.getX(); x <= p2.getX(); x++) {
			for (int z = p1.getZ(); z <= p2.getZ(); z++) {
				int y = world.getPrecipitationHeight(new BlockPos(x, 1, z)).getY() - 1;
				BlockPos pos = new BlockPos(x, y, z);
				if (p2.getY() >= y && y > 0 && world.canSnowAtBody(pos.up(), true)) {
					IBlockState state = world.getBlockState(pos);
					Block block = state.getBlock();
					if (block != Blocks.AIR && state.getBlockFaceShape(world, pos, EnumFacing.UP) == BlockFaceShape.SOLID) {
						world.setBlockState(pos.up(), Blocks.SNOW_LAYER.getDefaultState());
					}
				}
			}
		}
	}

	public static int getStepNumber(int x, int z, int minX, int maxX, int minZ, int maxZ) {
		int steps = 0;
		if (x < minX - 1) {
			steps += (minX - 1) - x;
		} else if (x > maxX + 1) {
			steps += x - (maxX + 1);
		}
		if (z < minZ - 1) {
			steps += (minZ - 1) - z;
		} else if (z > maxZ + 1) {
			steps += z - (maxZ + 1);
		}
		return steps;
	}

	private boolean attemptStructureGenerationAt(World world, BlockPos pos, EnumFacing face, StructureTemplate template, StructureMap map) {
		long t1 = System.currentTimeMillis();
		int prevY = pos.getY();
		StructureBB bb = new StructureBB(pos, face, template.getSize(), template.getOffset());
		int y = template.getValidationSettings().getAdjustedSpawnY(world, pos.getX(), pos.getY(), pos.getZ(), face, template, bb);
		pos = new BlockPos(pos.getX(), y, pos.getZ());
		bb.min = bb.min.up(y - prevY);
		bb.max = bb.max.up(y - prevY);
		int xs = bb.getXSize();
		int zs = bb.getZSize();
		int size = ((Math.max(xs, zs)) / 16) + 3;
		if (!checkOtherStructureCrossAndCloseness(world, pos, map, bb, size, template.getValidationSettings().getBorderSize())) {
			WorldGenStatistics.addStructurePlacementRejection(template.name, PlacementRejectionReason.STRUCTURE_BB_OVERLAP);
			WorldGenDetailedLogHelper.log("Structure \"{}\" failed placement, because its bounding box {} intersects an existing structure", () -> template.name, () -> bb);
			return false;
		}

		TownMap townMap = AWGameData.INSTANCE.getPerWorldData(world, TownMap.class);
		if (townMap.intersectsWithTown(bb)) {
			WorldGenStatistics.addStructurePlacementRejection(template.name, PlacementRejectionReason.TOWN_BB_OVERLAP);
			WorldGenDetailedLogHelper.log("Structure \"{}\" failed placement, because its bounding box {} intersects an existing town", () -> template.name, () -> bb);
			return false;
		}
		if (template.getValidationSettings().validatePlacement(world, pos.getX(), pos.getY(), pos.getZ(), face, template, bb)) {
			WorldGenStatistics.addStructureGeneratedInfo(template.name, world, pos);
			AncientWarfareStructure.LOG.debug("Validation took: {} ms", System.currentTimeMillis() - t1);
			generateStructureAt(world, pos, face, template, map);
			return true;
		}
		return false;
	}

	private boolean checkOtherStructureCrossAndCloseness(World world, BlockPos pos, StructureMap map, StructureBB bb, int size, int borderSize) {
		Collection<StructureEntry> bbCheckList = map.getEntriesNear(world, pos.getX(), pos.getZ(), size, true, new ArrayList<>());
		double maxDistance = 0;
		StructureBB bbWithBorder = new StructureBB(bb.min, bb.max).expand(borderSize, 0, borderSize);
		for (StructureEntry entry : bbCheckList) {
			if (bbWithBorder.intersects(entry.getBB())) {
				return false;
			}
			double distance = bb.getDistanceTo(entry.getBB());
			if (distance < MAX_DISTANCE_WITHIN_CLUSTER && distance > maxDistance) {
				maxDistance = distance;
			}
		}
		return !(maxDistance > 30 && world.rand.nextFloat() * (MAX_DISTANCE_WITHIN_CLUSTER - maxDistance) > 30);
	}

	private void generateStructureAt(World world, BlockPos pos, EnumFacing face, StructureTemplate template, StructureMap map) {
		map.setGeneratedAt(world, pos.getX(), pos.getZ(), new StructureEntry(pos.getX(), pos.getY(), pos.getZ(), face, template), template.getValidationSettings().isUnique());
		WorldGenTickHandler.INSTANCE.addStructureForGeneration(new StructureBuilderWorldGen(world, template, face, pos));
	}

}
