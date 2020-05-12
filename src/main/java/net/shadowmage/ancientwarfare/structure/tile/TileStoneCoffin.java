package net.shadowmage.ancientwarfare.structure.tile;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import net.minecraft.block.state.IBlockState;
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
	protected BlockCoffin.IVariant getDefaultVariant() {
		return BlockStoneCoffin.Variant.getDefault();
	}

	@Override
	protected BlockCoffin.IVariant deserializeVariant(String name) {
		return BlockStoneCoffin.Variant.fromName(name);
	}

	@Override
	protected void playSound(BlockCoffin.IVariant variant) {
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
}
