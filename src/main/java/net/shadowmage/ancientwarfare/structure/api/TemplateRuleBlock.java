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
import net.shadowmage.ancientwarfare.structure.AncientWarfareStructure;
import net.shadowmage.ancientwarfare.structure.registry.StructureBlockRegistry;

import java.util.List;
import java.util.MissingResourceException;

public abstract class TemplateRuleBlock extends TemplateRule {
	protected IBlockState state;
	private ItemStack cachedStack = null;

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

		ItemStack stack = getCachedStack();
		if (!stack.isEmpty()) {
			resources.add(stack);
		}
	}

	private ItemStack getCachedStack() {
		if (cachedStack == null) {
			cachedStack = getStack();
		}
		return cachedStack;
	}

	protected ItemStack getStack() {
		return StructureBlockRegistry.getItemStackFrom(state);
	}

	@Override
	public ItemStack getRemainingStack() {
		return StructureBlockRegistry.getRemainingStackFrom(state);
	}

	@Override
	public boolean placeInSurvival() {
		return state.getBlock() != Blocks.AIR && !getCachedStack().isEmpty();
	}

	@Override
	protected String getRuleType() {
		return "rule";
	}

	@Override
	public void parseRuleData(NBTTagCompound tag) {
		try {
			state = NBTHelper.getBlockState(tag.getCompoundTag("blockState"));
		}
		catch (MissingResourceException e) {
			AncientWarfareStructure.LOG.warn("Unable to find blockstate while parsing structure template thus replacing it with air - {}.", e.getMessage());
			state = Blocks.AIR.getDefaultState();
		}
	}

	@Override
	public void writeRuleData(NBTTagCompound tag) {
		tag.setTag("blockState", NBTHelper.getBlockStateTag(state));
	}
}
