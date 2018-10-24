package net.shadowmage.ancientwarfare.structure.template.plugin.defaultplugins.blockrules;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.structure.api.TemplateParsingException;

import java.util.List;

public class TemplateRuleFluid extends TemplateRuleVanillaBlocks {
	public static final String PLUGIN_NAME = "fluid";

	public TemplateRuleFluid(World world, BlockPos pos, IBlockState state, int turns) {
		super(world, pos, state, turns);
	}

	public TemplateRuleFluid(int ruleNumber, List<String> lines) throws TemplateParsingException.TemplateRuleParsingException {
		super(ruleNumber, lines);
	}

	@Override
	public boolean placeInSurvival() {
		return true;
	}

	@Override
	protected String getPluginName() {
		return PLUGIN_NAME;
	}
}
