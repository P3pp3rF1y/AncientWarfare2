package net.shadowmage.ancientwarfare.structure.town;

import net.minecraft.block.Block;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.structure.AncientWarfareStructure;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplate;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplateManager;
import net.shadowmage.ancientwarfare.structure.template.build.StructureBB;
import net.shadowmage.ancientwarfare.structure.town.TownTemplate.TownStructureEntry;
import net.shadowmage.ancientwarfare.structure.worldgen.WorldGenTickHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/*
 * Responsible for constructing the town -- leveling the area, placing the structures, constructing walls
 *
 * @author Shadowmage
 */
public class TownGenerator {

	public final TownTemplate template;
	public final World world;
	public final Random rng;
	public final StructureBB maximalBounds;
	public final StructureBB exteriorBounds;//maximal, shrunk by borderSize (16 blocks), maximal area encompassing extents of exterior buffer zone
	public final StructureBB wallsBounds;//exterior shrunk by exteriorSize (configurable), maximal area encompassing extents of walls
	public final StructureBB townBounds;//walls shrunk by wallSize (configurable), town generation area
	public final TownPartQuadrant[] quadrants = new TownPartQuadrant[4];
	public final TownPartQuadrant[] externalQuadrants = new TownPartQuadrant[8];//may be null refs if no exterior area is denoted
	public final List<StructureTemplate> uniqueTemplatesToGenerate = new ArrayList<>();//depleted as used
	public final List<StructureTemplate> mainTemplatesToGenerate = new ArrayList<>();//depleted as used
	public final List<StructureTemplate> houseTemplatesToGenerate = new ArrayList<>();//weighted list
	public final List<StructureTemplate> cosmeticTemplatesToGenerate = new ArrayList<>();//weighted list
	public final List<StructureTemplate> exteriorTemplatesToGenerate = new ArrayList<>();//weighted list
	public final List<BlockPos> structureDoors = new ArrayList<>();//list of all positions for generated doors.  used during lamp-post generation to not generate directly in front of a door

	public TownGenerator(World world, TownBoundingArea area, TownTemplate template) {
		this.world = world;
		this.template = template;
		long seed = (area.getCenterX() << 16) | area.getCenterZ();
		this.rng = new Random(seed);

		int y1 = area.getSurfaceY() + 1;
		int y2 = y1 + 20;

		area.wallSize = template.getWallSize();
		area.exteriorSize = template.getExteriorSize();

		this.maximalBounds = new StructureBB(area.getBlockMinX(), y1, area.getBlockMinZ(), area.getBlockMaxX(), y2, area.getBlockMaxZ());
		this.exteriorBounds = new StructureBB(area.getExteriorMinX(), y1, area.getExteriorMinZ(), area.getExteriorMaxX(), y2, area.getExteriorMaxZ());
		this.wallsBounds = new StructureBB(area.getWallMinX(), y1, area.getWallMinZ(), area.getWallMaxX(), y2, area.getWallMaxZ());
		this.townBounds = new StructureBB(area.getTownMinX(), y1, area.getTownMinZ(), area.getTownMaxX(), y2, area.getTownMaxZ());
	}

	/*
	 * Call this to initialize and start the generation of the town
	 */
	public void generate() {
		AncientWarfareStructure.LOG.info("Generating town at: " + townBounds.getCenterX() + " : " + townBounds.getCenterZ());
		determineStructuresToGenerate();
		TownGeneratorBorders.generateBorders(world, exteriorBounds, wallsBounds, maximalBounds);
		TownGeneratorBorders.levelTownArea(world, wallsBounds);

		generateGrid();
		TownGeneratorWalls.generateWalls(world, this, template, rng);
		WorldGenTickHandler.INSTANCE.addStructureGenCallback(() -> {
			generateRoads();
			TownGeneratorStructures.generateStructures(TownGenerator.this);
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
			int x = townBounds.min.getX();
			int y = townBounds.min.getY();//surface height?
			int z = townBounds.min.getZ();

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
		BlockPos pos = new BlockPos(minX, y, minZ);
		EntityVillager villager = new EntityVillager(world);
		villager.onInitialSpawn(world.getDifficultyForLocation(pos), null);
		for (int i = 0; i < 10; i++) {
			int x = minX + rng.nextInt(16);
			int z = minZ + rng.nextInt(16);
			pos = new BlockPos(x, y, z);
			if (world.isAirBlock(pos) && world.isAirBlock(pos.up()) && world.isSideSolid(pos.down(), EnumFacing.UP)) {
				villager.setPosition(x + 0.5d, y, z + 0.5d);
				world.spawnEntity(villager);
				return;
			}
		}
	}

	/*
	 * add initial generation entries to list of structures to attempt to generate
	 */
	private void determineStructuresToGenerate() {
		for (TownStructureEntry e : template.getUniqueStructureEntries()) {
			StructureTemplateManager.getTemplate(e.templateName).ifPresent(uniqueTemplatesToGenerate::add);
		}

		for (TownStructureEntry e : template.getMainStructureEntries()) {
			StructureTemplateManager.getTemplate(e.templateName).ifPresent(mainTemplatesToGenerate::add);
		}

		for (TownStructureEntry e : template.getHouseStructureEntries()) {
			StructureTemplateManager.getTemplate(e.templateName).ifPresent(t -> {
				for (int i = 0; i < e.min; i++) {
					houseTemplatesToGenerate.add(t);
				}
			});
		}

		for (TownStructureEntry e : template.getCosmeticEntries()) {
			StructureTemplateManager.getTemplate(e.templateName).ifPresent(t -> {
				for (int i = 0; i < e.min; i++) {
					this.cosmeticTemplatesToGenerate.add(t);
				}
			});
		}

		for (TownStructureEntry e : template.getExteriorStructureEntries()) {
			StructureTemplateManager.getTemplate(e.templateName).ifPresent(t -> {
				for (int i = 0; i < e.min; i++) {
					exteriorTemplatesToGenerate.add(t);
				}
			});
		}
	}

	/*
	 * Splits up the town into four quadrants<br>
	 * quadrants into blocks<br>
	 * and blocks into plots<br>
	 */
	private void generateGrid() {
		final int centerX = maximalBounds.getCenterX();
		final int centerZ = maximalBounds.getCenterZ();
		final int y1 = townBounds.min.getY();
		final int y2 = townBounds.max.getY();

		StructureBB bb;
		TownPartQuadrant tq;
		boolean[] roadBorders;

		//northwest quadrant, pre-shrunk for road borders
		roadBorders = new boolean[] {true, false, false, true};
		bb = new StructureBB(new BlockPos(townBounds.min.getX(), y1, townBounds.min.getZ()), new BlockPos(centerX - 2, y2, centerZ - 2));
		tq = new TownPartQuadrant(Direction.WEST, Direction.NORTH, bb, roadBorders, this);
		tq.subdivide(template.getTownBlockSize(), template.getTownPlotSize(), true);
		quadrants[0] = tq;

		//northeast quadrant
		roadBorders = new boolean[] {true, true, false, false};
		bb = new StructureBB(new BlockPos(centerX + 1, y1, townBounds.min.getZ()), new BlockPos(townBounds.max.getX(), y2, centerZ - 2));
		tq = new TownPartQuadrant(Direction.EAST, Direction.NORTH, bb, roadBorders, this);
		tq.subdivide(template.getTownBlockSize(), template.getTownPlotSize(), true);
		quadrants[1] = tq;

		//southeast quadrant
		roadBorders = new boolean[] {false, true, true, false};
		bb = new StructureBB(new BlockPos(centerX + 1, y1, centerZ + 1), new BlockPos(townBounds.max.getX(), y2, townBounds.max.getZ()));
		tq = new TownPartQuadrant(Direction.EAST, Direction.SOUTH, bb, roadBorders, this);
		tq.subdivide(template.getTownBlockSize(), template.getTownPlotSize(), true);
		quadrants[2] = tq;

		//southwest quadrant
		roadBorders = new boolean[] {false, false, true, true};
		bb = new StructureBB(new BlockPos(townBounds.min.getX(), y1, centerZ + 1), new BlockPos(centerX - 2, y2, townBounds.max.getZ()));
		tq = new TownPartQuadrant(Direction.WEST, Direction.SOUTH, bb, roadBorders, this);
		tq.subdivide(template.getTownBlockSize(), template.getTownPlotSize(), true);
		quadrants[3] = tq;

		if (template.getExteriorSize() > 0) {
			generateExteriorGrid();
		}
	}

	/*
	 * Splits any exterior buffer zone into 8 not-quite-quadrants, and those further down into blocks and plots
	 */
	private void generateExteriorGrid() {
		StructureBB bb;
		boolean[] roadBorders;
		int centerX = maximalBounds.getCenterX();
		int centerZ = maximalBounds.getCenterZ();

		int minX;
		int minY;
		int minZ;
		int maxX;
		int maxY;
		int maxZ;

		int eSize = template.getExteriorSize() * 16;
		int pSize = template.getTownPlotSize();

		minY = maximalBounds.min.getY();
		maxY = maximalBounds.max.getY();

		//1, northwest
		minX = exteriorBounds.min.getX();
		minZ = exteriorBounds.min.getZ();
		maxX = centerX - 3;
		maxZ = wallsBounds.min.getZ() - 1;
		roadBorders = new boolean[] {false, false, false, false};//
		bb = new StructureBB(new BlockPos(minX, minY, minZ), new BlockPos(maxX, maxY, maxZ));
		externalQuadrants[0] = new TownPartQuadrant(Direction.WEST, Direction.NORTH, bb, roadBorders, this);

		//2, northeast
		minX = centerX + 2;
		minZ = exteriorBounds.min.getZ();
		maxX = exteriorBounds.max.getX();
		maxZ = wallsBounds.min.getZ() - 1;
		bb = new StructureBB(new BlockPos(minX, minY, minZ), new BlockPos(maxX, maxY, maxZ));
		externalQuadrants[1] = new TownPartQuadrant(Direction.EAST, Direction.NORTH, bb, roadBorders, this);

		//3 west, north-part
		minX = exteriorBounds.min.getX();
		minZ = wallsBounds.min.getZ();
		maxX = wallsBounds.min.getX() - 1;
		maxZ = centerZ - 3;
		bb = new StructureBB(new BlockPos(minX, minY, minZ), new BlockPos(maxX, maxY, maxZ));
		externalQuadrants[2] = new TownPartQuadrant(Direction.WEST, Direction.NORTH, bb, roadBorders, this);

		//4 east, north-part
		minX = wallsBounds.max.getX() + 1;
		minZ = wallsBounds.min.getZ();
		maxX = exteriorBounds.max.getX();
		maxZ = centerZ - 3;
		bb = new StructureBB(new BlockPos(minX, minY, minZ), new BlockPos(maxX, maxY, maxZ));
		externalQuadrants[3] = new TownPartQuadrant(Direction.EAST, Direction.NORTH, bb, roadBorders, this);

		//5 west, south-part
		minX = exteriorBounds.min.getX();
		minZ = centerZ + 2;
		maxX = wallsBounds.min.getX() - 1;
		maxZ = wallsBounds.max.getZ();
		bb = new StructureBB(new BlockPos(minX, minY, minZ), new BlockPos(maxX, maxY, maxZ));
		externalQuadrants[4] = new TownPartQuadrant(Direction.WEST, Direction.NORTH, bb, roadBorders, this);

		//6 east, south-part
		minX = wallsBounds.max.getX() + 1;
		minZ = centerZ + 2;
		maxX = exteriorBounds.max.getX();
		maxZ = wallsBounds.max.getZ();
		bb = new StructureBB(new BlockPos(minX, minY, minZ), new BlockPos(maxX, maxY, maxZ));
		externalQuadrants[5] = new TownPartQuadrant(Direction.EAST, Direction.NORTH, bb, roadBorders, this);

		//7 southwest
		minX = exteriorBounds.min.getX();
		minZ = wallsBounds.max.getZ() + 1;
		maxX = centerX - 3;
		maxZ = exteriorBounds.max.getZ();
		bb = new StructureBB(new BlockPos(minX, minY, minZ), new BlockPos(maxX, maxY, maxZ));
		externalQuadrants[6] = new TownPartQuadrant(Direction.WEST, Direction.SOUTH, bb, roadBorders, this);

		//8 southeast
		minX = centerX + 2;
		minZ = wallsBounds.max.getZ() + 1;
		maxX = exteriorBounds.max.getX();
		maxZ = exteriorBounds.max.getZ();
		bb = new StructureBB(new BlockPos(minX, minY, minZ), new BlockPos(maxX, maxY, maxZ));
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

	/*
	 * generates roads on the extent of a townquadrant
	 */
	private void generateRoads(TownPartQuadrant tq) {
		int minX = tq.bb.min.getX();
		int maxX = tq.bb.max.getX();
		if (tq.hasRoadBorder(Direction.WEST)) {
			minX--;
		}
		if (tq.hasRoadBorder(Direction.EAST)) {
			maxX++;
		}
		for (int x = minX; x <= maxX; x++) {
			if (tq.hasRoadBorder(Direction.NORTH)) {
				genRoadBlock(x, tq.bb.min.getY() - 1, tq.bb.min.getZ() - 1);
			}//north
			if (tq.hasRoadBorder(Direction.SOUTH)) {
				genRoadBlock(x, tq.bb.min.getY() - 1, tq.bb.max.getZ() + 1);
			}//south
		}
		int minZ = tq.bb.min.getZ();
		int maxZ = tq.bb.max.getZ();
		if (tq.hasRoadBorder(Direction.NORTH)) {
			minZ--;
		}
		if (tq.hasRoadBorder(Direction.SOUTH)) {
			maxZ++;
		}
		for (int z = minZ; z <= maxZ; z++) {
			if (tq.hasRoadBorder(Direction.WEST)) {
				genRoadBlock(tq.bb.min.getX() - 1, tq.bb.min.getY() - 1, z);
			}//west
			if (tq.hasRoadBorder(Direction.EAST)) {
				genRoadBlock(tq.bb.max.getX() + 1, tq.bb.min.getY() - 1, z);
			}//east
		}
		for (TownPartBlock tb : tq.blocks) {
			generateRoads(tb);
		}
	}

	/*
	 * Generates roads on the extends of a townblock
	 */
	private void generateRoads(TownPartBlock tb) {
		int minX = tb.bb.min.getX();
		int maxX = tb.bb.max.getX();
		if (tb.hasRoadBorder(Direction.WEST)) {
			minX--;
		}
		if (tb.hasRoadBorder(Direction.EAST)) {
			maxX++;
		}
		for (int x = minX; x <= maxX; x++) {
			if (tb.hasRoadBorder(Direction.NORTH)) {
				genRoadBlock(x, tb.bb.min.getY() - 1, tb.bb.min.getZ() - 1);
			}//north
			if (tb.hasRoadBorder(Direction.SOUTH)) {
				genRoadBlock(x, tb.bb.min.getY() - 1, tb.bb.max.getZ() + 1);
			}//south
		}
		int minZ = tb.bb.min.getZ();
		int maxZ = tb.bb.max.getZ();
		if (tb.hasRoadBorder(Direction.NORTH)) {
			minZ--;
		}
		if (tb.hasRoadBorder(Direction.SOUTH)) {
			maxZ++;
		}
		for (int z = minZ; z <= maxZ; z++) {
			if (tb.hasRoadBorder(Direction.WEST)) {
				genRoadBlock(tb.bb.min.getX() - 1, tb.bb.min.getY() - 1, z);
			}//west
			if (tb.hasRoadBorder(Direction.EAST)) {
				genRoadBlock(tb.bb.max.getX() + 1, tb.bb.min.getY() - 1, z);
			}//east
		}
	}

	/*
	 * generates roads running from the edges of the 'townBounds' to the edges of the 'wallBounds'
	 */
	private void generateAdditionalRoads() {
		int minX;
		int minZ;
		int maxX;
		int maxZ;
		int y;
		y = maximalBounds.min.getY() - 1;

		//northern road
		minX = maximalBounds.getCenterX() - 2;
		maxX = minX + 3;
		minZ = exteriorBounds.min.getZ();
		maxZ = townBounds.min.getZ() - 1;
		for (int x = minX; x <= maxX; x++) {
			for (int z = minZ; z <= maxZ; z++) {
				genRoadBlock(x, y, z);
			}
		}

		//eastern road
		minX = townBounds.max.getX() + 1;
		minZ = maximalBounds.getCenterZ() - 2;
		maxX = exteriorBounds.max.getX();
		maxZ = minZ + 3;
		for (int x = minX; x <= maxX; x++) {
			for (int z = minZ; z <= maxZ; z++) {
				genRoadBlock(x, y, z);
			}
		}

		//southern road
		minX = maximalBounds.getCenterX() - 2;
		minZ = townBounds.max.getZ() + 1;
		maxX = minX + 3;
		maxZ = exteriorBounds.max.getZ();
		for (int x = minX; x <= maxX; x++) {
			for (int z = minZ; z <= maxZ; z++) {
				genRoadBlock(x, y, z);
			}
		}

		//western road
		minX = exteriorBounds.min.getX();
		minZ = maximalBounds.getCenterZ() - 2;
		maxX = townBounds.min.getX() - 1;
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
		BlockPos pos = new BlockPos(x, y, z);
		world.setBlockState(pos, block.getStateFromMeta(meta), 3);
		world.setBlockState(pos.down(), Blocks.COBBLESTONE.getDefaultState(), 3);
	}

}
