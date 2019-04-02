package net.shadowmage.ancientwarfare.structure.tile;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.shadowmage.ancientwarfare.core.util.WorldTools;
import net.shadowmage.ancientwarfare.structure.block.BlockTotemPart.Variant;

import java.util.Set;

import static net.shadowmage.ancientwarfare.core.render.property.CoreProperties.FACING;

public class TileTotemPart extends TileMulti {
	private static final String VARIANT_TAG = "variant";
	private Variant variant = Variant.BASE;
	private Variant dropVariant = Variant.BASE;

	public void setVariant(Variant variant) {
		this.variant = variant;
	}

	public Variant getVariant() {
		return variant;
	}

	@Override
	public void setMainBlockPos(BlockPos mainBlockPos) {
		super.setMainBlockPos(mainBlockPos);
		getMainBlockPos().ifPresent(mainPos -> WorldTools.getTile(world, mainPos, TileTotemPart.class).ifPresent(te -> dropVariant = te.getVariant()));
	}

	@Override
	protected void writeUpdateNBT(NBTTagCompound tag) {
		super.writeUpdateNBT(tag);
		tag.setByte(VARIANT_TAG, (byte) variant.getId());
	}

	@Override
	protected void handleUpdateNBT(NBTTagCompound tag) {
		super.handleUpdateNBT(tag);
		variant = Variant.fromId(tag.getByte(VARIANT_TAG));
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		variant = Variant.fromId(compound.getByte(VARIANT_TAG));
		getMainBlockPos().ifPresent(mainPos -> WorldTools.getTile(world, mainPos, TileTotemPart.class).ifPresent(te -> dropVariant = te.getVariant()));
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound = super.writeToNBT(compound);
		compound.setByte(VARIANT_TAG, (byte) variant.getId());
		return compound;
	}

	@Override
	public Set<BlockPos> getAdditionalPositions(IBlockState state) {
		return getVariant().getAdditionalPartPositions(pos, state.getValue(FACING));
	}

	public Variant getDropVariant() {
		return dropVariant;
	}
}
