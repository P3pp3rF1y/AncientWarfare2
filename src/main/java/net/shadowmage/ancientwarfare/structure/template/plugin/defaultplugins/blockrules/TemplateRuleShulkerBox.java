package net.shadowmage.ancientwarfare.structure.template.plugin.defaultplugins.blockrules;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TemplateRuleShulkerBox extends TemplateRuleBlockInventory {
	public static final String PLUGIN_NAME = "shulkerBox";

	public TemplateRuleShulkerBox(World world, BlockPos pos, IBlockState state, int turns) {
		super(world, pos, state, turns);
	}

	public TemplateRuleShulkerBox() {
		super();
	}

	@Override
	protected ItemStack getStack() {
		return new ItemStack(Item.getItemFromBlock(state.getBlock()), 1, state.getBlock().damageDropped(state));
	}

	@Override
	protected String getPluginName() {
		return PLUGIN_NAME;
	}
}
