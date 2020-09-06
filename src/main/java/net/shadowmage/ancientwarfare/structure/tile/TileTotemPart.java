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
		this.dropVariant = variant;
	}

	public Variant getVariant() {
		return variant;
	}

	@Override
	public void setMainBlockPos(BlockPos mainBlockPos) {
		super.setMainBlockPos(mainBlockPos);
		getMainBlockPos().flatMap(mainPos -> WorldTools.getTile(world, mainPos, TileTotemPart.class)).ifPresent(te -> dropVariant = te.getVariant());
	}

	@Override
	protected void readNBT(NBTTagCompound compound) {
		super.readNBT(compound);
		variant = Variant.fromId(compound.getByte(VARIANT_TAG));
		getMainBlockPos().flatMap(mainPos -> WorldTools.getTile(world, mainPos, TileTotemPart.class)).ifPresent(te -> dropVariant = te.getVariant());
	}

	@Override
	protected void writeNBT(NBTTagCompound compound) {
		super.writeNBT(compound);
		compound.setByte(VARIANT_TAG, (byte) variant.getId());
	}

	@Override
	public Set<BlockPos> getAdditionalPositions(IBlockState state) {
		return getVariant().getAdditionalPartPositions(pos, state.getValue(FACING));
	}

	public Variant getDropVariant() {
		return dropVariant;
	}
}
