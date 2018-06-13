package net.shadowmage.ancientwarfare.automation.block;

import codechicken.lib.model.ModelRegistryHelper;
import codechicken.lib.model.bakery.CCBakeryModel;
import codechicken.lib.model.bakery.IBakeryProvider;
import codechicken.lib.model.bakery.ModelBakery;
import codechicken.lib.model.bakery.generation.IBakery;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.common.property.PropertyFloat;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.automation.render.FlywheelControllerRenderer;
import net.shadowmage.ancientwarfare.automation.render.property.AutomationProperties;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileFlywheelControllerHeavy;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileFlywheelControllerLight;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileFlywheelControllerMedium;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.RotationType;
import net.shadowmage.ancientwarfare.core.render.BlockStateKeyGenerator;
import net.shadowmage.ancientwarfare.core.render.property.CoreProperties;
import net.shadowmage.ancientwarfare.core.util.ModelLoaderHelper;

public class BlockFlywheelController extends BlockTorqueBase implements IBakeryProvider {
	public static final IUnlistedProperty<Float> FLYWHEEL_ROTATION = new PropertyFloat("flywheel_rotation");

	public BlockFlywheelController(String regName) {
		super(Material.ROCK, regName);
	}

	@Override
	protected void addProperties(BlockStateContainer.Builder builder) {
		builder.add(AutomationProperties.TIER).add(FLYWHEEL_ROTATION, AutomationProperties.USE_INPUT, AutomationProperties.INPUT_ROTATION);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(AutomationProperties.TIER, TorqueTier.byMetadata(meta));
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(AutomationProperties.TIER).getMeta();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
		return FlywheelControllerRenderer.INSTANCE.handleState((IExtendedBlockState) state, world, pos);
	}

	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
		return getStateFromMeta(placer.getHeldItem(hand).getMetadata());
	}

	@Override
	public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		switch (state.getValue(AutomationProperties.TIER)) {
			case LIGHT:
				return new TileFlywheelControllerLight();
			case MEDIUM:
				return new TileFlywheelControllerMedium();
			case HEAVY:
				return new TileFlywheelControllerHeavy();
		}
		return null;
	}

	@Override
	public void getSubBlocks(CreativeTabs item, NonNullList<ItemStack> items) {
		items.add(new ItemStack(this, 1, 0));
		items.add(new ItemStack(this, 1, 1));
		items.add(new ItemStack(this, 1, 2));
	}

	@Override
	public boolean invertFacing() {
		return false;
	}

	@Override
	public RotationType getRotationType() {
		return RotationType.FOUR_WAY;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerClient() {
		ModelLoaderHelper.registerItem(this, "automation", "light", false); //the actual switch for itemstack types is processed by renderer

		ModelBakery.registerBlockKeyGenerator(this, new BlockStateKeyGenerator.Builder().addKeyProperties(AutomationProperties.TIER).addKeyProperties(CoreProperties.UNLISTED_FACING, AutomationProperties.DYNAMIC, FLYWHEEL_ROTATION, AutomationProperties.USE_INPUT).addKeyProperties(o -> String.format("%.6f", o), AutomationProperties.ROTATIONS).addKeyProperties(o -> String.format("%.6f", o), AutomationProperties.INPUT_ROTATION).build());

		ModelLoader.setCustomStateMapper(this, new StateMapperBase() {
			@Override
			@SideOnly(Side.CLIENT)
			protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
				switch (state.getValue(AutomationProperties.TIER)) {
					case LIGHT:
						return FlywheelControllerRenderer.LIGHT_MODEL_LOCATION;
					case MEDIUM:
						return FlywheelControllerRenderer.MEDIUM_MODEL_LOCATION;
					default:
						return FlywheelControllerRenderer.HEAVY_MODEL_LOCATION;
				}
			}
		});

		ModelRegistryHelper.register(FlywheelControllerRenderer.LIGHT_MODEL_LOCATION, new CCBakeryModel() {
			@Override
			@SideOnly(Side.CLIENT)
			public TextureAtlasSprite getParticleTexture() {
				return FlywheelControllerRenderer.INSTANCE.getSprite(TorqueTier.LIGHT);
			}
		});

		ModelRegistryHelper.register(FlywheelControllerRenderer.MEDIUM_MODEL_LOCATION, new CCBakeryModel() {
			@Override
			@SideOnly(Side.CLIENT)
			public TextureAtlasSprite getParticleTexture() {
				return FlywheelControllerRenderer.INSTANCE.getSprite(TorqueTier.MEDIUM);
			}
		});

		ModelRegistryHelper.register(FlywheelControllerRenderer.HEAVY_MODEL_LOCATION, new CCBakeryModel() {
			@Override
			@SideOnly(Side.CLIENT)
			public TextureAtlasSprite getParticleTexture() {
				return FlywheelControllerRenderer.INSTANCE.getSprite(TorqueTier.HEAVY);
			}
		});

	}

	@Override
	@SideOnly(Side.CLIENT)
	public IBakery getBakery() {
		return FlywheelControllerRenderer.INSTANCE;
	}
}
