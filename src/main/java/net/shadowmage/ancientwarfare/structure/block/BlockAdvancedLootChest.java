package net.shadowmage.ancientwarfare.structure.block;

import codechicken.lib.model.ModelRegistryHelper;
import net.minecraft.block.BlockChest;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ILockableContainer;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.proxy.IClientRegister;
import net.shadowmage.ancientwarfare.core.util.WorldTools;
import net.shadowmage.ancientwarfare.structure.AncientWarfareStructure;
import net.shadowmage.ancientwarfare.structure.render.ParticleOnlyModel;
import net.shadowmage.ancientwarfare.structure.render.RenderAdvancedLootChest;
import net.shadowmage.ancientwarfare.structure.render.RenderItemAdvancedLootChest;
import net.shadowmage.ancientwarfare.structure.tile.TileAdvancedLootChest;

import javax.annotation.Nullable;
import java.util.Random;

public class BlockAdvancedLootChest extends BlockChest implements IClientRegister {
	public BlockAdvancedLootChest() {
		super(Type.BASIC);
		setRegistryName(AncientWarfareStructure.MOD_ID, "advanced_loot_chest");
		setUnlocalizedName("advanced_loot_chest");
		setCreativeTab(AncientWarfareStructure.TAB);
		AncientWarfareStructure.proxy.addClientRegister(this);

		setHardness(2.5F);
		setSoundType(SoundType.WOOD);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerClient() {
		//noinspection ConstantConditions
		ModelResourceLocation modelLocation = new ModelResourceLocation(getRegistryName(), "normal");
		ModelRegistryHelper.registerItemRenderer(Item.getItemFromBlock(this), new RenderItemAdvancedLootChest());
		ModelRegistryHelper.register(modelLocation, ParticleOnlyModel.INSTANCE);
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

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (!world.isRemote && WorldTools.getTile(world, pos, TileAdvancedLootChest.class).map(te -> te.fillWithLootAndCheckIfGoodToOpen(player)).orElse(false)) {
			ILockableContainer ilockablecontainer = getLockableContainer(world, pos);
			if (ilockablecontainer != null) {
				player.displayGUIChest(ilockablecontainer);
				player.addStat(StatList.CHEST_OPENED);
			}
		}
		return true;
	}
}
