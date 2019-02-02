package net.shadowmage.ancientwarfare.structure.block.altar;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.structure.render.ParticleSun;

import java.util.Random;

import static net.shadowmage.ancientwarfare.core.render.property.CoreProperties.FACING;

public class BlockAltarSun extends BlockAltarTop {
	private static final AxisAlignedBB AABB_NORTH_SOUTH = new AxisAlignedBB(1 / 16D, 0, 5 / 16D, 15 / 16D, 1D, 11 / 16D);
	private static final AxisAlignedBB AABB_WEST_EAST = new AxisAlignedBB(5 / 16D, 0, 1 / 16D, 11 / 16D, 1D, 15 / 16D);

	public BlockAltarSun() {
		super(Material.WOOD, "altar_sun");
		setLightLevel(12 / 15F);
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(FACING, EnumFacing.getHorizontal(meta));
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
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		switch (state.getValue(FACING)) {
			case WEST:
			case EAST:
				return AABB_WEST_EAST;
			case NORTH:
			case SOUTH:
			default:
				return AABB_NORTH_SOUTH;
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {
		return layer == BlockRenderLayer.CUTOUT;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand) {
		int maxParticles = world.rand.nextInt(3);
		for (int i = 0; i < maxParticles; i++) {
			double angle = world.rand.nextFloat() * Math.PI * 2d;
			float distance = world.rand.nextFloat() * 5 / 16f;
			double sunHorizontalOffset = 0.5d + Math.sin(angle) * distance;
			double distanceFromSun = 0.5d + (world.rand.nextBoolean() ? 0.1d : -0.1d);
			double yOffset = 0.55d + Math.cos(angle) * distance;
			EnumFacing.Axis facingAxis = state.getValue(FACING).getAxis();
			double xOffset = facingAxis == EnumFacing.Axis.X ? distanceFromSun : sunHorizontalOffset;
			double zOffset = facingAxis == EnumFacing.Axis.Z ? distanceFromSun : sunHorizontalOffset;
			Minecraft.getMinecraft().effectRenderer.addEffect(new ParticleSun(world, pos.getX() + xOffset, pos.getY() + yOffset, pos.getZ() + zOffset));
		}
	}
}
