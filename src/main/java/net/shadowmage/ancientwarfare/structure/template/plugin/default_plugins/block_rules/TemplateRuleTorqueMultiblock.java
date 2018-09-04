package net.shadowmage.ancientwarfare.structure.template.plugin.default_plugins.block_rules;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.core.util.WorldTools;
import net.shadowmage.ancientwarfare.structure.api.IStructureBuilder;
import net.shadowmage.ancientwarfare.structure.api.TemplateParsingException;
import net.shadowmage.ancientwarfare.structure.api.TemplateRuleBlock;
import net.shadowmage.ancientwarfare.structure.block.BlockDataManager;

import java.util.List;

public class TemplateRuleTorqueMultiblock extends TemplateRuleBlock {

	public static final String PLUGIN_NAME = "awTorqueMulti";
	int meta;
	private String blockName;
	NBTTagCompound tag;

	public TemplateRuleTorqueMultiblock(World world, BlockPos pos, Block block, int meta, int turns) {
		super(world, pos, block, meta, turns);
		this.blockName = BlockDataManager.INSTANCE.getNameForBlock(block);
		this.meta = meta;
		this.tag = new NBTTagCompound();
		WorldTools.getTile(world, pos).ifPresent(t -> t.writeToNBT(tag));
	}

	public TemplateRuleTorqueMultiblock(int ruleNumber, List<String> lines) throws TemplateParsingException.TemplateRuleParsingException {
		super(ruleNumber, lines);
	}

	@Override
	public boolean shouldReuseRule(World world, Block block, int meta, int turns, BlockPos pos) {
		return false;
	}

	@Override
	public void handlePlacement(World world, int turns, BlockPos pos, IStructureBuilder builder) {
		Block block = BlockDataManager.INSTANCE.getBlockForName(blockName);
		if (world.setBlockState(pos, block.getStateFromMeta(meta), 3)) {
			WorldTools.getTile(world, pos).ifPresent(t -> {
				//TODO look into changing this so that the whole TE doesn't need reloading from custom NBT
				tag.setString("id", block.getRegistryName().toString());
				tag.setInteger("x", pos.getX());
				tag.setInteger("y", pos.getY());
				tag.setInteger("z", pos.getZ());
				t.readFromNBT(tag);
			});
			BlockTools.notifyBlockUpdate(world, pos);
			block.onBlockPlacedBy(world, pos, block.getStateFromMeta(meta), null, null);
		}
	}

	@Override
	public void parseRuleData(NBTTagCompound tag) {
		blockName = tag.getString("blockId");
		meta = tag.getInteger("meta");
		this.tag = tag.getCompoundTag("teData");
	}

	@Override
	public void writeRuleData(NBTTagCompound tag) {
		tag.setString("blockId", blockName);
		tag.setInteger("meta", meta);
		tag.setTag("teData", this.tag);
	}

	@Override
	public void addResources(NonNullList<ItemStack> resources) {
		resources.add(new ItemStack(Item.getItemFromBlock(BlockDataManager.INSTANCE.getBlockForName(blockName)), 1, meta));
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
