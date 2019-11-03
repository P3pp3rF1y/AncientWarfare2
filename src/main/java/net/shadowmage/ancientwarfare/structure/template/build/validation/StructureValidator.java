package net.shadowmage.ancientwarfare.structure.template.build.validation;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.shadowmage.ancientwarfare.automation.registry.TreeFarmRegistry;
import net.shadowmage.ancientwarfare.automation.tile.worksite.treefarm.ITree;
import net.shadowmage.ancientwarfare.automation.tile.worksite.treefarm.ITreeScanner;
import net.shadowmage.ancientwarfare.structure.AncientWarfareStructure;
import net.shadowmage.ancientwarfare.structure.config.AWStructureStatics;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplate;
import net.shadowmage.ancientwarfare.structure.template.build.StructureBB;
import net.shadowmage.ancientwarfare.structure.template.build.validation.properties.IStructureValidationProperty;
import net.shadowmage.ancientwarfare.structure.worldgen.WorldStructureGenerator;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.shadowmage.ancientwarfare.structure.template.build.validation.properties.StructureValidationProperties.*;

public abstract class StructureValidator {
	public final StructureValidationType validationType;

	private boolean riverBiomeChecked = false;
	private boolean canSpawnInRiverBiome = false;

	private HashMap<IStructureValidationProperty<?>, Object> properties = new LinkedHashMap<>();

	protected StructureValidator(StructureValidationType validationType) {
		this.validationType = validationType;
		for (IStructureValidationProperty property : validationType.getValidationProperties()) {
			properties.put(property, property.getDefaultValue());
		}
	}

	/*
	 * should be called from validator setup GUI when swapping between validator types,
	 * to transfer any comparable settings from the old one to the new one.<br>
	 * This method should be called on the NEW StructureValidator.
	 */
	public void inheritPropertiesFrom(StructureValidator validator) {
		for (IStructureValidationProperty<?> property : this.properties.keySet()) {
			if (validator.properties.containsKey(property)) {
				properties.put(property, validator.properties.get(property));
			}
		}
	}

	/*
	 * helper method to read data from tag -- to be overriden by
	 * child-classes that have additional validation data set through gui
	 */
	public final void readFromNBT(NBTTagCompound tag) {
		for (Map.Entry<IStructureValidationProperty<?>, Object> entry : this.properties.entrySet()) {
			entry.setValue(entry.getKey().deserializeNBT(tag));
		}
	}

	public final NBTTagCompound serializeToNBT() {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setString("validationType", this.validationType.getName());
		for (Map.Entry<IStructureValidationProperty<?>, Object> entry : this.properties.entrySet()) {
			serializePropertyNBT(tag, entry.getKey(), entry.getValue());
		}
		return tag;
	}

	private <T> void serializePropertyNBT(NBTTagCompound tag, IStructureValidationProperty<T> property, Object value) {
		property.serializeNBT(tag, property.getValueClass().cast(value));
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

	public void postGeneration(World world, BlockPos origin, StructureBB bb, StructureTemplate template) {

	}

	/*
	 * called from StructureBuilder when constructed with world-gen settings whenever a '0' rule is detected
	 * in the template
	 * implementations should fill the input x,y,z with whatever block is an appropriate 'fill' for that
	 * validation type -- e.g. air or water
	 */
	@SuppressWarnings("squid:S1172")
	public void handleClearAction(World world, BlockPos pos, StructureTemplate template, StructureBB bb) {
		if (isPreserveBlocks()) {
			return;
		}

		IBlockState state = world.getBlockState(pos);
		if (state.getMaterial() != Material.AIR) {
			if (state.getMaterial() == Material.WOOD) {
				Optional<ITreeScanner> treeScanner = TreeFarmRegistry.getRegisteredTreeScanner(state);
				if (treeScanner.isPresent()) {
					ITree tree = treeScanner.get().scanTree(world, pos);
					tree.getLeafPositions().forEach(world::setBlockToAir);
					tree.getTrunkPositions().forEach(world::setBlockToAir);
					return;
				}
			}
			world.setBlockToAir(pos);
		}
	}

	public static StructureValidator parseValidator(List<String> lines) {
		StructureValidationType type = StructureValidationType.GROUND;

		Iterator<String> it = lines.iterator();
		if (it.hasNext()) {
			type = parseType(it.next());
		}

		StructureValidator validator = type.getValidator();

		while (it.hasNext()) {
			parseLine(it.next(), validator::parsePropertyValue);
		}

		//defaulting templates to whitelist overworld dimension if no dimension list provided and set to blacklist
		if (!validator.isDimensionWhiteList() && validator.getAcceptedDimensions().length == 0) {
			validator.setPropertyValue(DIMENSION_WHITE_LIST, true);
			validator.setPropertyValue(DIMENSION_LIST, new int[] {0});
		}

		return validator;
	}

	private void parsePropertyValue(String name, String value) {
		for (IStructureValidationProperty<?> property : properties.keySet()) {
			if (property.getName().equalsIgnoreCase(name)) {
				parsePropertyValue(property, value);
			}
		}
	}

	private <T> void parsePropertyValue(IStructureValidationProperty<T> property, String stringValue) {
		T value = property.parseValue(stringValue);
		setPropertyValue(property, value);
	}

	@SuppressWarnings("squid:S4784")
	private static final Pattern NAME_VALUE_MATCHER = Pattern.compile("([^=]*)=([^=]*)");

	private static void parseLine(String line, BiConsumer<String, String> parseNameValue) {
		Matcher matcher = NAME_VALUE_MATCHER.matcher(line);
		if (matcher.matches()) {
			parseNameValue.accept(matcher.group(1), matcher.group(2));
		}
	}

	private static StructureValidationType parseType(String line) {
		Matcher matcher = NAME_VALUE_MATCHER.matcher(line);
		if (matcher.matches() && matcher.group(1).equalsIgnoreCase("type")) {
			return StructureValidationType.getTypeFromName(matcher.group(2)).orElse(StructureValidationType.GROUND);
		}
		return StructureValidationType.GROUND;
	}

	private static <T> String getStringValue(StructureValidator validator, IStructureValidationProperty<T> property) {
		T value = validator.getPropertyValue(property);
		return property.getStringValue(value);
	}

	public static void writeValidator(BufferedWriter out, StructureValidator validator) throws IOException {
		out.write("type=" + validator.validationType.getName());
		out.newLine();
		for (IStructureValidationProperty<?> property : validator.properties.keySet()) {
			out.write(property.getName() + "=" + getStringValue(validator, property));
			out.newLine();
		}
	}

	public <T> void setPropertyValue(IStructureValidationProperty<T> property, T value) {
		if (!properties.containsKey(property)) {
			throw new IllegalArgumentException("Unable to update property - validator doesn't have property: " + property.getName());
		}
		properties.put(property, value);
	}

	public <T> T getPropertyValue(IStructureValidationProperty<T> property) {
		if (!properties.containsKey(property)) {
			throw new IllegalArgumentException("Unable to get property value - validator doesn't have property: " + property.getName());
		}
		return property.getValueClass().cast(properties.get(property));
	}

	public final StructureValidator setDefaults(StructureTemplate template) {
		setDefaultSettings(template);
		return this;
	}

	public final void setBiomeWhiteList(boolean val) {
		setPropertyValue(BIOME_WHITE_LIST, val);
	}

	public final void setDimensionWhiteList(boolean val) {
		setPropertyValue(DIMENSION_WHITE_LIST, val);
	}

	public final boolean isBlockSwap() {
		return getPropertyValue(BLOCK_SWAP);
	}

	public final int getSelectionWeight() {
		return getPropertyValue(SELECTION_WEIGHT);
	}

	public final int getClusterValue() {
		return getPropertyValue(CLUSTER_VALUE);
	}

	public final boolean isWorldGenEnabled() {
		return getPropertyValue(WORLD_GEN);
	}

	public final boolean isPreserveBlocks() {
		return getPropertyValue(PRESERVE_BLOCKS);
	}

	public final boolean isBiomeWhiteList() {
		return getPropertyValue(BIOME_WHITE_LIST);
	}

	public final boolean isUnique() {
		return getPropertyValue(UNIQUE);
	}

	public final boolean isDimensionWhiteList() {
		return getPropertyValue(DIMENSION_WHITE_LIST);
	}

	public final int[] getAcceptedDimensions() {
		return getPropertyValue(DIMENSION_LIST);
	}

	public final void setValidDimension(Set<Integer> dims) {
		int[] dimsa = new int[dims.size()];
		int index = 0;
		for (Integer dim : dims) {
			dimsa[index] = dim;
			index++;
		}
		setPropertyValue(DIMENSION_LIST, dimsa);
	}

	public final int getMinDuplicateDistance() {
		return getPropertyValue(MIN_DUPLICATE_DISTANCE);
	}

	public final void setBiomeList(Set<String> biomes) {
		setPropertyValue(BIOME_LIST, biomes);
	}

	public Set<String> getBiomeList() {
		//noinspection unchecked
		return getPropertyValue(BIOME_LIST);
	}

	int getMaxFill() {
		return getPropertyValue(MAX_FILL);
	}

	int getMaxLeveling() {
		return getPropertyValue(MAX_LEVELING);
	}

	public int getBorderSize() {
		return getPropertyValue(BORDER_SIZE);
	}

	//*********************************************** UTILITY METHODS *************************************************//
	boolean validateBorderBlocks(World world, StructureBB bb, int minY, int maxY, boolean skipWater) {
		int bx;
		int bz;
		int borderSize = getBorderSize();

		for (bx = bb.min.getX() - borderSize; bx <= bb.max.getX() + borderSize; bx++) {
			bz = bb.min.getZ() - borderSize;
			if (!validateBlockHeightTypeAndBiome(world, bx, bz, minY, maxY, skipWater)) {
				return false;
			}
			bz = bb.max.getZ() + borderSize;
			if (!validateBlockHeightTypeAndBiome(world, bx, bz, minY, maxY, skipWater)) {
				return false;
			}
		}
		for (bz = bb.min.getZ() - borderSize + 1; bz <= bb.max.getZ() + borderSize - 1; bz++) {
			bx = bb.min.getX() - borderSize;
			if (!validateBlockHeightTypeAndBiome(world, bx, bz, minY, maxY, skipWater)) {
				return false;
			}
			bx = bb.max.getX() + borderSize;
			if (!validateBlockHeightTypeAndBiome(world, bx, bz, minY, maxY, skipWater)) {
				return false;
			}
		}
		return true;
	}

	private boolean canSpawnInRiverBiome() {
		if (!riverBiomeChecked) {
			//noinspection unchecked
			canSpawnInRiverBiome = getPropertyValue(BIOME_WHITE_LIST) && getPropertyValue(BIOME_LIST).stream().anyMatch(name -> {
				Biome biome = Biome.REGISTRY.getObject(new ResourceLocation((String) name));
				return biome != null && BiomeDictionary.hasType(biome, BiomeDictionary.Type.RIVER);
			});
			riverBiomeChecked = true;
		}

		return canSpawnInRiverBiome;
	}

	/*
	 * validates both top block height and block type for the input position and settings
	 */
	boolean validateBlockHeightAndType(World world, int x, int z, int min, int max, boolean skipWater, Predicate<IBlockState> isValidState) {
		return validateBlockType(world, x, validateBlockHeight(world, x, z, min, max, skipWater), z, isValidState);
	}

	private boolean validateBlockHeightTypeAndBiome(World world, int x, int z, int min, int max, boolean skipWater, Predicate<IBlockState> isValidState) {
		BlockPos pos = new BlockPos(x, 1, z);
		if (!canSpawnInRiverBiome() && BiomeDictionary.hasType(world.provider.getBiomeForCoords(pos), BiomeDictionary.Type.RIVER)) {
			AncientWarfareStructure.LOG.debug("Rejected for placement into river biome at {}", pos);
			return false;
		}

		return validateBlockHeightAndType(world, x, z, min, max, skipWater, isValidState);
	}

	boolean validateBlockHeightTypeAndBiome(World world, int x, int z, int min, int max, boolean skipWater) {
		return validateBlockHeightTypeAndBiome(world, x, z, min, max, skipWater, AWStructureStatics::isValidTargetBlock);
	}

	/*
	 * validates top block height at X, Z is >=  min and <= max (inclusive)
	 * returns topFoundY or -1 if not within range
	 */
	private int validateBlockHeight(World world, int x, int z, int minimumAcceptableY, int maximumAcceptableY, boolean skipWater) {
		int topFilledY = WorldStructureGenerator.getTargetY(world, x, z, skipWater);
		if (topFilledY < minimumAcceptableY || topFilledY > maximumAcceptableY) {
			AncientWarfareStructure.LOG.debug("rejected for leveling or depth test. foundY: {} min: {} max: {} at: {},{},{}", topFilledY, minimumAcceptableY, maximumAcceptableY, x, topFilledY, z);
			return -1;
		}
		return topFilledY;
	}

	/*
	 * validates the target block at x,y,z is one of the input valid blocks
	 */
	private boolean validateBlockType(World world, int x, int y, int z, Predicate<IBlockState> isValidState) {
		if (y <= 0 || y >= world.getHeight()) {
			return false;
		}
		IBlockState state = world.getBlockState(new BlockPos(x, y, z));
		Block block = state.getBlock();
		if (block == Blocks.AIR) {
			AncientWarfareStructure.LOG.debug("rejected for non-matching block: air at: {},{},{} ", x, y, z);
			return false;
		}
		if (!isValidState.test(state)) {
			AncientWarfareStructure.LOG.debug("Rejected for non-matching block: {} at: {},{},{} ", block.getRegistryName(), x, y, z);
			return false;
		}
		return true;
	}

	/*
	 * return the lowest acceptable Y level for a filled block
	 * for the input template and BB
	 */
	int getMinY(StructureTemplate template, StructureBB bb) {
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
	int getMaxY(StructureTemplate template, StructureBB bb) {
		return bb.min.getY() + template.getOffset().getY() + getMaxLeveling();
	}

	private int getMaxFillY(StructureTemplate template, StructureBB bb) {
		return getMinY(template, bb) + getMaxFill();
	}

	private void borderLeveling(World world, int x, int z, StructureTemplate template, StructureBB bb) {
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

	void prePlacementUnderfill(World world, StructureBB bb) {
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

	public boolean isSurvival() {
		return getPropertyValue(SURVIVAL);
	}

	public Set<String> getBiomeGroupList() {
		//noinspection unchecked
		return getPropertyValue(BIOME_GROUP_LIST);
	}

	public void setBiomeGroupList(Set<String> biomeGroups) {
		setPropertyValue(BIOME_GROUP_LIST, biomeGroups);
	}
}
