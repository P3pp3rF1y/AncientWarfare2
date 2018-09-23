package net.shadowmage.ancientwarfare.structure.template.plugin.defaultplugins.blockrules;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.core.util.WorldTools;
import net.shadowmage.ancientwarfare.structure.api.IStructureBuilder;
import net.shadowmage.ancientwarfare.structure.api.TemplateParsingException;
import net.shadowmage.ancientwarfare.structure.api.TemplateRuleBlock;

import java.util.List;

public class TemplateRuleTorqueMultiblock extends TemplateRuleBlock {
	public static final String PLUGIN_NAME = "awTorqueMulti";
	private NBTTagCompound tag;

	public TemplateRuleTorqueMultiblock(World world, BlockPos pos, IBlockState state, int turns) {
		super(state, turns);
		this.tag = new NBTTagCompound();
		WorldTools.getTile(world, pos).ifPresent(t -> t.writeToNBT(tag));
	}

	public TemplateRuleTorqueMultiblock(int ruleNumber, List<String> lines) throws TemplateParsingException.TemplateRuleParsingException {
		super(ruleNumber, lines);
	}

	@Override
	public boolean shouldReuseRule(World world, IBlockState state, int turns, BlockPos pos) {
		return false;
	}

	@Override
	public void handlePlacement(World world, int turns, BlockPos pos, IStructureBuilder builder) {
		if (world.setBlockState(pos, state, 3)) {
			WorldTools.getTile(world, pos).ifPresent(t -> {
				//TODO look into changing this so that the whole TE doesn't need reloading from custom NBT
				tag.setString("id", state.getBlock().getRegistryName().toString());
				tag.setInteger("x", pos.getX());
				tag.setInteger("y", pos.getY());
				tag.setInteger("z", pos.getZ());
				t.readFromNBT(tag);
			});
			BlockTools.notifyBlockUpdate(world, pos);
			state.getBlock().onBlockPlacedBy(world, pos, state, null, null);
		}
	}

	@Override
	public void parseRuleData(NBTTagCompound tag) {
		super.parseRuleData(tag);
		this.tag = tag.getCompoundTag("teData");
	}

	@Override
	public void writeRuleData(NBTTagCompound tag) {
		super.writeRuleData(tag);
		tag.setTag("teData", this.tag);
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
