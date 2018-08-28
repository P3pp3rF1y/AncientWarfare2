package net.shadowmage.ancientwarfare.structure.template.build.validation;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.shadowmage.ancientwarfare.automation.registry.TreeFarmRegistry;
import net.shadowmage.ancientwarfare.automation.tile.worksite.treefarm.ITree;
import net.shadowmage.ancientwarfare.automation.tile.worksite.treefarm.ITreeScanner;
import net.shadowmage.ancientwarfare.core.util.StringTools;
import net.shadowmage.ancientwarfare.structure.AncientWarfareStructure;
import net.shadowmage.ancientwarfare.structure.block.BlockDataManager;
import net.shadowmage.ancientwarfare.structure.config.AWStructureStatics;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplate;
import net.shadowmage.ancientwarfare.structure.template.build.StructureBB;
import net.shadowmage.ancientwarfare.structure.worldgen.WorldStructureGenerator;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.Predicate;

public abstract class StructureValidator {
	public static final String PROP_WORLD_GEN = "enableWorldGen";
	public static final String PROP_UNIQUE = "unique";
	public static final String PROP_SURVIVAL = "survival";
	public static final String PROP_PRESERVE_BLOCKS = "preserveBlocks";
	public static final String PROP_SELECTION_WEIGHT = "selectionWeight";
	public static final String PROP_CLUSTER_VALUE = "clusterValue";
	public static final String PROP_MIN_DUPLICATE_DISTANCE = "minDuplicateDistance";
	public static final String PROP_BORDER_SIZE = "borderSize";
	public static final String PROP_MAX_LEVELING = "maxLeveling";
	public static final String PROP_MAX_FILL = "maxFill";
	public static final String PROP_BIOME_WHITE_LIST = "biomeWhiteList";
	public static final String PROP_DIMENSION_WHITE_LIST = "dimensionWhiteList";
	public static final String PROP_BIOME_LIST = "biomeList";
	public static final String PROP_DIMENSION_LIST = "dimensionList";
	public static final String PROP_BLOCK_LIST = "blockList";
	public static final String PROP_BLOCK_SWAP = "blockSwap";

	public final StructureValidationType validationType;

	private HashMap<String, StructureValidationProperty> properties = new HashMap<>();

	protected StructureValidator(StructureValidationType validationType) {
		this.validationType = validationType;
		for (StructureValidationProperty property : validationType.getValidationProperties()) {
			this.properties.put(property.regName, property.copy());
		}
	}

	/*
	 * should be called from validator setup GUI when swapping between validator types,
	 * to transfer any comparable settings from the old one to the new one.<br>
	 * This method should be called on the NEW StructureValidator.
	 */
	public void inheritPropertiesFrom(StructureValidator validator) {
		StructureValidationProperty prop;
		for (String name : this.properties.keySet()) {
			if (validator.properties.containsKey(name)) {
				prop = validator.properties.get(name);
				this.properties.put(name, prop.copy());
			}
		}
	}

	protected void readFromLines(List<String> lines) {
	}

	protected void write(BufferedWriter writer) throws IOException {
	}

	/*
	 * helper method to read data from tag -- to be overriden by
	 * child-classes that have additional validation data set through gui
	 */
	public final void readFromNBT(NBTTagCompound tag) {
		for (StructureValidationProperty prop : this.properties.values()) {
			prop.readFromNBT(tag);
		}
	}

	public final NBTTagCompound serializeToNBT() {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setString("validationType", this.validationType.getName());
		for (StructureValidationProperty prop : this.properties.values()) {
			prop.writeToNBT(tag);
		}
		return tag;
	}

	protected void setDefaultSettings(StructureTemplate template) {
	}

	/*
	 * should this template be included for selection for generation? should only validate block placement, most other stuff has been checked (dimension/biome/cluster value/etc)
	 */
	public abstract boolean shouldIncludeForSelection(World world, int x, int y, int z, EnumFacing face, StructureTemplate template);

	/*
	 * if template should be included for selection, get the adjusted spawn Y level from the input block position.  this adjustedY will be used for validation and generation if template is selected and validated
	 */
	@SuppressWarnings("squid:S1172")
	public int getAdjustedSpawnY(World world, int x, int y, int z, EnumFacing face, StructureTemplate template, StructureBB bb) {
		return y;
	}

	/*
	 * if selected for placement, validate that placement. return false if placement is invalid
	 */
	public abstract boolean validatePlacement(World world, int x, int y, int z, EnumFacing face, StructureTemplate template, StructureBB bb);

	/*
	 * after validation, do any necessary clearing or leveling/etc
	 */
	public abstract void preGeneration(World world, BlockPos pos, EnumFacing face, StructureTemplate template, StructureBB bb);

	public void postGeneration(World world, BlockPos origin, StructureBB bb) {

	}

	/*
	 * called from StructureBuilder when constructed with world-gen settings whenever a '0' rule is detected
	 * in the template
	 * implementations should fill the input x,y,z with whatever block is an appropriate 'fill' for that
	 * validation type -- e.g. air or water
	 */
	@SuppressWarnings("squid:S1172")
	public void handleClearAction(World world, BlockPos pos, StructureTemplate template, StructureBB bb) {
		IBlockState state = world.getBlockState(pos);
		if (state.getMaterial() != Material.AIR) {
			ITreeScanner treeScanner = TreeFarmRegistry.getTreeScanner(state);
			if (!treeScanner.matches(state)) {
				world.setBlockToAir(pos);
				return;
			}
			ITree tree = treeScanner.scanTree(world, pos);
			tree.getLeafPositions().forEach(world::setBlockToAir);
			tree.getTrunkPositions().forEach(world::setBlockToAir);
		}
	}

	public static boolean startLow(String text, String test) {
		return text.toLowerCase(Locale.ENGLISH).startsWith(test);
	}

	public static StructureValidator parseValidator(List<String> lines) {
		String type = null;
		List<String> tagLines = new ArrayList<>();
		Iterator<String> it = lines.iterator();
		String line;
		boolean unique = false;
		boolean worldGen = false;
		boolean biome = false;
		boolean dimension = false;
		boolean blocks = false;
		boolean survival = false;
		boolean swap = false;
		int selectionWeight = 1;
		int clusterValue = 1;
		int duplicate = 1;
		int maxLeveling = 0;
		int maxFill = 0;
		int borderSize = 0;
		int[] dimensions = null;
		Set<String> biomes = new HashSet<>();
		Set<String> validTargetBlocks = new HashSet<>();

		while (it.hasNext() && (line = it.next()) != null) {
			if (startLow(line, "type=")) {
				type = StringTools.safeParseString("=", line);
			} else if (startLow(line, "unique=")) {
				unique = StringTools.safeParseBoolean("=", line);
			} else if (startLow(line, "survival=")) {
				survival = StringTools.safeParseBoolean("=", line);
			} else if (startLow(line, "worldgenenabled=")) {
				worldGen = StringTools.safeParseBoolean("=", line);
			} else if (startLow(line, "biomewhitelist=")) {
				biome = StringTools.safeParseBoolean("=", line);
			} else if (startLow(line, "dimensionwhitelist=")) {
				dimension = StringTools.safeParseBoolean("=", line);
				if (dimensions == null) {
					dimensions = new int[] {};
				}
			} else if (startLow(line, "preserveblocks=")) {
				blocks = StringTools.safeParseBoolean("=", line);
			} else if (startLow(line, "dimensionlist=")) {
				dimensions = StringTools.safeParseIntArray("=", line);
			} else if (startLow(line, "biomelist=")) {
				StringTools.safeParseStringsToSet(biomes, "=", line, true);
			} else if (startLow(line, "selectionweight=")) {
				selectionWeight = StringTools.safeParseInt("=", line);
			} else if (startLow(line, "clustervalue=")) {
				clusterValue = StringTools.safeParseInt("=", line);
			} else if (startLow(line, "minduplicatedistance=")) {
				duplicate = StringTools.safeParseInt("=", line);
			} else if (startLow(line, "leveling=")) {
				maxLeveling = StringTools.safeParseInt("=", line);
			} else if (startLow(line, "fill=")) {
				maxFill = StringTools.safeParseInt("=", line);
			} else if (startLow(line, "border=")) {
				borderSize = StringTools.safeParseInt("=", line);
			} else if (startLow(line, "validtargetblocks=")) {
				StringTools.safeParseStringsToSet(validTargetBlocks, "=", line, false);
			} else if (startLow(line, "blockswap=")) {
				swap = StringTools.safeParseBoolean("=", line);
			} else if (startLow(line, "data:")) {
				tagLines.add(line);
				while (it.hasNext() && (line = it.next()) != null) {
					tagLines.add(line);
					if (startLow(line, ":enddata")) {
						break;
					}
				}
			}
		}

		//defaulting templates to overworld dimension if no dimension list provided
		if (dimensions == null) {
			dimension = true;
			dimensions = new int[] {0};
		}

		StructureValidationType validatorType = StructureValidationType.getTypeFromName(type);
		StructureValidator validator;
		if (validatorType == null) {
			validator = StructureValidationType.GROUND.getValidator();
		} else {
			validator = validatorType.getValidator();
			validator.readFromLines(tagLines);
		}

		validator.setProperty(PROP_DIMENSION_WHITE_LIST, dimension);
		validator.setProperty(PROP_DIMENSION_LIST, dimensions);
		validator.setProperty(PROP_BIOME_WHITE_LIST, biome);
		validator.setProperty(PROP_BIOME_LIST, biomes);
		validator.setProperty(PROP_WORLD_GEN, worldGen);
		validator.setProperty(PROP_SURVIVAL, survival);
		validator.setProperty(PROP_UNIQUE, unique);
		validator.setProperty(PROP_PRESERVE_BLOCKS, blocks);
		validator.setProperty(PROP_CLUSTER_VALUE, clusterValue);
		validator.setProperty(PROP_SELECTION_WEIGHT, selectionWeight);
		validator.setProperty(PROP_MIN_DUPLICATE_DISTANCE, duplicate);
		validator.setProperty(PROP_MAX_FILL, maxFill);
		validator.setProperty(PROP_MAX_LEVELING, maxLeveling);
		validator.setProperty(PROP_BORDER_SIZE, borderSize);
		validator.setProperty(PROP_BLOCK_LIST, validTargetBlocks);
		validator.setProperty(PROP_BLOCK_SWAP, swap);
		return validator;
	}

	public static void writeValidator(BufferedWriter out, StructureValidator validator) throws IOException {
		out.write("type=" + validator.validationType.getName());
		out.newLine();
		out.write("survival=" + validator.isSurvival());
		out.newLine();
		out.write("worldGenEnabled=" + validator.isWorldGenEnabled());
		out.newLine();
		out.write("unique=" + validator.isUnique());
		out.newLine();
		out.write("preserveBlocks=" + validator.isPreserveBlocks());
		out.newLine();
		out.write("selectionWeight=" + validator.getSelectionWeight());
		out.newLine();
		out.write("clusterValue=" + validator.getClusterValue());
		out.newLine();
		out.write("minDuplicateDistance=" + validator.getMinDuplicateDistance());
		out.newLine();
		out.write("dimensionWhiteList=" + validator.isDimensionWhiteList());
		out.newLine();
		out.write("dimensionList=" + StringTools.getCSVStringForArray(validator.getAcceptedDimensions()));
		out.newLine();
		out.write("biomeWhiteList=" + validator.isBiomeWhiteList());
		out.newLine();
		out.write("biomeList=" + StringTools.getCSVValueFor(validator.getBiomeList().toArray(new String[validator.getBiomeList().size()])));
		out.newLine();
		out.write("leveling=" + validator.getMaxLeveling());
		out.newLine();
		out.write("fill=" + validator.getMaxFill());
		out.newLine();
		out.write("border=" + validator.getBorderSize());
		out.newLine();
		out.write(StructureValidator.PROP_BLOCK_SWAP + "=" + validator.isBlockSwap());
		out.newLine();
		out.write("data:");
		out.newLine();
		validator.write(out);
		out.write(":enddata");
		out.newLine();
	}

	public final StructureValidator setDefaults(StructureTemplate template) {
		setDefaultSettings(template);
		return this;
	}

	private void setProperty(String name, Object value) {
		if (properties.containsKey(name)) {
			properties.get(name).setValue(value);
		} else {
			throw new IllegalArgumentException("Validation properties does not contain key for: " + name);
		}
	}

	public final void setBiomeWhiteList(boolean val) {
		properties.get(PROP_BIOME_WHITE_LIST).setValue(val);
	}

	public final void setDimensionWhiteList(boolean val) {
		properties.get(PROP_DIMENSION_WHITE_LIST).setValue(val);
	}

	public final boolean isBlockSwap() {
		return properties.get(PROP_BLOCK_SWAP).getDataBoolean();
	}

	public final int getSelectionWeight() {
		return properties.get(PROP_SELECTION_WEIGHT).getDataInt();
	}

	public final int getClusterValue() {
		return properties.get(PROP_CLUSTER_VALUE).getDataInt();
	}

	public final boolean isWorldGenEnabled() {
		return properties.get(PROP_WORLD_GEN).getDataBoolean();
	}

	public final boolean isPreserveBlocks() {
		return properties.get(PROP_PRESERVE_BLOCKS).getDataBoolean();
	}

	public final boolean isBiomeWhiteList() {
		return properties.get(PROP_BIOME_WHITE_LIST).getDataBoolean();
	}

	public final boolean isUnique() {
		return properties.get(PROP_UNIQUE).getDataBoolean();
	}

	public final boolean isDimensionWhiteList() {
		return properties.get(PROP_DIMENSION_WHITE_LIST).getDataBoolean();
	}

	public final int[] getAcceptedDimensions() {
		return properties.get(PROP_DIMENSION_LIST).getDataIntArray();
	}

	public final void setValidDimension(Set<Integer> dims) {
		int[] dimsa = new int[dims.size()];
		int index = 0;
		for (Integer dim : dims) {
			dimsa[index] = dim;
			index++;
		}
		properties.get(PROP_DIMENSION_LIST).setValue(dimsa);
	}

	public final int getMinDuplicateDistance() {
		return properties.get(PROP_MIN_DUPLICATE_DISTANCE).getDataInt();
	}

	public final void setBiomeList(Collection<String> biomes) {
		Set<String> blocks = new HashSet<>();
		blocks.addAll(biomes);
		properties.get(PROP_BIOME_LIST).setValue(blocks);
	}

	public Set<String> getBiomeList() {
		return properties.get(PROP_BIOME_LIST).getDataStringSet();
	}

	public int getMaxFill() {
		return properties.get(PROP_MAX_FILL).getDataInt();
	}

	public int getMaxLeveling() {
		return properties.get(PROP_MAX_LEVELING).getDataInt();
	}

	public int getBorderSize() {
		return properties.get(PROP_BORDER_SIZE).getDataInt();
	}

	//*********************************************** UTILITY METHODS *************************************************//
	protected boolean validateBorderBlocks(World world, StructureBB bb, int minY, int maxY, boolean skipWater) {
		int bx;
		int bz;
		int borderSize = getBorderSize();
		boolean riverBiomeValid = BiomeDictionary.hasType(world.provider.getBiomeForCoords(new BlockPos(bb.min.getX(), 1, bb.min.getZ())), BiomeDictionary.Type.RIVER);
		for (bx = bb.min.getX() - borderSize; bx <= bb.max.getX() + borderSize; bx++) {
			bz = bb.min.getZ() - borderSize;
			if (!validateBlockHeightTypeAndBiome(world, bx, bz, minY, maxY, skipWater, riverBiomeValid)) {
				return false;
			}
			bz = bb.max.getZ() + borderSize;
			if (!validateBlockHeightTypeAndBiome(world, bx, bz, minY, maxY, skipWater, riverBiomeValid)) {
				return false;
			}
		}
		for (bz = bb.min.getZ() - borderSize + 1; bz <= bb.max.getZ() + borderSize - 1; bz++) {
			bx = bb.min.getX() - borderSize;
			if (!validateBlockHeightTypeAndBiome(world, bx, bz, minY, maxY, skipWater, riverBiomeValid)) {
				return false;
			}
			bx = bb.max.getX() + borderSize;
			if (!validateBlockHeightTypeAndBiome(world, bx, bz, minY, maxY, skipWater, riverBiomeValid)) {
				return false;
			}
		}
		return true;
	}

	/*
	 * validates both top block height and block type for the input position and settings
	 */
	protected boolean validateBlockHeightAndType(World world, int x, int z, int min, int max, boolean skipWater, Predicate<IBlockState> isValidState) {
		return validateBlockType(world, x, validateBlockHeight(world, x, z, min, max, skipWater), z, isValidState);
	}

	private boolean validateBlockHeightTypeAndBiome(World world, int x, int z, int min, int max, boolean skipWater, boolean riverBiomeValid, Predicate<IBlockState> isValidState) {
		BlockPos pos = new BlockPos(x, 1, z);
		if (!riverBiomeValid && BiomeDictionary.hasType(world.provider.getBiomeForCoords(pos), BiomeDictionary.Type.RIVER)) {
			AncientWarfareStructure.LOG.debug("Rejected for placement into river biome at {}", pos.toString());
			return false;
		}

		return validateBlockHeightAndType(world, x, z, min, max, skipWater, isValidState);
	}

	protected boolean validateBlockHeightTypeAndBiome(World world, int x, int z, int min, int max, boolean skipWater, boolean riverBiomeValid) {
		return validateBlockHeightTypeAndBiome(world, x, z, min, max, skipWater, riverBiomeValid, AWStructureStatics::isValidTargetBlock);
	}

	/*
	 * validates top block height at X, Z is >=  min and <= max (inclusive)
	 * returns topFoundY or -1 if not within range
	 */
	private int validateBlockHeight(World world, int x, int z, int minimumAcceptableY, int maximumAcceptableY, boolean skipWater) {
		int topFilledY = WorldStructureGenerator.getTargetY(world, x, z, skipWater);
		if (topFilledY < minimumAcceptableY || topFilledY > maximumAcceptableY) {
			AncientWarfareStructure.LOG.info("rejected for leveling or depth test. foundY: " + topFilledY + " min: " + minimumAcceptableY + " max:" + maximumAcceptableY + " at: " + x + "," + topFilledY + "," + z);
			return -1;
		}
		return topFilledY;
	}

	/*
	 * validates the target block at x,y,z is one of the input valid blocks
	 */
	private boolean validateBlockType(World world, int x, int y, int z, Predicate<IBlockState> isValidState) {
		if (y < 0 || y >= world.getHeight()) {
			return false;
		}
		IBlockState state = world.getBlockState(new BlockPos(x, y, z));
		Block block = state.getBlock();
		if (block == Blocks.AIR) {
			AncientWarfareStructure.LOG.info("rejected for non-matching block: air" + " at: " + x + "," + y + "," + z);
			return false;
		}
		if (!isValidState.test(state)) {
			AncientWarfareStructure.LOG.info("Rejected for non-matching block: " + BlockDataManager.INSTANCE.getNameForBlock(block) + " at: " + x + "," + y + "," + z);
			return false;
		}
		return true;
	}

	/*
	 * return the lowest acceptable Y level for a filled block
	 * for the input template and BB
	 */
	protected int getMinY(StructureTemplate template, StructureBB bb) {
		int minY = bb.min.getY() - getMaxFill() - 1;
		if (getBorderSize() > 0) {
			minY += template.getOffset().getY();
		}
		return minY;
	}

	/*
	 * return the highest acceptable Y level for a filled block
	 * for the input template and BB
	 */
	protected int getMaxY(StructureTemplate template, StructureBB bb) {
		return bb.min.getY() + template.getOffset().getY() + getMaxLeveling();
	}

	private int getMaxFillY(StructureTemplate template, StructureBB bb) {
		return getMinY(template, bb) + getMaxFill();
	}

	protected void borderLeveling(World world, int x, int z, StructureTemplate template, StructureBB bb) {
		if (getMaxLeveling() <= 0) {
			return;
		}
		int topFilledY = WorldStructureGenerator.getTargetY(world, x, z, true);
		int step = WorldStructureGenerator.getStepNumber(x, z, bb.min.getX(), bb.max.getX(), bb.min.getZ(), bb.max.getZ());
		for (int y = bb.min.getY() + template.getOffset().getY() + step; y <= topFilledY; y++) {
			handleClearAction(world, new BlockPos(x, y, z), template, bb);
		}
		Biome biome = world.provider.getBiomeForCoords(new BlockPos(x, 1, z));
		IBlockState fillBlock = biome.topBlock;
		int y = bb.min.getY() + template.getOffset().getY() + step - 1;
		BlockPos pos = new BlockPos(x, y, z);
		IBlockState state = world.getBlockState(pos);
		Block block = state.getBlock();
		if (block != Blocks.AIR && state.getMaterial() != Material.WATER && !AWStructureStatics.isSkippable(state)) {
			world.setBlockState(pos, fillBlock);
		}
	}

	private void borderFill(World world, int x, int z, StructureTemplate template, StructureBB bb) {
		if (getMaxFill() <= 0) {
			return;
		}
		int maxFillY = getMaxFillY(template, bb);
		int step = WorldStructureGenerator.getStepNumber(x, z, bb.min.getX(), bb.max.getX(), bb.min.getZ(), bb.max.getZ());
		maxFillY -= step;
		Biome biome = world.provider.getBiomeForCoords(new BlockPos(x, 1, z));
		IBlockState fillBlock = biome.topBlock;
		for (int y = maxFillY; y > 1; y--) {
			BlockPos pos = new BlockPos(x, y, z);
			IBlockState state = world.getBlockState(pos);
			Block block = state.getBlock();
			if (AWStructureStatics.isSkippable(state) || block == Blocks.WATER || block == Blocks.FLOWING_WATER) {
				world.setBlockState(pos, fillBlock);
			}
		}
	}

	private void underFill(World world, int x, int z, StructureBB bb) {
		int topFilledY = WorldStructureGenerator.getTargetY(world, x, z, true);
		Biome biome = world.provider.getBiomeForCoords(new BlockPos(x, 1, z));
		IBlockState fillBlock = biome.topBlock;
		for (int y = topFilledY; y <= bb.min.getY() - 1; y++) {
			world.setBlockState(new BlockPos(x, y, z), fillBlock);
		}
	}

	protected void prePlacementUnderfill(World world, StructureBB bb) {
		if (getMaxFill() <= 0) {
			return;
		}
		int bx;
		int bz;
		for (bx = bb.min.getX(); bx <= bb.max.getX(); bx++) {
			for (bz = bb.min.getZ(); bz <= bb.max.getZ(); bz++) {
				underFill(world, bx, bz, bb);
			}
		}
	}

	protected void prePlacementBorder(World world, StructureTemplate template, StructureBB bb) {
		int borderSize = getBorderSize();
		if (borderSize <= 0) {
			return;
		}
		int bx;
		int bz;
		for (bx = bb.min.getX() - borderSize; bx <= bb.max.getX() + borderSize; bx++) {
			for (bz = bb.max.getZ() + borderSize; bz > bb.max.getZ(); bz--) {
				borderLeveling(world, bx, bz, template, bb);
				borderFill(world, bx, bz, template, bb);
			}
			for (bz = bb.min.getZ() - borderSize; bz < bb.min.getZ(); bz++) {
				borderLeveling(world, bx, bz, template, bb);
				borderFill(world, bx, bz, template, bb);
			}
		}
		for (bz = bb.min.getZ(); bz <= bb.max.getZ(); bz++) {
			for (bx = bb.min.getX() - borderSize; bx < bb.min.getX(); bx++) {
				borderLeveling(world, bx, bz, template, bb);
				borderFill(world, bx, bz, template, bb);
			}
			for (bx = bb.max.getX() + borderSize; bx > bb.max.getX(); bx--) {
				borderLeveling(world, bx, bz, template, bb);
				borderFill(world, bx, bz, template, bb);
			}
		}
	}

	public Collection<StructureValidationProperty> getProperties() {
		return properties.values();
	}

	public boolean isSurvival() {
		return properties.get(PROP_SURVIVAL).getDataBoolean();
	}

}
