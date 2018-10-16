package net.shadowmage.ancientwarfare.structure.tile;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.shadowmage.ancientwarfare.core.tile.TileUpdatable;
import net.shadowmage.ancientwarfare.structure.block.BlockTotemPart.Variant;

import java.util.Optional;

public class TileTotemPart extends TileUpdatable {
	private static final String VARIANT_TAG = "variant";
	private Variant variant = Variant.BASE;
	private BlockPos mainBlockPos = null;

	public void setVariant(Variant variant) {
		this.variant = variant;
	}

	public Variant getVariant() {
		return variant;
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
		mainBlockPos = BlockPos.fromLong(compound.getLong("mainBlockPos"));
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound = super.writeToNBT(compound);
		compound.setByte(VARIANT_TAG, (byte) variant.getId());
		if (mainBlockPos != null) {
			compound.setLong("mainBlockPos", mainBlockPos.toLong());
		}
		return compound;
	}

	public void setMainBlockPos(BlockPos mainBlockPos) {
		this.mainBlockPos = mainBlockPos;
	}

	public Optional<BlockPos> getMainBlockPos() {
		return Optional.ofNullable(mainBlockPos);
	}
}
