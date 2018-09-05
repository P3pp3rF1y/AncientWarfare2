package net.shadowmage.ancientwarfare.structure.api;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.core.util.NBTHelper;
import net.shadowmage.ancientwarfare.structure.block.BlockDataManager;

import java.util.List;

public abstract class TemplateRuleBlock extends TemplateRule {
	protected IBlockState state;

	public TemplateRuleBlock(IBlockState state, int turns) {
		this.state = BlockTools.rotateFacing(state, turns);
	}

	public TemplateRuleBlock(int ruleNumber, List<String> lines) throws TemplateParsingException.TemplateRuleParsingException {
		parseRule(ruleNumber, lines);
	}

	public abstract boolean shouldReuseRule(World world, IBlockState state, int turns, BlockPos pos);

	@Override
	public void addResources(NonNullList<ItemStack> resources) {
		if (state.getBlock() == Blocks.AIR) {
			return;
		}

		ItemStack stack = BlockDataManager.INSTANCE.getInventoryStackForBlock(state);
		if (stack.isEmpty()) {
			throw new IllegalArgumentException("Could not create item for block: " + NBTHelper.getBlockStateTag(state).toString());
		}
		resources.add(stack);
	}

	@Override
	protected String getRuleType() {
		return "rule";
	}

	@Override
	public void parseRuleData(NBTTagCompound tag) {
		state = NBTHelper.getBlockState(tag.getCompoundTag("blockState"));
	}

	@Override
	public void writeRuleData(NBTTagCompound tag) {
		tag.setTag("blockState", NBTHelper.getBlockStateTag(state));
	}
}
