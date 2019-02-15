package net.shadowmage.ancientwarfare.structure.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

import static net.shadowmage.ancientwarfare.core.render.property.CoreProperties.FACING;

public class BlockTotemCube extends BlockBaseStructure {
	private boolean isLit;

	public BlockTotemCube(String regName, boolean isLit) {
		super(Material.ROCK, regName);
		this.isLit = isLit;
		if (isLit) {
			setLightLevel(0.875F);
		}
	}

	public BlockTotemCube(String regName) {
		this(regName, false);
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(FACING, EnumFacing.HORIZONTALS[meta]);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(FACING).getHorizontalIndex();
	}

	@Override
	public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		return getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
	}

	@Override
	public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		if (isLit) {
			double d0 = (double) pos.getX() + 0.5D;
			double d1 = (double) pos.getY() + rand.nextDouble() * 6.0D / 16.0D + 5/16D;
			double d2 = (double) pos.getZ() + 0.5D;
			double d4 = rand.nextDouble() * 0.6D - 0.3D;

			if (rand.nextDouble() < 0.1D) {
				worldIn.playSound((double) pos.getX() + 0.5D, (double) pos.getY(), (double) pos.getZ() + 0.5D, SoundEvents.BLOCK_FURNACE_FIRE_CRACKLE, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
			}

			EnumFacing enumfacing = stateIn.getValue(FACING);
			spawnParticles(worldIn, d0, d1, d2, d4, enumfacing);
			spawnParticles(worldIn, d0, d1, d2, d4, enumfacing.getOpposite());
		}
	}

	private void spawnParticles(World worldIn, double x, double y, double z, double d4, EnumFacing enumfacing) {
		switch (enumfacing) {
			case WEST:
				worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x - 0.52D, y, z + d4, 0.0D, 0.0D, 0.0D);
				worldIn.spawnParticle(EnumParticleTypes.FLAME, x - 0.52D, y, z + d4, 0.0D, 0.0D, 0.0D);
				break;
			case EAST:
				worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x + 0.52D, y, z + d4, 0.0D, 0.0D, 0.0D);
				worldIn.spawnParticle(EnumParticleTypes.FLAME, x + 0.52D, y, z + d4, 0.0D, 0.0D, 0.0D);
				break;
			case NORTH:
				worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x + d4, y, z - 0.52D, 0.0D, 0.0D, 0.0D);
				worldIn.spawnParticle(EnumParticleTypes.FLAME, x + d4, y, z - 0.52D, 0.0D, 0.0D, 0.0D);
				break;
			case SOUTH:
			default:
				worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x + d4, y, z + 0.52D, 0.0D, 0.0D, 0.0D);
				worldIn.spawnParticle(EnumParticleTypes.FLAME, x + d4, y, z + 0.52D, 0.0D, 0.0D, 0.0D);
		}
	}
}
