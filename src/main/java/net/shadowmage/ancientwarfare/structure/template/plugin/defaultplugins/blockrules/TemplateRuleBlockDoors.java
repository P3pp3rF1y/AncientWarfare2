package net.shadowmage.ancientwarfare.structure.template.plugin.defaultplugins.blockrules;

import net.minecraft.block.BlockDoor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TemplateRuleBlockDoors extends TemplateRuleVanillaBlocks {

	public static final String PLUGIN_NAME = "doors";

	public TemplateRuleBlockDoors(World world, BlockPos pos, IBlockState state, int turns) {
		super(world, pos, state, turns);
	}

	public TemplateRuleBlockDoors() {
		super();
	}

	@Override
	public void addResources(NonNullList<ItemStack> resources) {
		if (state.getValue(BlockDoor.HALF) == BlockDoor.EnumDoorHalf.UPPER) {
			super.addResources(resources);
		}
	}

	@Override
	public String getPluginName() {
		return PLUGIN_NAME;
	}
}
