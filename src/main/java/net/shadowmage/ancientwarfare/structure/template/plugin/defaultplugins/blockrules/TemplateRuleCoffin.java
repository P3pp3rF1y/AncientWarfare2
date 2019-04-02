package net.shadowmage.ancientwarfare.structure.template.plugin.defaultplugins.blockrules;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.structure.block.BlockCoffin;
import net.shadowmage.ancientwarfare.structure.tile.TileCoffin;

public class TemplateRuleCoffin extends TemplateRuleMulti<TileCoffin> {
	public static final String PLUGIN_NAME = "coffin";

	public TemplateRuleCoffin(World world, BlockPos pos, IBlockState state, int turns) {
		super(world, pos, state, turns, TileCoffin.class);
		BlockCoffin.CoffinDirection direction = BlockCoffin.CoffinDirection.fromName(tag.getString("direction"));
		tag.setString("direction", rotateDirection(direction, turns).getName());
	}

	private BlockCoffin.CoffinDirection rotateDirection(BlockCoffin.CoffinDirection direction, int turns) {
		BlockCoffin.CoffinDirection ret = direction;
		for (int turn = 0; turn < turns; turn++) {
			ret = ret.rotateY();
		}
		return ret;
	}

	public TemplateRuleCoffin() {
		super(TileCoffin.class);
	}

	@Override
	protected void rotateTe(TileCoffin te, int turns) {
		super.rotateTe(te, turns);
		te.setDirection(rotateDirection(te.getDirection(), turns));
	}

	@Override
	public String getPluginName() {
		return PLUGIN_NAME;
	}
}
