package net.shadowmage.ancientwarfare.structure.template.build;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLog;
import net.minecraft.block.BlockNewLog;
import net.minecraft.block.BlockOldLog;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockSandStone;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeDesert;
import net.minecraft.world.biome.BiomeSavanna;
import net.minecraft.world.biome.BiomeTaiga;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.BiomeEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.core.util.WorldTools;
import net.shadowmage.ancientwarfare.structure.api.IStructureBuilder;
import net.shadowmage.ancientwarfare.structure.api.TemplateRule;
import net.shadowmage.ancientwarfare.structure.api.TemplateRuleBlock;
import net.shadowmage.ancientwarfare.structure.api.TemplateRuleEntityBase;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

import static net.shadowmage.ancientwarfare.structure.template.build.validation.properties.StructureValidationProperties.BIOME_REPLACEMENT;

public class StructureBuilder implements IStructureBuilder {

	protected StructureTemplate template;
	protected World world;
	BlockPos buildOrigin;
	EnumFacing buildFace;
	protected int turns;
	int maxPriority = 3;
	int currentPriority;//current build priority...may not be needed anymore?
	Vec3i curTempPos;
	BlockPos destination;

	protected StructureBB bb;

	private boolean isFinished = false;
	private Biome biome;
	private Map<BlockPos, IBlockState> statesToSetAgain = new HashMap<>();
	private Map<BlockPos, IBlockState> positionsToUpdate = new HashMap<>();

	public StructureBuilder(World world, StructureTemplate template, EnumFacing face, BlockPos pos) {
		this(world, template, face, pos, new StructureBB(pos, face, template));
	}

	public StructureBuilder(World world, StructureTemplate template, EnumFacing face, BlockPos buildKey, StructureBB bb) {
		this.world = world;
		biome = world.provider.getBiomeForCoords(buildKey);
		this.template = template;
		this.buildFace = face;
		this.bb = bb;
		buildOrigin = buildKey;
		destination = BlockPos.ORIGIN;
		curTempPos = Vec3i.NULL_VECTOR;
		currentPriority = 0;

		turns = ((face.getHorizontalIndex() + 2) % 4);
		/*
		 * initialize the first target destination so that the structure is ready to start building when called on to build
		 */
		incrementDestination();
	}

	public StructureTemplate getTemplate() {
		return template;
	}

	public StructureBB getBoundingBox() {
		return bb;
	}

	protected StructureBuilder() {
		destination = BlockPos.ORIGIN;
		buildOrigin = BlockPos.ORIGIN;
	}

	public void instantConstruction() {
		while (!this.isFinished()) {
			Optional<TemplateRuleBlock> rule = template.getRuleAt(curTempPos);
			if (rule.isPresent()) {
				placeCurrentPosition(rule.get());
			} else if (currentPriority == 0) {
				placeAir();
			}
			increment();
		}
		setStateAgainForSpecialBlocks();
		updateNeighbors();
		changeBiome();

		this.placeEntities();
	}

	private void changeBiome() {
		ResourceLocation biomeRegistryName = template.getValidationSettings().getPropertyValue(BIOME_REPLACEMENT);
		if (!ForgeRegistries.BIOMES.containsKey(biomeRegistryName)) {
			return;
		}

		Biome replacementBiome = ForgeRegistries.BIOMES.getValue(biomeRegistryName);

		BlockPos minPos = bb.min;
		BlockPos maxPos = new BlockPos(bb.max.getX(), bb.min.getY(), bb.max.getZ());
		BlockPos.getAllInBox(minPos, maxPos).forEach(pos -> {
			if (isTopBlockSolid(world, pos)) {
				//noinspection ConstantConditions
				WorldTools.changeBiome(world, pos, replacementBiome);
			}
		});
	}

	private boolean isTopBlockSolid(World world, BlockPos pos) {
		Chunk chunk = world.getChunkFromBlockCoords(pos);
		BlockPos posDown;
		for (BlockPos currentPos = new BlockPos(pos.getX(), chunk.getTopFilledSegment() + 16, pos.getZ()); currentPos.getY() >= 0; currentPos = posDown) {
			posDown = currentPos.down();
			IBlockState state = chunk.getBlockState(posDown);

			if (state.getMaterial() != Material.AIR && !state.getBlock().isLeaves(state, world, posDown) && !state.getBlock().isFoliage(world, posDown)) {
				return !state.getMaterial().isLiquid();
			}
		}
		return false;
	}

	private void updateNeighbors() {
		for (Map.Entry<BlockPos, IBlockState> entry : positionsToUpdate.entrySet()) {
			world.notifyNeighborsRespectDebug(entry.getKey(), entry.getValue().getBlock(), true);

			if (entry.getValue().hasComparatorInputOverride()) {
				world.updateComparatorOutputLevel(entry.getKey(), entry.getValue().getBlock());
			}
			//schedule update spread in the next 40 ticks in case we have a lot of redstone somewhere
			world.scheduleUpdate(entry.getKey(), entry.getValue().getBlock(), world.rand.nextInt(40));
		}
	}

	private void setStateAgainForSpecialBlocks() {
		for (Map.Entry<BlockPos, IBlockState> entry : statesToSetAgain.entrySet()) {
			world.setBlockState(entry.getKey(), entry.getValue(), 2);
		}
	}

	private void placeEntities() {
		for (TemplateRuleEntityBase rule : template.getEntityRules().values()) {
			destination = BlockTools.rotateInArea(rule.getPosition(), template.getSize().getX(), template.getSize().getZ(), turns).add(bb.min);
			rule.handlePlacement(world, turns, destination, this);
		}
	}

	/*
	 * should be called by template-rules to handle block-placement in the world.
	 * Handles village-block swapping during world-gen, and chunk-insert for blocks
	 * with priority > 0
	 */
	@Override
	public boolean placeBlock(BlockPos pos, IBlockState state, int priority) {
		if (pos.getY() <= 0 || pos.getY() >= world.getHeight()) {
			return false;
		}

		IBlockState adjustedState = state;
		if (template.getValidationSettings().isBlockSwap()) {
			adjustedState = getBiomeSpecificBlockState(biome, state);
		}

		boolean result = world.setBlockState(pos, adjustedState, 2);
		if (result) {
			if (DOUBLE_SET_BLOCKS.contains(adjustedState.getBlock())) {
				statesToSetAgain.put(pos, adjustedState);
			}
			if (state.canProvidePower()) {
				positionsToUpdate.put(pos, adjustedState);
			}
		}

		return result;
	}

	private void placeCurrentPosition(TemplateRule rule) {
		if (rule.shouldPlaceOnBuildPass(world, turns, destination, currentPriority)) {
			this.placeRule(rule);
		}
	}

	protected boolean increment() {
		if (isFinished) {
			return false;
		}
		if (incrementPosition()) {
			incrementDestination();
		} else {
			this.isFinished = true;
		}
		return !isFinished;
	}

	private void placeAir() {
		if (!template.getValidationSettings().isPreserveBlocks()) {
			world.setBlockToAir(destination);
		}
	}

	void placeRule(TemplateRule rule) {
		if (destination.getY() <= 0) {
			return;
		}
		rule.handlePlacement(world, turns, destination, this);
	}

	void incrementDestination() {
		destination = BlockTools.rotateInArea(new BlockPos(curTempPos), template.getSize().getX(), template.getSize().getZ(), turns).add(bb.min);
	}

	/*
	 * return true if could increment position
	 * return false if template is finished
	 */
	private boolean incrementPosition() {
		int currentX = curTempPos.getX();
		int currentY = curTempPos.getY();
		int currentZ = curTempPos.getZ();
		currentX++;
		if (currentX >= template.getSize().getX()) {
			currentX = 0;
			currentZ++;
			if (currentZ >= template.getSize().getZ()) {
				currentZ = 0;
				currentY++;
				if (currentY >= template.getSize().getY()) {
					currentY = 0;
					currentPriority++;
					if (currentPriority > maxPriority) {
						currentPriority = 0;
						return false;
					}
				}
			}
		}
		curTempPos = new Vec3i(currentX, currentY, currentZ);
		return true;
	}

	public boolean isFinished() {
		return isFinished;
	}

	float getTotalBlocks() {
		return (float) template.getSize().getX() * template.getSize().getZ() * template.getSize().getY();
	}

	public float getPercentDoneWithPass() {
		float max = getTotalBlocks();
		float current = (float) curTempPos.getY() * (template.getSize().getX() * template.getSize().getZ());//add layers done
		current += curTempPos.getZ() * template.getSize().getX();//add rows done
		current += curTempPos.getX();//add blocks done
		return current / max;
	}

	public int getPass() {
		return currentPriority;
	}

	public int getMaxPasses() {
		return maxPriority;
	}

	private IBlockState getBiomeSpecificBlockState(Biome biome, IBlockState originalBlockState) {
		BiomeEvent.GetVillageBlockID event = new BiomeEvent.GetVillageBlockID(biome, originalBlockState);
		MinecraftForge.TERRAIN_GEN_BUS.post(event);

		if (event.getResult() == Event.Result.DENY)
			return event.getReplacement();

		for (Map.Entry<Class, Set<IBlockSwapMapping>> entry : BIOME_SWAP_STATES.entrySet()) {
			if (entry.getKey().isInstance(biome)) {
				for (IBlockSwapMapping mapping : entry.getValue()) {
					if (mapping.matches(originalBlockState.getBlock())) {
						return mapping.swap(originalBlockState);
					}
				}
			}
		}

		return originalBlockState;
	}

	private static final Set<Block> DOUBLE_SET_BLOCKS = ImmutableSet.of(Blocks.RAIL, Blocks.ACTIVATOR_RAIL, Blocks.DETECTOR_RAIL, Blocks.GOLDEN_RAIL);

	// @formatter:off
	private static final Map<Class, Set<IBlockSwapMapping>> BIOME_SWAP_STATES = ImmutableMap.of(
			BiomeDesert.class, ImmutableSet.of(
					new BlockSwapMapping(b -> b == Blocks.LOG || b == Blocks.LOG2, s -> Blocks.SANDSTONE.getDefaultState()),
					new BlockSwapMapping(b -> b == Blocks.COBBLESTONE, s -> Blocks.SANDSTONE.getDefaultState()),
					new BlockSwapMapping(b -> b == Blocks.PLANKS,
							s -> Blocks.SANDSTONE.getDefaultState().withProperty(BlockSandStone.TYPE, BlockSandStone.EnumType.SMOOTH)),
					new BlockSwapMapping(b -> b == Blocks.OAK_STAIRS,
							s -> Blocks.SANDSTONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, s.getValue(BlockStairs.FACING))),
					new BlockSwapMapping(b -> b == Blocks.STONE_STAIRS,
							s -> Blocks.SANDSTONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, s.getValue(BlockStairs.FACING))),
					new BlockSwapMapping(b -> b == Blocks.GRAVEL, s -> Blocks.SANDSTONE.getDefaultState())),
			BiomeTaiga.class, ImmutableSet.of(
					new BlockSwapMapping(b -> b == Blocks.LOG || b == Blocks.LOG2,
							s -> Blocks.LOG.getDefaultState().withProperty(BlockOldLog.VARIANT, BlockPlanks.EnumType.SPRUCE)
									.withProperty(BlockLog.LOG_AXIS, s.getValue(BlockLog.LOG_AXIS))),
					new BlockSwapMapping(b -> b == Blocks.PLANKS,
							s -> Blocks.PLANKS.getDefaultState().withProperty(BlockPlanks.VARIANT, BlockPlanks.EnumType.SPRUCE)),
					new BlockSwapMapping(b -> b == Blocks.OAK_STAIRS,
							s -> Blocks.SPRUCE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, s.getValue(BlockStairs.FACING))),
					new BlockSwapMapping(b -> b == Blocks.OAK_FENCE, s -> Blocks.SPRUCE_FENCE.getDefaultState())),
			BiomeSavanna.class, ImmutableSet.of(
					new BlockSwapMapping(b -> b == Blocks.LOG || b == Blocks.LOG2,
							s -> Blocks.LOG2.getDefaultState().withProperty(BlockNewLog.VARIANT, BlockPlanks.EnumType.ACACIA)
									.withProperty(BlockLog.LOG_AXIS, s.getValue(BlockLog.LOG_AXIS))),
					new BlockSwapMapping(b -> b == Blocks.PLANKS,
							s -> Blocks.PLANKS.getDefaultState().withProperty(BlockPlanks.VARIANT, BlockPlanks.EnumType.ACACIA)),
					new BlockSwapMapping(b -> b == Blocks.OAK_STAIRS,
							s -> Blocks.ACACIA_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, s.getValue(BlockStairs.FACING))),
					new BlockSwapMapping(b -> b == Blocks.COBBLESTONE, s -> Blocks.LOG2.getDefaultState().withProperty(BlockNewLog.VARIANT, BlockPlanks.EnumType.ACACIA)
							.withProperty(BlockLog.LOG_AXIS, BlockLog.EnumAxis.Y)),
					new BlockSwapMapping(b -> b == Blocks.OAK_FENCE, s -> Blocks.ACACIA_FENCE.getDefaultState()))
	);

	public EnumFacing getBuildFace() {
		return buildFace;
	}

	// @formatter:on

	private interface IBlockSwapMapping {
		boolean matches(Block block);

		IBlockState swap(IBlockState state);
	}

	private static class BlockSwapMapping implements IBlockSwapMapping {
		private final Predicate<Block> blockMatcher;
		private final Function<IBlockState, IBlockState> doSwap;

		private BlockSwapMapping(Predicate<Block> blockMatcher, Function<IBlockState, IBlockState> doSwap) {
			this.blockMatcher = blockMatcher;
			this.doSwap = doSwap;
		}

		@Override
		public boolean matches(Block block) {
			return blockMatcher.test(block);
		}

		@Override
		public IBlockState swap(IBlockState state) {
			return doSwap.apply(state);
		}
	}
}
