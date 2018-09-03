package net.shadowmage.ancientwarfare.structure.template.plugin.default_plugins.block_rules;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.structure.api.IStructureBuilder;
import net.shadowmage.ancientwarfare.structure.api.TemplateParsingException;
import net.shadowmage.ancientwarfare.structure.api.TemplateRuleBlock;
import net.shadowmage.ancientwarfare.structure.block.BlockDataManager;

import java.util.List;

public class TemplateRuleModBlocks extends TemplateRuleBlock {
	public static final String PLUGIN_NAME = "modBlockDefault";
	private String blockName;
	private int meta;

	public TemplateRuleModBlocks(World world, BlockPos pos, Block block, int meta, int turns) {
		super(world, pos, block, meta, turns);
		this.blockName = BlockDataManager.INSTANCE.getNameForBlock(block);
		this.meta = meta;
	}

	public TemplateRuleModBlocks(int ruleNumber, List<String> lines) throws TemplateParsingException.TemplateRuleParsingException {
		super(ruleNumber, lines);
	}

	@Override
	public boolean shouldReuseRule(World world, Block block, int meta, int turns, BlockPos pos) {
		return BlockDataManager.INSTANCE.getNameForBlock(block).equals(blockName) && meta == this.meta;
	}

	@Override
	public void handlePlacement(World world, int turns, BlockPos pos, IStructureBuilder builder) {
		Block block = BlockDataManager.INSTANCE.getBlockForName(blockName);
		world.setBlockState(pos, block.getStateFromMeta(meta), 3);
	}

	@Override
	public void writeRuleData(NBTTagCompound tag) {
		tag.setString("blockName", blockName);
		tag.setInteger("meta", meta);
	}

	@Override
	public void parseRuleData(NBTTagCompound tag) {
		blockName = tag.getString("blockName");
		meta = tag.getInteger("meta");
	}

	@Override
	public void addResources(NonNullList<ItemStack> resources) {
		//TODO add resources
	}

	@Override
	public boolean shouldPlaceOnBuildPass(World world, int turns, BlockPos pos, int buildPass) {
		return buildPass == 0;
	}

	@Override
	protected String getPluginName() {
		return PLUGIN_NAME;
	}
}
