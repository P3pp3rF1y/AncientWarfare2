package net.shadowmage.ancientwarfare.structure.tile;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.shadowmage.ancientwarfare.structure.block.BlockCoffin;
import net.shadowmage.ancientwarfare.structure.block.BlockStoneCoffin;
import net.shadowmage.ancientwarfare.structure.init.AWStructureSounds;

import java.util.Map;
import java.util.Set;

public class TileStoneCoffin extends TileCoffin {
	private static final int TOTAL_OPEN_TIME = 60;
	private BlockStoneCoffin.Variant variant = BlockStoneCoffin.Variant.STONE;

	public void setVariant(BlockStoneCoffin.Variant variant) {
		this.variant = variant;
	}

	@Override
	public BlockStoneCoffin.Variant getVariant() {
		return getValueFromMain(TileStoneCoffin.class, TileStoneCoffin::getVariant, variant, () -> BlockStoneCoffin.Variant.STONE);
	}

	@Override
	public Set<BlockPos> getAdditionalPositions(IBlockState state) {
		return
				ImmutableSet.of(
						pos.offset(direction.getFacing()),
						pos.offset(direction.getFacing()).offset(direction.getFacing()),
						pos.offset(direction.getFacing()).offset(direction.getFacing()).offset(direction.getFacing()),
						pos.offset(direction.getFacing().rotateYCCW()),
						pos.offset(direction.getFacing().rotateYCCW()).offset(direction.getFacing()),
						pos.offset(direction.getFacing().rotateYCCW()).offset(direction.getFacing()).offset(direction.getFacing()),
						pos.offset(direction.getFacing().rotateYCCW()).offset(direction.getFacing()).offset(direction.getFacing()).offset(direction.getFacing())
				);
	}

	private static final Map<BlockCoffin.IVariant, SoundEvent> COFFIN_SOUNDS = ImmutableMap.of(
			BlockStoneCoffin.Variant.STONE, AWStructureSounds.STONE_COFFIN_OPENS,
			BlockStoneCoffin.Variant.SANDSTONE, AWStructureSounds.SANDSTONE_SARCOPHAGUS_OPENS,
			BlockStoneCoffin.Variant.PRISMARINE, AWStructureSounds.PRISMARINE_COFFIN_OPENS,
			BlockStoneCoffin.Variant.DEMONIC, AWStructureSounds.DEMONIC_COFFIN_OPENS);

	@Override
	protected void playSound() {
		world.playSound(null, pos, COFFIN_SOUNDS.get(variant), SoundCategory.BLOCKS, 1, 1);
	}

	@Override
	protected int getTotalOpenTime() {
		return TOTAL_OPEN_TIME;
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		Vec3i vec = direction.getFacing().getDirectionVec();
		return new AxisAlignedBB(pos.add(-3, 0, -3), pos.add(3, 2, 3)).expand(vec.getX(), vec.getY(), vec.getZ());
	}

	@Override
	protected void readNBT(NBTTagCompound compound) {
		super.readNBT(compound);
		variant = BlockStoneCoffin.Variant.fromName(compound.getString("variant"));
	}

	@Override
	protected void writeNBT(NBTTagCompound compound) {
		super.writeNBT(compound);
		compound.setString("variant", variant.getName());
	}
}
