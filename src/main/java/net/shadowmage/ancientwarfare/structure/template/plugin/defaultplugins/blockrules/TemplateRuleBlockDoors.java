package net.shadowmage.ancientwarfare.structure.template.plugin.defaultplugins.blockrules;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.structure.api.IStructureBuilder;
import net.shadowmage.ancientwarfare.structure.api.TemplateParsingException;
import net.shadowmage.ancientwarfare.structure.block.BlockDataManager;

import java.util.List;

public class TemplateRuleBlockDoors extends TemplateRuleVanillaBlocks {

	public static final String PLUGIN_NAME = "doors";
	private byte sideFlag;

	public TemplateRuleBlockDoors(World world, BlockPos pos, Block block, int meta, int turns) {
		super(world, pos, block, meta, turns);
		if (world.getBlockState(pos.up()) == block) {
			IBlockState state = world.getBlockState(pos.up());
			sideFlag = (byte) state.getBlock().getMetaFromState(state);
		}
	}

	public TemplateRuleBlockDoors(int ruleNumber, List<String> lines) throws TemplateParsingException.TemplateRuleParsingException {
		super(ruleNumber, lines);
	}

	@Override
	public void handlePlacement(World world, int turns, BlockPos pos, IStructureBuilder builder) {
		Block block = BlockDataManager.INSTANCE.getBlockForName(blockName);
		int localMeta = BlockDataManager.INSTANCE.getRotatedMeta(block, this.meta, turns);
		if (world.getBlockState(pos.down()).getBlock() != block)//this is the bottom door block, call placeDoor from our block...
		{
			world.setBlockState(pos, block.getStateFromMeta(localMeta), 2);
			world.setBlockState(pos.up(), block.getStateFromMeta(sideFlag == 0 ? 8 : sideFlag), 2);
		}
	}

	@Override
	public void writeRuleData(NBTTagCompound tag) {
		tag.setString("blockName", blockName);
		tag.setInteger("meta", meta);
		tag.setInteger("buildPass", buildPass);
		tag.setByte("sideFlag", sideFlag);
	}

	@Override
	public void parseRuleData(NBTTagCompound tag) {
		this.blockName = tag.getString("blockName");
		this.meta = tag.getInteger("meta");
		this.buildPass = tag.getInteger("buildPass");
		this.sideFlag = tag.getByte("sideFlag");
	}

	@Override
	public boolean shouldReuseRule(World world, Block block, int meta, int turns, BlockPos pos) {
		IBlockState state = world.getBlockState(pos.up());
		Block block1 = state.getBlock();
		return blockName.equals(BlockDataManager.INSTANCE.getNameForBlock(block1)) && block1.getMetaFromState(state) == sideFlag;
	}

	@Override
	public void addResources(NonNullList<ItemStack> resources) {
		if (sideFlag > 0) {
			super.addResources(resources);
		}
	}

	@Override
	protected String getPluginName() {
		return PLUGIN_NAME;
	}
}
