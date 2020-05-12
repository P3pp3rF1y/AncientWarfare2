package net.shadowmage.ancientwarfare.structure.tile;

import com.google.common.collect.ImmutableSet;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.structure.block.BlockCoffin;
import net.shadowmage.ancientwarfare.structure.block.BlockWoodenCoffin;
import net.shadowmage.ancientwarfare.structure.init.AWStructureSounds;

import java.util.Set;

public class TileWoodenCoffin extends TileCoffin {
	private static final int TOTAL_OPEN_TIME = 20;

	private boolean upright = false;

	@Override
	protected int getTotalOpenTime() {
		return TOTAL_OPEN_TIME;
	}

	@Override
	public Set<BlockPos> getAdditionalPositions(IBlockState state) {
		return upright ? ImmutableSet.of(pos.up(), pos.up().up()) :
				ImmutableSet.of(pos.offset(direction.getFacing()), pos.offset(direction.getFacing()).offset(direction.getFacing()));
	}

	@Override
	protected BlockCoffin.IVariant getDefaultVariant() {
		return BlockWoodenCoffin.Variant.getDefault();
	}

	@Override
	public void setPlacementDirection(World world, BlockPos pos, IBlockState state, EnumFacing horizontalFacing, float rotationYaw) {
		setDirection(upright ? BlockCoffin.CoffinDirection.fromYaw(rotationYaw) : BlockCoffin.CoffinDirection.fromFacing(horizontalFacing));
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		if (upright) {
			return new AxisAlignedBB(pos.add(-1, 0, -1), pos.add(2, 3, 2));
		}

		Vec3i vec = direction.getFacing().getDirectionVec();
		return new AxisAlignedBB(pos.add(-1, 0, -1), pos.add(2, 1, 2)).expand(vec.getX(), vec.getY(), vec.getZ());
	}

	@Override
	protected void readNBT(NBTTagCompound compound) {
		super.readNBT(compound);
		upright = compound.getBoolean("upright");
	}

	@Override
	protected BlockCoffin.IVariant deserializeVariant(String name) {
		return BlockWoodenCoffin.Variant.fromName(name);
	}

	@Override
	protected void writeNBT(NBTTagCompound compound) {
		super.writeNBT(compound);
		compound.setBoolean("upright", upright);
	}

	@Override
	protected void playSound(BlockCoffin.IVariant variant) {
		world.playSound(null, pos, AWStructureSounds.COFFIN_OPENS, SoundCategory.BLOCKS, 1, 1);
	}

	public void setUpright(boolean upright) {
		this.upright = upright;
	}

	public boolean getUpright() {
		return upright;
	}
}
