package net.shadowmage.ancientwarfare.structure.template.plugin.defaultplugins.blockrules;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.structure.api.IStructureBuilder;
import net.shadowmage.ancientwarfare.structure.api.TemplateParsingException;
import net.shadowmage.ancientwarfare.structure.api.TemplateRuleBlock;
import net.shadowmage.ancientwarfare.structure.block.BlockDataManager;

import javax.annotation.Nonnull;
import java.util.List;

public class TemplateRuleVanillaBlocks extends TemplateRuleBlock {

	public static final String PLUGIN_NAME = "vanillaBlocks";
	public String blockName;
	public Block block;
	public int meta;
	public int buildPass;

	/*
	 * constructor for dynamic construction.  passed world and coords so that the rule can handle its own logic internally
	 */
	public TemplateRuleVanillaBlocks(World world, BlockPos pos, Block block, int meta, int turns) {
		super(world, pos, block, meta, turns);
		this.blockName = BlockDataManager.INSTANCE.getNameForBlock(block);
		this.block = block;
		this.meta = BlockDataManager.INSTANCE.getRotatedMeta(block, meta, turns);
		this.buildPass = BlockDataManager.INSTANCE.getPriorityForBlock(block);
	}

	public TemplateRuleVanillaBlocks(int ruleNumber, List<String> lines) throws TemplateParsingException.TemplateRuleParsingException {
		super(ruleNumber, lines);
	}

	@Override
	public void handlePlacement(World world, int turns, BlockPos pos, IStructureBuilder builder) {
		int localMeta = BlockDataManager.INSTANCE.getRotatedMeta(block, this.meta, turns);
		builder.placeBlock(pos, block.getStateFromMeta(localMeta), buildPass);
	}

	@Override
	public boolean shouldReuseRule(World world, Block block, int meta, int turns, BlockPos pos) {
		return block != null && block == this.block && BlockDataManager.INSTANCE.getRotatedMeta(block, meta, turns) == this.meta;
	}

	@Override
	public void addResources(NonNullList<ItemStack> resources) {
		if (block == null || block == Blocks.AIR) {
			return;
		}

		@Nonnull ItemStack stack = BlockDataManager.INSTANCE.getInventoryStackForBlock(block, meta);
		if (stack.isEmpty()) {
			throw new IllegalArgumentException("Could not create item for block: " + block + " (lookup name: " + blockName + ") meta: " + meta);
		}
		resources.add(stack);
	}

	@Override
	public boolean shouldPlaceOnBuildPass(World world, int turns, BlockPos pos, int buildPass) {
		return buildPass == this.buildPass;
	}

	@Override
	public String toString() {
		return String.format("Vanilla Block Rule id: %s meta: %s buildPass: %s", blockName, meta, buildPass);
	}

	@Override
	public void writeRuleData(NBTTagCompound tag) {
		tag.setString("blockName", blockName);
		tag.setInteger("meta", meta);
		tag.setInteger("buildPass", buildPass);
	}

	@Override
	public void parseRuleData(NBTTagCompound tag) {
		this.blockName = tag.getString("blockName");
		this.block = BlockDataManager.INSTANCE.getBlockForName(blockName);
		this.meta = tag.getInteger("meta");
		this.buildPass = tag.getInteger("buildPass");
	}

	@Override
	protected String getPluginName() {
		return PLUGIN_NAME;
	}
}
