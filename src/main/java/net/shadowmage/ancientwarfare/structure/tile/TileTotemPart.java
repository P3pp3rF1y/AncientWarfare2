package net.shadowmage.ancientwarfare.structure.tile;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.shadowmage.ancientwarfare.core.tile.TileUpdatable;
import net.shadowmage.ancientwarfare.structure.block.BlockTotemPart.Variant;

import java.util.Optional;

public class TileTotemPart extends TileUpdatable {
	private static final String VARIANT_TAG = "variant";
	public static final String MAIN_BLOCK_POS_TAG = "mainBlockPos";
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
		if (compound.hasKey(MAIN_BLOCK_POS_TAG)) {
			mainBlockPos = BlockPos.fromLong(compound.getLong(MAIN_BLOCK_POS_TAG));
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound = super.writeToNBT(compound);
		compound.setByte(VARIANT_TAG, (byte) variant.getId());
		if (mainBlockPos != null) {
			compound.setLong(MAIN_BLOCK_POS_TAG, mainBlockPos.toLong());
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
