package net.shadowmage.ancientwarfare.structure.block;

import codechicken.lib.model.DummyBakedModel;
import codechicken.lib.model.ModelRegistryHelper;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.proxy.IClientRegister;
import net.shadowmage.ancientwarfare.core.util.ModelLoaderHelper;
import net.shadowmage.ancientwarfare.core.util.WorldTools;
import net.shadowmage.ancientwarfare.structure.AncientWarfareStructures;
import net.shadowmage.ancientwarfare.structure.tile.TEGateProxy;

import javax.annotation.Nullable;

public final class BlockGateProxy extends BlockContainer implements IClientRegister {
	private static final AxisAlignedBB Z_AXIS_AABB = new AxisAlignedBB(8D / 16D, 0, 0, 8D / 16D, 1, 1);
	private static final AxisAlignedBB X_AXIS_AABB = new AxisAlignedBB(0, 0, 8D / 16D, 1, 1, 8D / 16D);
	private static final AxisAlignedBB ZERO_AABB = new AxisAlignedBB(0, 0, 0, 0, 0, 0);

	public BlockGateProxy() {
		super(Material.ROCK);
		setCreativeTab(null);
		setUnlocalizedName("gate_proxy");
		setRegistryName(new ResourceLocation(AncientWarfareStructures.MOD_ID, "gate_proxy"));
		setResistance(2000.f);
		setHardness(5.f);
		AncientWarfareStructures.proxy.addClientRegistrar(this);
	}

	@Override
	public boolean hasTileEntity(IBlockState state) {
		return true;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {
		return new TEGateProxy();
	}

	@Override
	public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		//nothing gets dropped
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Nullable
	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
		return (world.getBlockState(pos.offset(EnumFacing.WEST)).getBlock() == this || world.getBlockState(pos.offset(EnumFacing.EAST)).getBlock() == this)
				? X_AXIS_AABB : Z_AXIS_AABB;
	}

	@Override
	public boolean canCollideCheck(IBlockState state, boolean hitIfLiquid) {
		return true;
	}

	@Override
	public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World world, BlockPos pos) {
		return ZERO_AABB;
	}

	//Actually "can go through", for mob pathing
	@Override
	public boolean isPassable(IBlockAccess world, BlockPos pos) {
		if (WorldTools.getTile(world, pos, TEGateProxy.class).map(TEGateProxy::isGateClosed).orElse(false)) {
			return false;
		}

		//Gate is probably open, Search identical neighbour
		if (world.getBlockState(pos.offset(EnumFacing.WEST)).getBlock() == this) {
			return world.getBlockState(pos.offset(EnumFacing.EAST)).getBlock() == this;
		} else if (world.getBlockState(pos.offset(EnumFacing.NORTH)).getBlock() == this) {
			return world.getBlockState(pos.offset(EnumFacing.SOUTH)).getBlock() == this;
		}
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerClient() {
		//noinspection ConstantConditions
		ModelResourceLocation modelLocation = new ModelResourceLocation(getRegistryName(), "normal");
		ModelLoaderHelper.registerItem(this, modelLocation);
		ModelLoader.setCustomStateMapper(this, new StateMapperBase() {
			@Override
			protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
				return modelLocation;
			}
		});
		ModelRegistryHelper.register(modelLocation, new DummyBakedModel());
	}
}
