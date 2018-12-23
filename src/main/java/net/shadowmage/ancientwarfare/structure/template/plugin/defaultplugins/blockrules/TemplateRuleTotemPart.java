package net.shadowmage.ancientwarfare.structure.template.plugin.defaultplugins.blockrules;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.render.property.CoreProperties;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.core.util.WorldTools;
import net.shadowmage.ancientwarfare.structure.api.IStructureBuilder;
import net.shadowmage.ancientwarfare.structure.api.TemplateRuleBlock;
import net.shadowmage.ancientwarfare.structure.block.BlockTotemPart;
import net.shadowmage.ancientwarfare.structure.tile.TileTotemPart;

import javax.annotation.Nullable;
import java.util.Optional;

public class TemplateRuleTotemPart extends TemplateRuleBlock {
	public static final String PLUGIN_NAME = "totemPart";
	private BlockTotemPart.Variant variant;
	private boolean mainBlock = false;

	public TemplateRuleTotemPart(World world, BlockPos pos, IBlockState state, int turns) {
		super(state, turns);

		Optional<TileTotemPart> te = WorldTools.getTile(world, pos, TileTotemPart.class);
		if (!te.isPresent()) {
			return;
		}
		TileTotemPart totem = te.get();
		variant = totem.getVariant();
		mainBlock = !totem.getMainBlockPos().isPresent();
	}

	public TemplateRuleTotemPart() {
		super();
	}

	@Override
	public void handlePlacement(World world, int turns, BlockPos pos, IStructureBuilder builder) {
		if (mainBlock) {
			IBlockState rotatedState = BlockTools.rotateFacing(state, turns);
			world.setBlockState(pos, rotatedState);
			WorldTools.getTile(world, pos, TileTotemPart.class).ifPresent(te -> te.setVariant(variant));
			variant.placeAdditionalParts(world, pos, rotatedState.getValue(CoreProperties.FACING));
		}
	}

	@Override
	public boolean shouldPlaceOnBuildPass(World world, int turns, BlockPos pos, int buildPass) {
		return buildPass == 0;
	}

	@Override
	public String getPluginName() {
		return PLUGIN_NAME;
	}

	@Override
	public boolean shouldReuseRule(World world, IBlockState state, int turns, BlockPos pos) {
		return false;
	}

	@Override
	public void writeRuleData(NBTTagCompound tag) {
		super.writeRuleData(tag);
		if (mainBlock) {
			tag.setByte("variant", (byte) variant.getId());
			tag.setBoolean("mainBlock", mainBlock);
		}
	}

	@Override
	public void parseRule(NBTTagCompound tag) {
		super.parseRule(tag);
		mainBlock = tag.getBoolean("mainBlock");
		if (mainBlock) {
			variant = BlockTotemPart.Variant.fromId(tag.getByte("variant"));
		}
	}

	@Nullable
	@Override
	public TileEntity getTileEntity(int turns) {
		TileTotemPart te = new TileTotemPart();
		te.setVariant(variant != null ? variant : BlockTotemPart.Variant.WINGS);
		return te;
	}
}
