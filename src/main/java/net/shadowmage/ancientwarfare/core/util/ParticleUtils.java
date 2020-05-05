package net.shadowmage.ancientwarfare.core.util;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

@SideOnly(Side.CLIENT)
public class ParticleUtils {
	private ParticleUtils() {}

	private static Random rand = new Random();

	public static void playDestroyEffects(World world, BlockPos pos, int particle) {
		for (int j = 0; j < 4; ++j) {
			for (int k = 0; k < 4; ++k) {
				for (int l = 0; l < 4; ++l) {
					double d0 = ((double) j + 0.5D) / 4.0D;
					double d1 = ((double) k + 0.5D) / 4.0D;
					double d2 = ((double) l + 0.5D) / 4.0D;
					world.spawnParticle(EnumParticleTypes.BLOCK_CRACK, pos.getX() + d0, pos.getY() + d1, pos.getZ() + d2, d0 - 0.5D, d1 - 0.5D, d2 - 0.5D, particle);
				}
			}
		}
	}

	public static void playHitEffects(World world, RayTraceResult target, int particle) {
		BlockPos pos = target.getBlockPos();
		EnumFacing side = target.sideHit;
		IBlockState iblockstate = world.getBlockState(pos);
		if (iblockstate.getRenderType() != EnumBlockRenderType.INVISIBLE) {
			int i = pos.getX();
			int j = pos.getY();
			int k = pos.getZ();
			float f = 0.1F;
			AxisAlignedBB axisalignedbb = iblockstate.getBoundingBox(world, pos);
			double d0 = (double) i + rand.nextDouble() * (axisalignedbb.maxX - axisalignedbb.minX - 0.20000000298023224D) + 0.10000000149011612D + axisalignedbb.minX;
			double d1 = (double) j + rand.nextDouble() * (axisalignedbb.maxY - axisalignedbb.minY - 0.20000000298023224D) + 0.10000000149011612D + axisalignedbb.minY;
			double d2 = (double) k + rand.nextDouble() * (axisalignedbb.maxZ - axisalignedbb.minZ - 0.20000000298023224D) + 0.10000000149011612D + axisalignedbb.minZ;

			if (side == EnumFacing.DOWN) {
				d1 = (double) j + axisalignedbb.minY - 0.10000000149011612D;
			}

			if (side == EnumFacing.UP) {
				d1 = (double) j + axisalignedbb.maxY + 0.10000000149011612D;
			}

			if (side == EnumFacing.NORTH) {
				d2 = (double) k + axisalignedbb.minZ - 0.10000000149011612D;
			}

			if (side == EnumFacing.SOUTH) {
				d2 = (double) k + axisalignedbb.maxZ + 0.10000000149011612D;
			}

			if (side == EnumFacing.WEST) {
				d0 = (double) i + axisalignedbb.minX - 0.10000000149011612D;
			}

			if (side == EnumFacing.EAST) {
				d0 = (double) i + axisalignedbb.maxX + 0.10000000149011612D;
			}
			world.spawnParticle(EnumParticleTypes.BLOCK_CRACK, d0, d1, d2, 0.0D, 0.0D, 0.0D, particle);

		}
	}

}


