package net.shadowmage.ancientwarfare.structure.template.plugin.defaultplugins.blockrules;

import net.minecraft.block.BlockDoor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.structure.api.IStructureBuilder;

import java.util.Collections;
import java.util.List;

public class TemplateRuleBlockDoors extends TemplateRuleVanillaBlocks {

	public static final String PLUGIN_NAME = "doors";

	public TemplateRuleBlockDoors(World world, BlockPos pos, IBlockState state, int turns) {
		super(world, pos, state, turns);
	}

	public TemplateRuleBlockDoors() {
		super();
	}

	@Override
	public List<ItemStack> getResources() {
		if (state.getValue(BlockDoor.HALF) == BlockDoor.EnumDoorHalf.UPPER) {
			return super.getResources();
		}

		return Collections.emptyList();
	}

	@Override
	public void handlePlacement(World world, int turns, BlockPos pos, IStructureBuilder builder) {
		if (state.getValue(BlockDoor.HALF) == BlockDoor.EnumDoorHalf.LOWER) {
			IBlockState state = BlockTools.rotateFacing(this.state, turns);
			builder.placeBlock(pos, state, buildPass);
			builder.placeBlock(pos.up(), state.withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.UPPER), buildPass);
		}
	}

	@Override
	public String getPluginName() {
		return PLUGIN_NAME;
	}
}
