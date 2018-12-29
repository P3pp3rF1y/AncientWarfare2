package net.shadowmage.ancientwarfare.structure.template.plugin.defaultplugins.blockrules;

import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.structure.api.IStructureBuilder;

import static net.minecraft.block.BlockVine.*;

public class TemplateRuleVine extends TemplateRuleVanillaBlocks {

	public static final String PLUGIN_NAME = "vine";

	private PropertyBool[] SIDE_PROPERTIES = new PropertyBool[] {NORTH, EAST, SOUTH, WEST};

	public TemplateRuleVine() {
		super();
	}

	public TemplateRuleVine(World world, BlockPos pos, IBlockState state, int turns) {
		super(world, pos, state, turns);
		this.state = rotateSides(this.state, turns);
	}

	private IBlockState rotateSides(IBlockState state, int turns) {
		IBlockState modifiedState = state;
		for (int i = 0; i < 4; i++) {
			modifiedState = modifiedState.withProperty(SIDE_PROPERTIES[(i + turns) % 4], state.getValue(SIDE_PROPERTIES[i]));
		}
		return modifiedState;
	}

	@Override
	public boolean shouldReuseRule(World world, IBlockState state, int turns, BlockPos pos) {
		return state.getBlock() == this.state.getBlock() && rotateSides(state, turns).getProperties().equals(this.state.getProperties());
	}

	@Override
	public void handlePlacement(World world, int turns, BlockPos pos, IStructureBuilder builder) {
		builder.placeBlock(pos, getState(turns), buildPass);
	}

	@Override
	public IBlockState getState(int turns) {
		return rotateSides(state, turns);
	}

	@Override
	public String getPluginName() {
		return PLUGIN_NAME;
	}
}
