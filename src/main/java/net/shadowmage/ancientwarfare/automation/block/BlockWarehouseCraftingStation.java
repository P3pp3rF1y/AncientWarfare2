package net.shadowmage.ancientwarfare.automation.block;

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
import net.shadowmage.ancientwarfare.automation.gui.GuiWarehouseCraftingStation;
import net.shadowmage.ancientwarfare.automation.tile.warehouse2.TileWarehouseCraftingStation;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.util.WorldTools;

public class BlockWarehouseCraftingStation extends BlockBaseAutomation {

	public BlockWarehouseCraftingStation(String regName) {
		super(Material.ROCK, regName);
		setHardness(2.f);
	}

	@Override
	public boolean hasTileEntity(IBlockState state) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileWarehouseCraftingStation();
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		return WorldTools.clickInteractableTileWithHand(world, pos, player, hand);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerClient() {
		super.registerClient();

		NetworkHandler.registerGui(NetworkHandler.GUI_WAREHOUSE_CRAFTING, GuiWarehouseCraftingStation.class);
	}
}
