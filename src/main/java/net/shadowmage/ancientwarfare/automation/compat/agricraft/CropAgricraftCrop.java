package net.shadowmage.ancientwarfare.automation.compat.agricraft;

import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import com.infinityraider.agricraft.init.AgriBlocks;
import com.infinityraider.agricraft.tiles.TileEntityCrop;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;
import net.shadowmage.ancientwarfare.automation.tile.worksite.cropfarm.ICrop;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;
import net.shadowmage.ancientwarfare.core.util.WorldTools;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class CropAgricraftCrop implements ICrop {
	@Override
	public boolean matches(IBlockState state) {
		return state.getBlock() == AgriBlocks.getInstance().CROP;
	}

	@Override
	public List<BlockPos> getPositionsToHarvest(World world, BlockPos pos, IBlockState state) {
		Optional<TileEntityCrop> te = WorldTools.getTile(world, pos, TileEntityCrop.class);

		return te.filter(TileEntityCrop::isMature).map(tileEntityCrop -> Collections.singletonList(pos)).orElseGet(Collections::emptyList);
	}

	@Override
	public boolean canBeFertilized(IBlockState state, World world, BlockPos pos) {
		return WorldTools.getTile(world, pos, TileEntityCrop.class).filter(IAgriCrop::isFertile).isPresent();
	}

	@Override
	public boolean harvest(World world, IBlockState state, BlockPos pos, int fortune, IItemHandler inventory) {
		Optional<TileEntityCrop> te = WorldTools.getTile(world, pos, TileEntityCrop.class);

		if (!te.isPresent()) {
			return false;
		}

		TileEntityCrop crop = te.get();

		NonNullList<ItemStack> drops = NonNullList.create();

		//getting drops and setting stage separately instead of calling onHarvest because of inventory full check
		crop.getDrops(drops::add, false, false, true);

		if (!InventoryTools.canInventoryHold(inventory, drops) || !crop.setGrowthStage(0)) {
			return false;
		}

		InventoryTools.insertOrDropItems(inventory, drops, world, pos);

		return true;
	}
}
