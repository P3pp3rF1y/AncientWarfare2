package net.shadowmage.ancientwarfare.automation.item;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.automation.block.BlockWarehouseStockLinker;
import net.shadowmage.ancientwarfare.automation.tile.warehouse2.TileWarehouseStockLinker;
import net.shadowmage.ancientwarfare.core.item.ItemBlockOwnedRotatable;
import net.shadowmage.ancientwarfare.core.util.RayTraceUtils;

public class ItemBlockWarehouseStockLinker extends ItemBlockOwnedRotatable {

	public ItemBlockWarehouseStockLinker(BlockWarehouseStockLinker block) {
		super(block);
	}

	private void addMessage(EntityPlayer player) {
		player.sendMessage(new TextComponentTranslation("guistrings.automation.warehouse_set"));
	}

	@Override
	public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState){
		if (player.isSneaking() && !world.isRemote){
			RayTraceResult hit = RayTraceUtils.getPlayerTarget(player, 5, 0);
			TileWarehouseStockLinker stockLinker = TileWarehouseStockLinker.getStockLinker(stack);
			if (stockLinker != null) {
				if (hit != null && hit.typeOfHit == RayTraceResult.Type.BLOCK) {
					stockLinker.writeStack(stack, hit.getBlockPos());
					addMessage(player);
				}
				return false;
			}
		}
		return super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState);
	}
}
