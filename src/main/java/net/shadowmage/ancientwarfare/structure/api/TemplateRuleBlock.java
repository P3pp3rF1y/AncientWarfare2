package net.shadowmage.ancientwarfare.structure.api;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.core.util.NBTHelper;
import net.shadowmage.ancientwarfare.core.util.RenderTools;
import net.shadowmage.ancientwarfare.structure.AncientWarfareStructure;
import net.shadowmage.ancientwarfare.structure.registry.StructureBlockRegistry;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.MissingResourceException;
import java.util.Optional;

public abstract class TemplateRuleBlock extends TemplateRule {
	protected IBlockState state;
	private ItemStack cachedStack = null;
	private boolean placeInSurvival = false;

	public TemplateRuleBlock(IBlockState state, int turns) {
		this.state = BlockTools.rotateFacing(state, turns);
	}

	public TemplateRuleBlock() {
	}

	public abstract boolean shouldReuseRule(World world, IBlockState state, int turns, BlockPos pos);

	@Override
	public List<ItemStack> getResources() {
		if (state.getBlock() == Blocks.AIR) {
			return Collections.emptyList();
		}

		ItemStack stack = getCachedStack();
		if (!stack.isEmpty()) {
			return Collections.singletonList(stack);
		}

		return Collections.emptyList();
	}

	private ItemStack getCachedStack() {
		cacheStack();
		return cachedStack;
	}

	private void cacheStack() {
		if (cachedStack == null) {
			Optional<ItemStack> stack = getStack();
			placeInSurvival = stack.isPresent();
			cachedStack = stack.orElse(ItemStack.EMPTY);
		}
	}

	protected Optional<ItemStack> getStack() {
		return StructureBlockRegistry.getItemStackFrom(state);
	}

	@Override
	public ItemStack getRemainingStack() {
		return StructureBlockRegistry.getRemainingStackFrom(state);
	}

	@Override
	public boolean placeInSurvival() {
		cacheStack();
		return state.getBlock() != Blocks.AIR && placeInSurvival;
	}

	@Override
	protected String getRuleType() {
		return "rule";
	}

	@Override
	public void parseRule(NBTTagCompound tag) {
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

	@SideOnly(Side.CLIENT)
	public void renderRule(int turns, BlockPos pos, IBlockAccess blockAccess, BufferBuilder bufferBuilder) {
		Minecraft.getMinecraft().getBlockRendererDispatcher().renderBlock(getState(turns), pos, blockAccess, bufferBuilder);
	}

	public IBlockState getState(int turns) {
		return BlockTools.rotateFacing(state, turns);
	}

	@Nullable
	public TileEntity getTileEntity(int turns) {
		return null;
	}

	@SuppressWarnings("squid:S1172")
	public boolean isDynamicallyRendered(int turns) {
		return false;
	}

	@SideOnly(Side.CLIENT)
	public void renderRuleDynamic(int turns, BlockPos pos) {
		RenderTools.renderTESR(getTileEntity(turns), pos);
	}
}
