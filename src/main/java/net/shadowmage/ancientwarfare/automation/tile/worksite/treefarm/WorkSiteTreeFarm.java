package net.shadowmage.ancientwarfare.automation.tile.worksite.treefarm;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.IGrowable;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.common.util.Constants;
import net.shadowmage.ancientwarfare.automation.registry.TreeFarmRegistry;
import net.shadowmage.ancientwarfare.automation.tile.worksite.TileWorksiteFarm;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class WorkSiteTreeFarm extends TileWorksiteFarm {
	private boolean hasShears;
	private final Set<BlockPos> blocksToShear;
	private final Set<BlockPos> blocksToChop;
	private final Set<BlockPos> blocksToPlant;
	private final Set<BlockPos> blocksToFertilize;

	public WorkSiteTreeFarm() {
		super();
		blocksToChop = new LinkedHashSet<>();
		blocksToPlant = new HashSet<>();
		blocksToFertilize = new HashSet<>();
		blocksToShear = new LinkedHashSet<>();
	}

	@Override
	protected boolean isPlantable(ItemStack stack) {
		return TreeFarmRegistry.isPlantable(stack);
	}

	@Override
	protected boolean isMiscItem(ItemStack stack) {
		return stack.getItem() == Items.SHEARS || super.isMiscItem(stack);
	}

	@Override
	public void onBoundsAdjusted() {
		validateCollection(blocksToFertilize);
		validateCollection(blocksToChop);
		validateCollection(blocksToPlant);
		if (!hasShears) {
			blocksToShear.clear();
		}
		markDirty();
	}

	@Override
	protected void countResources() {
		super.countResources();
		hasShears = InventoryTools.getCountOf(miscInventory, s -> s.getItem() == Items.SHEARS) > 0;
	}

	@Override
	protected boolean processWork() {
		return shearBlock() || chopBlock() || plant() || bonemealBlock();
	}

	private boolean bonemealBlock() {
		if (bonemealCount <= 0 || blocksToFertilize.isEmpty()) {
			return false;
		}

		Iterator<BlockPos> it = blocksToFertilize.iterator();
		BlockPos position = it.next();
		it.remove();

		IBlockState state = world.getBlockState(position);

		return canFertilize(world, position, state) && fertilize(position);

	}

	private boolean plant() {
		if (plantableCount <= 0 || blocksToPlant.isEmpty()) {
			return false;
		}

		Optional<ItemStack> plantable = InventoryTools.stream(plantableInventory).filter(this::isPlantable).findFirst();
		if (plantable.isPresent()) {
			Iterator<BlockPos> it = blocksToPlant.iterator();
			BlockPos position = it.next();
			it.remove();
			if (isUnwantedPlant(world.getBlockState(position).getBlock())) {
				world.setBlockToAir(position);
			}
			if (canReplace(position) && (tryPlace(plantable.get().copy(), position, EnumFacing.UP) || tryPlace(plantable.get().copy(), position, EnumFacing.DOWN))) {
				InventoryTools.removeItems(plantableInventory, plantable.get(), 1);
				return true;
			}
		}

		return false;
	}

	private boolean chopBlock() {
		if (blocksToChop.isEmpty()) {
			return false;
		}

		Iterator<BlockPos> it = blocksToChop.iterator();
		BlockPos position = it.next();
		it.remove();
		return harvestBlock(position);
	}

	private boolean shearBlock() {
		if (!hasShears || blocksToShear.isEmpty()) {
			return false;
		}

		Iterator<BlockPos> it = blocksToShear.iterator();
		BlockPos position = it.next();
		it.remove();
		Block block = world.getBlockState(position).getBlock();
		if (block instanceof IShearable) {
			Optional<ItemStack> shears = InventoryTools.stream(miscInventory).filter(s -> s.getItem() instanceof ItemShears).findFirst();

			if (shears.isPresent() && shear(position, (IShearable) block, shears.get())) {
				return true;
			}
		}

		return false;
	}

	private boolean shear(BlockPos position, IShearable block, ItemStack shears) {
		if (block.isShearable(shears, world, position)) {
			NonNullList<ItemStack> drops = InventoryTools.toNonNullList(block.onSheared(shears, world, position, getFortune()));
			drops = InventoryTools.insertItems(plantableInventory, drops, false);
			InventoryTools.insertOrDropItems(mainInventory, drops, world, pos);
			world.setBlockToAir(position);
			return true;
		}
		return false;
	}

	private void addTreeBlocks(IBlockState state, BlockPos basePos) {
		world.profiler.startSection("TreeFinder");
		int chops = blocksToChop.size();
		ITree tree = TreeFarmRegistry.getTreeScanner(state).scanTree(world, basePos);
		List<BlockPos> leafBlocks = tree.getLeafPositions();
		if (hasShears) {
			blocksToShear.addAll(leafBlocks);
		} else {
			blocksToChop.addAll(leafBlocks);
		}
		blocksToChop.addAll(tree.getTrunkPositions());

		if (blocksToChop.size() != chops) {
			markDirty();
		}
		world.profiler.endSection();
	}

	@Override
	public WorkType getWorkType() {
		return WorkType.FORESTRY;
	}

	@Override
	public boolean onBlockClicked(EntityPlayer player, @Nullable EnumHand hand) {
		if (!player.world.isRemote) {
			NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_WORKSITE_TREE_FARM, pos);
		}
		return true;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		if (!blocksToChop.isEmpty()) {
			NBTTagList chopList = new NBTTagList();
			for (BlockPos position : blocksToChop) {
				chopList.appendTag(new NBTTagLong(position.toLong()));
			}
			tag.setTag("targetList", chopList);
		}
		return tag;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		blocksToChop.clear();
		if (tag.hasKey("targetList")) {
			NBTTagList chopList = tag.getTagList("targetList", Constants.NBT.TAG_LONG);
			for (int i = 0; i < chopList.tagCount(); i++) {
				blocksToChop.add(BlockPos.fromLong(((NBTTagLong) chopList.get(i)).getLong()));
			}
		}
	}

	@Override
	protected void scanBlockPosition(BlockPos scanPos) {
		if (canReplace(scanPos)) {
			IBlockState state = world.getBlockState(scanPos.down());
			if (TreeFarmRegistry.isSoil(state) || (state.getMaterial() == Material.AIR && TreeFarmRegistry.isSoil(world.getBlockState(scanPos.up())))) {
				blocksToPlant.add(scanPos);
			}
		} else {
			IBlockState state = world.getBlockState(scanPos);
			Block block = state.getBlock();
			if (canFertilize(world, scanPos, state)) {
				blocksToFertilize.add(scanPos);
			} else if (state.getMaterial() == Material.WOOD && !blocksToChop.contains(scanPos)) {
				addTreeBlocks(state, scanPos);
			} else if (isUnwantedPlant(block)) {
				blocksToPlant.add(scanPos);
			}
		}
	}

	private boolean canFertilize(World world, BlockPos pos, IBlockState state) {
		return state.getBlock() instanceof IGrowable && ((IGrowable) state.getBlock()).canGrow(world, pos, state, world.isRemote);
	}

	private boolean isUnwantedPlant(Block block) {
		return block instanceof BlockBush && !TreeFarmRegistry.isPlantable(block);
	}

	@Override
	protected boolean hasWorksiteWork() {
		return (hasShears && !blocksToShear.isEmpty()) || !blocksToChop.isEmpty() || (bonemealCount > 0 && !blocksToFertilize
				.isEmpty()) || (plantableCount > 0 && !blocksToPlant.isEmpty());
	}
}
