package net.shadowmage.ancientwarfare.structure.template.plugin.defaultplugins.blockrules;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TemplateRuleFluid extends TemplateRuleVanillaBlocks {
	public static final String PLUGIN_NAME = "fluid";

	public TemplateRuleFluid(World world, BlockPos pos, IBlockState state, int turns) {
		super(world, pos, state, turns);
	}

	public TemplateRuleFluid() {
		super();
	}

	@Override
	public boolean placeInSurvival() {
		return true;
	}

	@Override
	public String getPluginName() {
		return PLUGIN_NAME;
	}
}
