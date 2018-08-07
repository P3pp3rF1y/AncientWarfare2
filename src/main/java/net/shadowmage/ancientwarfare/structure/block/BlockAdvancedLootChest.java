package net.shadowmage.ancientwarfare.structure.block;

import codechicken.lib.model.ModelRegistryHelper;
import net.minecraft.block.BlockChest;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.proxy.IClientRegister;
import net.shadowmage.ancientwarfare.structure.AncientWarfareStructures;
import net.shadowmage.ancientwarfare.structure.item.AWStructuresItemLoader;
import net.shadowmage.ancientwarfare.structure.render.ParticleDummyModel;
import net.shadowmage.ancientwarfare.structure.render.RenderAdvancedLootChest;
import net.shadowmage.ancientwarfare.structure.render.RenderItemAdvancedLootChest;
import net.shadowmage.ancientwarfare.structure.tile.TileAdvancedLootChest;

import javax.annotation.Nullable;
import java.util.Random;

public class BlockAdvancedLootChest extends BlockChest implements IClientRegister {
	protected BlockAdvancedLootChest() {
		super(Type.BASIC);
		setRegistryName(AncientWarfareStructures.MOD_ID, "advanced_loot_chest");
		setUnlocalizedName("advanced_loot_chest");
		setCreativeTab(AWStructuresItemLoader.structureTab);
		AncientWarfareStructures.proxy.addClientRegistrar(this);

		setHardness(2.5F);
		setSoundType(SoundType.WOOD);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerClient() {
		//noinspection ConstantConditions
		ModelResourceLocation modelLocation = new ModelResourceLocation(getRegistryName(), "normal");
		ModelRegistryHelper.registerItemRenderer(Item.getItemFromBlock(this), new RenderItemAdvancedLootChest());
		ModelRegistryHelper.register(modelLocation, ParticleDummyModel.INSTANCE);
		ModelLoader.setCustomStateMapper(this, new StateMapperBase() {
			@Override
			@SideOnly(Side.CLIENT)
			protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
				return modelLocation;
			}
		});

		ClientRegistry.bindTileEntitySpecialRenderer(TileAdvancedLootChest.class, new RenderAdvancedLootChest());
	}

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return Item.getItemFromBlock(Blocks.CHEST);
	}

	@Override
	public boolean hasTileEntity(IBlockState state) {
		return true;
	}

	@Nullable
	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileAdvancedLootChest();
	}
}
