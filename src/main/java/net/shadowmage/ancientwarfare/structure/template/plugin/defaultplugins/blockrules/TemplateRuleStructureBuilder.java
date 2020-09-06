package net.shadowmage.ancientwarfare.structure.template.plugin.defaultplugins.blockrules;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.owner.Owner;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.core.util.WorldTools;
import net.shadowmage.ancientwarfare.structure.api.IStructureBuilder;
import net.shadowmage.ancientwarfare.structure.api.TemplateRuleBlock;
import net.shadowmage.ancientwarfare.structure.init.AWStructureItems;
import net.shadowmage.ancientwarfare.structure.template.build.StructureBuilderTicked;
import net.shadowmage.ancientwarfare.structure.tile.TileStructureBuilder;

import java.util.Optional;

public class TemplateRuleStructureBuilder extends TemplateRuleBlock {
	public static final String PLUGIN_NAME = "AWStructureBuilder";

	String templateName;
	EnumFacing facing;

	public TemplateRuleStructureBuilder(World world, BlockPos pos, IBlockState state, int turns) {
		super(state, turns);
		WorldTools.getTile(world, pos, TileStructureBuilder.class).ifPresent(structureBuilder -> {
			StructureBuilderTicked builder = structureBuilder.getBuilder();
			templateName = builder.getTemplate().name;
			facing = rotateFacing(turns, builder.getBuildFace());
		});
	}

	public TemplateRuleStructureBuilder() {
		super();
	}

	@Override
	public boolean shouldReuseRule(World world, IBlockState state, int turns, BlockPos pos) {
		return false;
	}

	@Override
	public void handlePlacement(World world, int turns, BlockPos pos, IStructureBuilder builder) {
		builder.placeBlock(pos, BlockTools.rotateFacing(state, turns), 0);
		WorldTools.getTile(world, pos, TileStructureBuilder.class).ifPresent(structureBuilder -> {
			structureBuilder.setOwner(Owner.EMPTY);
			EnumFacing placementFacing = rotateFacing(turns, facing);
			AWStructureItems.STRUCTURE_BUILDER_TICKED.setupStructureBuilder(world, pos, structureBuilder, templateName, placementFacing);
		});
	}

	private EnumFacing rotateFacing(int turns, EnumFacing facing) {
		for (int i = 0; i < turns; i++) {
			facing = facing.rotateY();
		}
		return facing;
	}

	@Override
	protected Optional<ItemStack> getStack() {
		ItemStack stack = new ItemStack(AWStructureItems.STRUCTURE_BUILDER_TICKED);
		stack.setTagInfo("structureName", new NBTTagString(templateName));
		return Optional.of(stack);
	}

	@Override
	public String getPluginName() {
		return PLUGIN_NAME;
	}

	@Override
	public void writeRuleData(NBTTagCompound tag) {
		super.writeRuleData(tag);
		tag.setString("templateName", templateName);
		tag.setInteger("facing", facing.getHorizontalIndex());
	}

	@Override
	public boolean shouldPlaceOnBuildPass(World world, int turns, BlockPos pos, int buildPass) {
		return buildPass == 0;
	}

	@Override
	public void parseRule(NBTTagCompound tag) {
		super.parseRule(tag);
		templateName = tag.getString("templateName");
		facing = EnumFacing.HORIZONTALS[tag.getInteger("facing")];
	}
}
