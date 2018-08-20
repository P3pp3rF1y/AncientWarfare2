package net.shadowmage.ancientwarfare.structure.block;

import net.minecraft.block.BlockDirectional;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.util.WorldTools;
import net.shadowmage.ancientwarfare.structure.render.StructureScannerRenderer;
import net.shadowmage.ancientwarfare.structure.tile.TileStructureScanner;

import javax.annotation.Nullable;
import java.util.Optional;

public class BlockStructureScanner extends BlockBaseStructure {
	public static final PropertyDirection FACING = BlockDirectional.FACING;

	public BlockStructureScanner() {
		super(Material.WOOD, "structure_scanner_block");
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (!world.isRemote) {
			NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_SCANNER, pos);
		}
		return true;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING);
	}

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
		Optional<TileStructureScanner> tile = WorldTools.getTile(worldIn, pos, TileStructureScanner.class);
		return tile.map(tileStructureScanner -> state.withProperty(FACING, tileStructureScanner.getRenderFacing())).orElse(state);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return 0;
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		WorldTools.getTile(worldIn, pos, TileStructureScanner.class).ifPresent(t -> t.setFacing(placer.getHorizontalFacing().getOpposite()));
	}

	@Override
	public boolean hasTileEntity(IBlockState state) {
		return true;
	}

	@Nullable
	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileStructureScanner();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerClient() {
		super.registerClient();

		ClientRegistry.bindTileEntitySpecialRenderer(TileStructureScanner.class, new StructureScannerRenderer());
	}
}
