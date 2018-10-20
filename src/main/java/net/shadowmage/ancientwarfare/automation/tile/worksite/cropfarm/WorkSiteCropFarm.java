package net.shadowmage.ancientwarfare.automation.tile.worksite.cropfarm;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockStem;
import net.minecraft.block.IGrowable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
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
import net.shadowmage.ancientwarfare.automation.tile.worksite.IWorksiteAction;
import net.shadowmage.ancientwarfare.automation.tile.worksite.TileWorksiteFarm;
import net.shadowmage.ancientwarfare.core.entity.AWFakePlayer;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Optional;
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
		return CropFarmRegistry.getCrop(stack).isPlantable(stack);
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
			} else if (CropFarmRegistry.isSoil(stateDown)) {
				blocksToPlant.add(position);
			}
		}

		if (state.getBlock() == Blocks.AIR) {
			return;
		}

		ICrop crop = CropFarmRegistry.getCrop(state);
		blocksToHarvest.addAll(crop.getPositionsToHarvest(world, position, state));

		if (crop.canBeFertilized(state, world, position)) {
			blocksToFertilize.add(position);
		}
	}

	@Override
	public WorkType getWorkType() {
		return WorkType.FARMING;
	}

	@Override
	public boolean onBlockClicked(EntityPlayer player, @Nullable EnumHand hand) {
		if (!player.world.isRemote) {
			NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_WORKSITE_CROP_FARM, pos);
		}
		return true;
	}

	private boolean hasToPlant() {
		return (plantableCount > 0 && !blocksToPlant.isEmpty());
	}

	private boolean hasToFertilize() {
		return (bonemealCount > 0 && !blocksToFertilize.isEmpty());
	}

	private static final IWorksiteAction PLANT_ACTION = e -> WorksiteImplementation.getEnergyPerActivation(e) / 5D;
	private static final IWorksiteAction FERTILIZE_ACTION = e -> WorksiteImplementation.getEnergyPerActivation(e) / 5D;
	private static final IWorksiteAction TILL_ACTION = e -> WorksiteImplementation.getEnergyPerActivation(e) / 5D;
	private static final IWorksiteAction HARVEST_ACTION = e -> WorksiteImplementation.getEnergyPerActivation(e) / 5D;

	@Override
	protected Optional<IWorksiteAction> getNextAction() {
		if (!blocksToHarvest.isEmpty()) {
			return Optional.of(HARVEST_ACTION);
		} else if (hasToFertilize()) {
			return Optional.of(FERTILIZE_ACTION);
		} else if (hasToPlant()) {
			return Optional.of(PLANT_ACTION);
		} else if (!blocksToTill.isEmpty()) {
			return Optional.of(TILL_ACTION);
		}

		return Optional.empty();
	}

	@Override
	protected boolean processAction(IWorksiteAction action) {
		if (action == TILL_ACTION) {
			return tryTill();
		} else if (action == HARVEST_ACTION) {
			return tryHarvest();
		} else if (action == PLANT_ACTION) {
			return tryPlant();
		} else if (action == FERTILIZE_ACTION) {
			return tryFertilize();
		}
		return false;
	}

	private boolean tryFertilize() {
		Iterator<BlockPos> it = blocksToFertilize.iterator();
		BlockPos position;
		while (it.hasNext() && (position = it.next()) != null) {
			it.remove();
			IBlockState state = world.getBlockState(position);
			Block block = state.getBlock();
			if (block instanceof IGrowable) {
				for (int slot = 0; slot < miscInventory.getSlots(); slot++) {
					ItemStack stack = miscInventory.getStackInSlot(slot);
					if (stack.isEmpty()) {
						continue;
					}
					if (isBonemeal(stack)) {
						ItemStack clone = stack.copy();
						if (ItemDye.applyBonemeal(clone, world, position, AWFakePlayer.get(world), EnumHand.MAIN_HAND)) {
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
		return false;
	}

	private boolean tryPlant() {
		Iterator<BlockPos> it = blocksToPlant.iterator();
		BlockPos position;
		while (it.hasNext() && (position = it.next()) != null) {
			it.remove();
			if (canReplace(position)) {
				for (int slot = 0; slot < plantableInventory.getSlots(); slot++) {
					ItemStack stack = plantableInventory.getStackInSlot(slot);
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
		return false;
	}

	private boolean tryHarvest() {
		Iterator<BlockPos> it = blocksToHarvest.iterator();
		BlockPos position;
		if (it.hasNext() && (position = it.next()) != null) {
			it.remove();
			IBlockState state = world.getBlockState(position);
			ICrop crop = CropFarmRegistry.getCrop(state);
			return crop.harvest(world, state, position, getFortune(), inventoryForDrops);
		}
		return false;
	}

	private boolean tryTill() {
		Iterator<BlockPos> it = blocksToTill.iterator();
		BlockPos position;
		while (it.hasNext() && (position = it.next()) != null) {
			it.remove();
			IBlockState state = world.getBlockState(position);
			if (CropFarmRegistry.isTillable(state) && canReplace(position.up())) {
				world.setBlockState(position, CropFarmRegistry.getTilledState(state));
				world.playSound(null, pos, SoundEvents.ITEM_HOE_TILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
				return true;
			}
		}
		return false;
	}
}
