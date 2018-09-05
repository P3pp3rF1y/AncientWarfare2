package net.shadowmage.ancientwarfare.structure.template.plugin.defaultplugins.blockrules;

import net.minecraft.block.BlockDoor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.structure.api.IStructureBuilder;
import net.shadowmage.ancientwarfare.structure.api.TemplateParsingException;

import java.util.List;

public class TemplateRuleBlockDoors extends TemplateRuleVanillaBlocks {

	public static final String PLUGIN_NAME = "doors";
	private boolean lower;

	public TemplateRuleBlockDoors(World world, BlockPos pos, IBlockState state, int turns) {
		super(world, pos, state, turns);
		lower = world.getBlockState(pos.up()).getBlock() == state.getBlock();
	}

	public TemplateRuleBlockDoors(int ruleNumber, List<String> lines) throws TemplateParsingException.TemplateRuleParsingException {
		super(ruleNumber, lines);
	}

	@Override
	public void handlePlacement(World world, int turns, BlockPos pos, IStructureBuilder builder) {
		IBlockState rotatedState = BlockTools.rotateFacing(state, turns);
		if (lower) {
			world.setBlockState(pos, rotatedState.withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.LOWER), 2);
			world.setBlockState(pos.up(), rotatedState.withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.UPPER), 2);
		}
	}

	@Override
	public void writeRuleData(NBTTagCompound tag) {
		super.writeRuleData(tag);
		tag.setBoolean("lower", lower);
	}

	@Override
	public void parseRuleData(NBTTagCompound tag) {
		super.parseRuleData(tag);
		lower = tag.getBoolean("lower");
	}

	@Override
	public void addResources(NonNullList<ItemStack> resources) {
		if (lower) {
			super.addResources(resources);
		}
	}

	@Override
	protected String getPluginName() {
		return PLUGIN_NAME;
	}
}
