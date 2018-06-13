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
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.automation.render.FlywheelStorageRenderer;
import net.shadowmage.ancientwarfare.automation.render.property.AutomationProperties;
import net.shadowmage.ancientwarfare.automation.tile.torque.multiblock.TileFlywheelStorage;
import net.shadowmage.ancientwarfare.core.render.BlockStateKeyGenerator;
import net.shadowmage.ancientwarfare.core.util.ModelLoaderHelper;

import static net.shadowmage.ancientwarfare.automation.render.property.AutomationProperties.*;

public class BlockFlywheelStorage extends BlockBaseAutomation implements IBakeryProvider {

	public BlockFlywheelStorage(String regName) {
		super(Material.ROCK, regName);
	}

	@Override
	public boolean eventReceived(IBlockState state, World world, BlockPos pos, int id, int param) {
		TileEntity tileentity = world.getTileEntity(pos);
		return tileentity != null && tileentity.receiveClientEvent(id, param);
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer.Builder(this).add(TIER).add(DYNAMIC, IS_CONTROL, HEIGHT, WIDTH, ROTATION).build();
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(TIER, TorqueTier.byMetadata(meta));
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(TIER).getMeta();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
		return FlywheelStorageRenderer.INSTANCE.handleState((IExtendedBlockState) state, world, pos);
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		super.onBlockPlacedBy(world, pos, state, placer, stack);
		TileFlywheelStorage te = (TileFlywheelStorage) world.getTileEntity(pos);
		te.blockPlaced();
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isNormalCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		TileFlywheelStorage te = (TileFlywheelStorage) world.getTileEntity(pos);
		super.breakBlock(world, pos, state);
		te.blockBroken();//have to call post block-break so that the controller properly sees the block as gone //TODO this should probably be invalidate
	}

	@Override
	public int damageDropped(IBlockState state) {
		return state.getValue(TIER).getMeta();
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileFlywheelStorage();
	}

	@Override
	public boolean hasTileEntity(IBlockState state) {
		return true;
	}

	@Override
	public void getSubBlocks(CreativeTabs creativeTab, NonNullList<ItemStack> list) {
		list.add(new ItemStack(Item.getItemFromBlock(this), 1, 0));
		list.add(new ItemStack(Item.getItemFromBlock(this), 1, 1));
		list.add(new ItemStack(Item.getItemFromBlock(this), 1, 2));
	}

	@Override
	public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {
		return layer == BlockRenderLayer.SOLID || layer == BlockRenderLayer.TRANSLUCENT;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerClient() {
		ModelLoaderHelper.registerItem(this, FlywheelStorageRenderer.LIGHT_MODEL_LOCATION); //the actual switch for itemstack types is processed by renderer

		ModelBakery.registerBlockKeyGenerator(this, new BlockStateKeyGenerator.Builder().addKeyProperties(TIER).addKeyProperties(DYNAMIC, IS_CONTROL, WIDTH, HEIGHT).addKeyProperties(o -> String.format("%.6f", o), ROTATION).build());

		ModelLoader.setCustomStateMapper(this, new StateMapperBase() {
			@Override
			@SideOnly(Side.CLIENT)
			protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
				switch (state.getValue(AutomationProperties.TIER)) {
					case LIGHT:
						return FlywheelStorageRenderer.LIGHT_MODEL_LOCATION;
					case MEDIUM:
						return FlywheelStorageRenderer.MEDIUM_MODEL_LOCATION;
					default:
						return FlywheelStorageRenderer.HEAVY_MODEL_LOCATION;
				}
			}
		});

		ModelRegistryHelper.register(FlywheelStorageRenderer.LIGHT_MODEL_LOCATION, new CCBakeryModel() {
			@Override
			@SideOnly(Side.CLIENT)
			public TextureAtlasSprite getParticleTexture() {
				return FlywheelStorageRenderer.INSTANCE.getSprite(false, TorqueTier.LIGHT);
			}
		});

		ModelRegistryHelper.register(FlywheelStorageRenderer.MEDIUM_MODEL_LOCATION, new CCBakeryModel() {
			@Override
			@SideOnly(Side.CLIENT)
			public TextureAtlasSprite getParticleTexture() {
				return FlywheelStorageRenderer.INSTANCE.getSprite(false, TorqueTier.MEDIUM);
			}
		});

		ModelRegistryHelper.register(FlywheelStorageRenderer.HEAVY_MODEL_LOCATION, new CCBakeryModel() {
			@Override
			@SideOnly(Side.CLIENT)
			public TextureAtlasSprite getParticleTexture() {
				return FlywheelStorageRenderer.INSTANCE.getSprite(false, TorqueTier.HEAVY);
			}
		});

	}

	@Override
	@SideOnly(Side.CLIENT)
	public IBakery getBakery() {
		return FlywheelStorageRenderer.INSTANCE;
	}
}
