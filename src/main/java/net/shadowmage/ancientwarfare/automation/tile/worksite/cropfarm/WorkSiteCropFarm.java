package net.shadowmage.ancientwarfare.automation.tile.worksite.cropfarm;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockStem;
import net.minecraft.block.IGrowable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import net.shadowmage.ancientwarfare.automation.registry.CropFarmRegistry;
import net.shadowmage.ancientwarfare.automation.tile.worksite.TileWorksiteFarm;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;

import javax.annotation.Nonnull;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

public class WorkSiteCropFarm extends TileWorksiteFarm {
	private final Set<BlockPos> blocksToTill = new LinkedHashSet<>();
	private final Set<BlockPos> blocksToHarvest = new LinkedHashSet<>();
	private final Set<BlockPos> blocksToPlant = new LinkedHashSet<>();
	private final Set<BlockPos> blocksToFertilize = new LinkedHashSet<>();

	private final IItemHandler inventoryForDrops;

	public WorkSiteCropFarm() {
		super();
		inventoryForDrops = new CombinedInvWrapper(plantableInventory, mainInventory);
	}

	@Override
	protected boolean isPlantable(ItemStack stack) {
		return stack.getItem() instanceof IPlantable;
	}

	@Override
	protected boolean isFarmable(Block block, BlockPos farmablePos) {
		if (super.isFarmable(block, farmablePos)) {
			return ((IPlantable) block).getPlantType(world, farmablePos) == EnumPlantType.Crop;
		}
		return block instanceof BlockCrops || block instanceof BlockStem;
	}

	@Override
	public void onBoundsAdjusted() {
		validateCollection(blocksToFertilize);
		validateCollection(blocksToHarvest);
		validateCollection(blocksToPlant);
		validateCollection(blocksToTill);
	}

	@Override
	protected void scanBlockPosition(BlockPos position) {
		IBlockState state = world.getBlockState(position);
		Block block = world.getBlockState(position).getBlock();
		if (block.isReplaceable(world, position)) {
			IBlockState stateDown = world.getBlockState(position.down());
			if (CropFarmRegistry.isTillable(stateDown)) {
				blocksToTill.add(position.down());
			} else if (CropFarmRegistry.isPlantable(stateDown)) {
				blocksToPlant.add(position);
			}
		}

		IHarvestable harvestable = HarvestableFactory.getHarvestable(state);
		blocksToHarvest.addAll(harvestable.getPositionsToHarvest(world, position, state));

		if (harvestable.canBeFertilized(state, world, position)) {
			blocksToFertilize.add(position);
		}
	}

	@Override
	protected boolean processWork() {

		Iterator<BlockPos> it;
		BlockPos position;
		Block block;
		if (!blocksToTill.isEmpty()) {
			it = blocksToTill.iterator();
			while (it.hasNext() && (position = it.next()) != null) {
				it.remove();
				IBlockState state = world.getBlockState(position);
				if (CropFarmRegistry.isTillable(state) && canReplace(position.up())) {
					world.setBlockState(position, CropFarmRegistry.getTilledState(state));
					world.playSound(null, pos, SoundEvents.ITEM_HOE_TILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
					return true;
				}
			}
		} else if (!blocksToHarvest.isEmpty()) {
			it = blocksToHarvest.iterator();
			while (it.hasNext() && (position = it.next()) != null) {
				it.remove();
				IBlockState state = world.getBlockState(position);
				IHarvestable harvestable = HarvestableFactory.getHarvestable(state);
				return harvestable.harvest(world, state, position, getOwnerAsPlayer(), getFortune(), inventoryForDrops);
			}
		} else if (hasToPlant()) {
			it = blocksToPlant.iterator();
			while (it.hasNext() && (position = it.next()) != null) {
				it.remove();
				if (canReplace(position)) {
					@Nonnull ItemStack stack;
					for (int slot = 0; slot < plantableInventory.getSlots(); slot++) {
						stack = plantableInventory.getStackInSlot(slot);
						if (stack.isEmpty()) {
							continue;
						}
						if (isPlantable(stack)) {
							ItemStack clone = stack.copy();
							if (tryPlace(clone, position, EnumFacing.UP)) {
								plantableInventory.extractItem(slot, 1, false);
								return true;
							}
						}
					}
					return false;
				}
			}
		} else if (hasToFertilize()) {
			it = blocksToFertilize.iterator();
			while (it.hasNext() && (position = it.next()) != null) {
				it.remove();
				IBlockState state = world.getBlockState(position);
				block = state.getBlock();
				if (block instanceof IGrowable) {
					@Nonnull ItemStack stack;
					for (int slot = 0; slot < miscInventory.getSlots(); slot++) {
						stack = miscInventory.getStackInSlot(slot);
						if (stack.isEmpty()) {
							continue;
						}
						if (isBonemeal(stack)) {
							ItemStack clone = stack.copy();
							if (ItemDye.applyBonemeal(clone, world, position, getOwnerAsPlayer(), EnumHand.MAIN_HAND)) {
								miscInventory.extractItem(slot, 1, false);
								world.playEvent(2005, position, 0);
							}
							block = world.getBlockState(position).getBlock();
							if (block instanceof IGrowable) {
								if (((IGrowable) block).canGrow(world, position, state, world.isRemote)) {
									blocksToFertilize.add(position);
								} else if (isFarmable(block, position)) {
									blocksToHarvest.add(position);
								}
							}
							return true;
						}
					}
					return false;
				}
			}
		}
		return false;
	}

	@Override
	public WorkType getWorkType() {
		return WorkType.FARMING;
	}

	@Override
	public boolean onBlockClicked(EntityPlayer player, EnumHand hand) {
		if (!player.world.isRemote) {
			NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_WORKSITE_CROP_FARM, pos);
		}
		return true;
	}

	@Override
	protected boolean hasWorksiteWork() {
		return hasToPlant() || hasToFertilize() || !blocksToTill.isEmpty() || !blocksToHarvest.isEmpty();
	}

	private boolean hasToPlant() {
		return (plantableCount > 0 && !blocksToPlant.isEmpty());
	}

	private boolean hasToFertilize() {
		return (bonemealCount > 0 && !blocksToFertilize.isEmpty());
	}
}
