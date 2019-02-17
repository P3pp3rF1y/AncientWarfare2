package net.shadowmage.ancientwarfare.structure.template.plugin.defaultplugins.blockrules;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.util.WorldTools;
import net.shadowmage.ancientwarfare.structure.api.IStructureBuilder;

import static net.shadowmage.ancientwarfare.structure.block.BlockProtectionFlag.ROTATION;

public class TemplateRuleFlag extends TemplateRuleBlockTile {
	public static final String PLUGIN_NAME = "flag";

	public TemplateRuleFlag(World world, BlockPos pos, IBlockState state, int turns) {
		super(world, pos, rotate(state, turns), turns);
	}

	public TemplateRuleFlag() {
		super();
	}

	@Override
	public void handlePlacement(World world, int turns, BlockPos pos, IStructureBuilder builder) {
		builder.placeBlock(pos, getState(turns), buildPass);
		WorldTools.getTile(world, pos).ifPresent(t -> {
			tag.setInteger("x", pos.getX());
			tag.setInteger("y", pos.getY());
			tag.setInteger("z", pos.getZ());
			t.readFromNBT(tag);
		});
	}

	@Override
	public IBlockState getState(int turns) {
		return rotate(state, turns);
	}

	private static IBlockState rotate(IBlockState state, int turns) {
		return state.withProperty(ROTATION, (state.getValue(ROTATION) + 4 * turns) % 16);
	}

	@Override
	public String getPluginName() {
		return PLUGIN_NAME;
	}
}
