package net.shadowmage.ancientwarfare.structure.block;

import codechicken.lib.model.ModelRegistryHelper;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
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
import net.shadowmage.ancientwarfare.structure.render.ParticleSun;
import net.shadowmage.ancientwarfare.structure.render.ProtectionFlagRenderer;
import net.shadowmage.ancientwarfare.structure.tile.TileProtectionFlag;

import javax.annotation.Nullable;
import java.util.Random;

public class BlockProtectionFlag extends BlockFlag {
	public BlockProtectionFlag() {
		super(Material.WOOD, "protection_flag");
		setResistance(6000000F);
		setLightLevel(13 / 15F);
	}

	@Override
	public float getPlayerRelativeBlockHardness(IBlockState state, EntityPlayer player, World worldIn, BlockPos pos) {
		float original = super.getPlayerRelativeBlockHardness(state, player, worldIn, pos);
		return WorldTools.getTile(worldIn, pos, TileProtectionFlag.class).map(te -> te.getPlayerRelativeBlockHardness(player, original))
				.orElse(original);
	}

	@Nullable
	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileProtectionFlag();
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		WorldTools.getTile(world, pos, TileProtectionFlag.class).ifPresent(te -> te.onActivatedBy(player));
		return true;
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

		ClientRegistry.bindTileEntitySpecialRenderer(TileProtectionFlag.class, new ProtectionFlagRenderer());
	}

	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		int maxParticles = worldIn.rand.nextInt(10);
		for (int i = 0; i < maxParticles; i++) {

			double d0 = (double) pos.getX() + worldIn.rand.nextFloat();
			double d1 = (double) pos.getY() + 1.9D * worldIn.rand.nextFloat();
			double d2 = (double) pos.getZ() + worldIn.rand.nextFloat();
			worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0, d1, d2, 0.0D, 0.0D, 0.0D);
			Minecraft.getMinecraft().effectRenderer.addEffect(new ParticleSun(worldIn, d0, d1, d2));
		}
	}
}
