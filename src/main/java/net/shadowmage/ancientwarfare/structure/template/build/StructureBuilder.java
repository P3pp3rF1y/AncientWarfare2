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
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeDesert;
import net.minecraft.world.biome.BiomeSavanna;
import net.minecraft.world.biome.BiomeTaiga;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.BiomeEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.structure.api.IStructureBuilder;
import net.shadowmage.ancientwarfare.structure.api.TemplateRule;
import net.shadowmage.ancientwarfare.structure.api.TemplateRuleEntity;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplate;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

public class StructureBuilder implements IStructureBuilder {

	protected StructureTemplate template;
	protected World world;
	protected BlockPos buildOrigin;
	protected EnumFacing buildFace;
	protected int turns;
	protected int maxPriority = 4;
	protected int currentPriority;//current build priority...may not be needed anymore?
	protected int currentX, currentY, currentZ;//coords in template
	private Vec3i destSize;
	protected BlockPos destination;

	protected StructureBB bb;

	private boolean isFinished = false;
	private Biome biome;

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
		currentX = currentY = currentZ = 0;
		destSize = template.size;
		currentPriority = 0;

		turns = ((face.getHorizontalIndex() + 2) % 4);
		for (int i = 0; i < turns; i++) {
			destSize = new Vec3i(destSize.getZ(), destSize.getY(), destSize.getX());
		}
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
		try {
			while (!this.isFinished()) {
				Optional<TemplateRule> rule = template.getRuleAt(currentX, currentY, currentZ);
				if (rule.isPresent()) {
					placeCurrentPosition(rule.get());
				} else if (currentPriority == 0) {
					placeAir();
				}
				increment();
			}
		}
		catch (Exception e) {
			//TODO do we really need exception handling here?
			throw new RuntimeException("Caught exception while constructing template blocks: " + template.getRuleAt(currentX, currentY, currentZ).orElse(null), e);
		}
		this.placeEntities();
	}

	protected void placeEntities() {
		TemplateRuleEntity[] rules = template.getEntityRules();
		for (TemplateRuleEntity rule : rules) {
			if (rule == null) {
				continue;
			}
			destination = BlockTools.rotateInArea(rule.getPosition(), template.getSize().getX(), template.getSize().getZ(), turns).add(bb.min);
			try {
				rule.handlePlacement(world, turns, destination, this);
			}
			catch (StructureBuildingException e) {
				e.printStackTrace();
			}
		}
	}

	/*
	 * should be called by template-rules to handle block-placement in the world.
	 * Handles village-block swapping during world-gen, and chunk-insert for blocks
	 * with priority > 0
	 */
	@Override
	public void placeBlock(BlockPos pos, IBlockState state, int priority) {
		if (pos.getY() <= 0 || pos.getY() >= world.getHeight()) {
			return;
		}

		IBlockState adjustedState = state;
		if (template.getValidationSettings().isBlockSwap()) {
			adjustedState = getBiomeSpecificBlockState(biome, state);
		}

		int updateFlag = state.canProvidePower() ? 3 : 2;
		world.setBlockState(pos, adjustedState, updateFlag);
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

	protected void placeAir() {
		if (!template.getValidationSettings().isPreserveBlocks()) {
			template.getValidationSettings().handleClearAction(world, destination, template, bb);
		}
	}

	protected void placeRule(TemplateRule rule) {
		if (destination.getY() <= 0) {
			return;
		}
		try {
			rule.handlePlacement(world, turns, destination, this);
		}
		catch (StructureBuildingException e) {
			e.printStackTrace();
		}
	}

	protected void incrementDestination() {
		destination = BlockTools.rotateInArea(new BlockPos(currentX, currentY, currentZ), template.getSize().getX(), template.getSize().getZ(), turns).add(bb.min);
	}

	/*
	 * return true if could increment position
	 * return false if template is finished
	 */
	protected boolean incrementPosition() {
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
		return true;
	}

	public boolean isFinished() {
		return isFinished;
	}

	public float getPercentDoneWithPass() {
		float max = template.getSize().getX() * template.getSize().getZ() * template.getSize().getY();
		float current = currentY * (template.getSize().getX() * template.getSize().getZ());//add layers done
		current += currentZ * template.getSize().getX();//add rows done
		current += currentX;//add blocks done
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

	// @formatter:on

	private interface IBlockSwapMapping {
		boolean matches(Block block);

		IBlockState swap(IBlockState state);
	}

	private static class BlockSwapMapping implements IBlockSwapMapping {
		private final Predicate<Block> blockMatcher;
		private final Function<IBlockState, IBlockState> doSwap;

		public BlockSwapMapping(Predicate<Block> blockMatcher, Function<IBlockState, IBlockState> doSwap) {
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
