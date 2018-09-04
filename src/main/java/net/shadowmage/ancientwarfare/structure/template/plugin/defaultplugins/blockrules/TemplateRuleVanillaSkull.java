package net.shadowmage.ancientwarfare.structure.template.plugin.defaultplugins.blockrules;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.structure.api.IStructureBuilder;
import net.shadowmage.ancientwarfare.structure.api.TemplateParsingException;

import java.util.List;

public class TemplateRuleVanillaSkull extends TemplateRuleBlockTile {
	public static final String PLUGIN_NAME = "vanillaSkull";
	public int rotation;

	public TemplateRuleVanillaSkull(World world, BlockPos pos, Block block, int meta, int turns) {
		super(world, pos, block, meta, turns);
		int t = tag.getInteger("Rot");
		t = getRotation(t, turns);
		rotation = t;
	}

	public TemplateRuleVanillaSkull(int ruleNumber, List<String> lines) throws TemplateParsingException.TemplateRuleParsingException {
		super(ruleNumber, lines);
	}

	@Override
	public void handlePlacement(World world, int turns, BlockPos pos, IStructureBuilder builder) {
		tag.setInteger("Rot", getRotation(rotation, turns));
		super.handlePlacement(world, turns, pos, builder);
	}

	private int getRotation(int originalRotation, int turns) {
		EnumFacing facing = EnumFacing.getHorizontal((originalRotation % 16) / 4).getOpposite();

		for (int t = 0; t < turns; t++) {
			facing = facing.rotateY();
		}

		return facing.getOpposite().getHorizontalIndex() * 4;
	}

	@Override
	public void writeRuleData(NBTTagCompound tag) {
		this.tag.setInteger("Rot", rotation);
		super.writeRuleData(tag);
	}

	@Override
	public void parseRuleData(NBTTagCompound tag) {
		super.parseRuleData(tag);
		rotation = this.tag.getInteger("Rot");
	}

	@Override
	protected String getPluginName() {
		return PLUGIN_NAME;
	}
}
