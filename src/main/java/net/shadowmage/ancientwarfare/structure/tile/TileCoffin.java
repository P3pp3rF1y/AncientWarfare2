package net.shadowmage.ancientwarfare.structure.tile;

import com.google.common.collect.ImmutableSet;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.shadowmage.ancientwarfare.structure.block.BlockCoffin;

import java.util.Set;

public class TileCoffin extends TileMulti {
	private boolean upright = false;
	private BlockCoffin.CoffinDirection direction = BlockCoffin.CoffinDirection.NORTH;

	public int getVariant() {
		return variant;
	}

	public void setVariant(int variant) {
		this.variant = variant;
	}

	private int variant = 1;

	@Override
	public Set<BlockPos> getAdditionalPositions(IBlockState state) {
		return upright ? ImmutableSet.of(pos.up(), pos.up().up()) :
				ImmutableSet.of(pos.offset(direction.getFacing()), pos.offset(direction.getFacing()).offset(direction.getFacing()));
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		compound.setBoolean("upright", upright);
		compound.setString("direction", direction.getName());
		compound.setInteger("variant", variant);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound = super.writeToNBT(compound);
		upright = compound.getBoolean("upright");
		direction = BlockCoffin.CoffinDirection.fromName(compound.getString("direction"));
		variant = compound.getInteger("variant");
		return compound;
	}

	public void setUpright(boolean upright) {
		this.upright = upright;
	}

	public void setDirection(BlockCoffin.CoffinDirection direction) {
		this.direction = direction;
	}

	public BlockCoffin.CoffinDirection getDirection() {
		return direction;
	}

	public boolean getUpright() {
		return upright;
	}
}
