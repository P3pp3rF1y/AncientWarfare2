package net.shadowmage.ancientwarfare.structure.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.structure.gui.GuiStructureScannerBlock;
import net.shadowmage.ancientwarfare.structure.tile.TileStructureScanner;

import javax.annotation.Nullable;

public class BlockStructureScanner extends BlockBaseStructure {
	public BlockStructureScanner() {
		super(Material.WOOD, "structure_scanner_block");
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (!world.isRemote) {
			NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_STRUCTURE_SCANNER_BLOCK, pos);
		}
		return true;
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

		NetworkHandler.registerGui(NetworkHandler.GUI_STRUCTURE_SCANNER_BLOCK, GuiStructureScannerBlock.class);
	}
}
