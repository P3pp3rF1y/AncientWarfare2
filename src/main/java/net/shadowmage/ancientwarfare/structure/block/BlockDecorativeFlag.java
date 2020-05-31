package net.shadowmage.ancientwarfare.structure.block;

import codechicken.lib.model.ModelRegistryHelper;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.util.WorldTools;
import net.shadowmage.ancientwarfare.structure.init.AWStructureBlocks;
import net.shadowmage.ancientwarfare.structure.render.ParticleOnlyModel;
import net.shadowmage.ancientwarfare.structure.render.ProtectionFlagRenderer;
import net.shadowmage.ancientwarfare.structure.tile.TileDecorativeFlag;

import javax.annotation.Nullable;

public class BlockDecorativeFlag extends BlockFlag {
	public BlockDecorativeFlag() {
		super(Material.WOOD, "decorative_flag");
		setResistance(5.0F);
		setHardness(2.0F);
	}

	@Nullable
	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileDecorativeFlag();
	}

	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
		return WorldTools.getTile(world, pos, TileDecorativeFlag.class)
				.map(TileDecorativeFlag::getItemStack).orElse(new ItemStack(AWStructureBlocks.DECORATIVE_FLAG));
	}

	@Override
	public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, ItemStack stack) {
		if (te instanceof TileDecorativeFlag) {
			spawnAsEntity(worldIn, pos, ((TileDecorativeFlag) te).getItemStack());
		} else {
			super.harvestBlock(worldIn, player, pos, state, te, stack);
		}
	}

	@Override
	public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		drops.add(WorldTools.getTile(world, pos, TileDecorativeFlag.class)
				.map(TileDecorativeFlag::getItemStack).orElse(new ItemStack(AWStructureBlocks.DECORATIVE_FLAG)));
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		WorldTools.getTile(world, pos, TileDecorativeFlag.class).ifPresent(te -> te.setFromStack(stack));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerClient() {
		//noinspection ConstantConditions
		ModelResourceLocation modelLocation = new ModelResourceLocation(getRegistryName(), "normal");
		ModelRegistryHelper.registerItemRenderer(Item.getItemFromBlock(this), new ProtectionFlagRenderer());
		ModelRegistryHelper.register(modelLocation, ParticleOnlyModel.INSTANCE);
		ModelLoader.setCustomStateMapper(this, new StateMapperBase() {
			@Override
			@SideOnly(Side.CLIENT)
			protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
				return modelLocation;
			}
		});

		ClientRegistry.bindTileEntitySpecialRenderer(TileDecorativeFlag.class, new ProtectionFlagRenderer());
	}
}
