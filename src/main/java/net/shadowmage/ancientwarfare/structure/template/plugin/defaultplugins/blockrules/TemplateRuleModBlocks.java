package net.shadowmage.ancientwarfare.structure.template.plugin.defaultplugins.blockrules;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.structure.api.IStructureBuilder;
import net.shadowmage.ancientwarfare.structure.api.TemplateParsingException;
import net.shadowmage.ancientwarfare.structure.api.TemplateRuleBlock;

import java.util.List;

public class TemplateRuleModBlocks extends TemplateRuleBlock {
	public static final String PLUGIN_NAME = "modBlockDefault";

	public TemplateRuleModBlocks(World world, BlockPos pos, IBlockState state, int turns) {
		super(state, turns);
	}

	public TemplateRuleModBlocks(int ruleNumber, List<String> lines) throws TemplateParsingException.TemplateRuleParsingException {
		super(ruleNumber, lines);
	}

	@Override
	public boolean shouldReuseRule(World world, IBlockState state, int turns, BlockPos pos) {
		return this.state.getBlock() == state.getBlock() && this.state.getProperties().equals(state.getProperties());
	}

	@Override
	public void handlePlacement(World world, int turns, BlockPos pos, IStructureBuilder builder) {
		world.setBlockState(pos, BlockTools.rotateFacing(state, turns), 3);
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
