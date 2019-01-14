package net.shadowmage.ancientwarfare.npc.entity.vehicle;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Optional;

public class TargetFactory {
	private TargetFactory() {
	}

	public static Optional<NBTTagCompound> serializeNBT(ITarget target, NBTTagCompound tag) {
		if (target instanceof BlockPosTarget) {
			((BlockPosTarget) target).serializeToNBT(tag);
			return Optional.of(tag);
		}

		//entity target doesn't need serializing as it will be recreated by setAttackTarget call on entity load
		return Optional.empty();
	}

	public static ITarget deserializeFromNBT(NBTTagCompound tag) {
		if (tag.hasKey("targetPos")) {
			return new BlockPosTarget(BlockPos.fromLong(tag.getLong("targetPos")));
		}
		return NONE;
	}

	public static final ITarget NONE = new ITarget() {
		@Override
		public double getX() {
			return 0;
		}

		@Override
		public double getY() {
			return 0;
		}

		@Override
		public double getZ() {
			return 0;
		}

		private final AxisAlignedBB noBounds = new AxisAlignedBB(0, 0, 0, 0, 0, 0);

		@Override
		public AxisAlignedBB getBoundigBox() {
			return noBounds;
		}

		@Override
		public boolean exists(World entityWorld) {
			return false;
		}
	};
}
