package net.shadowmage.ancientwarfare.automation.tile.worksite;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraftforge.common.ForgeChunkManager;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.upgrade.WorksiteUpgrade;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;

public final class WorkSiteQuarry extends TileWorksiteBoundedInventory {
	private boolean finished;
	private boolean hasDoneInit = false;
	public int height;

	/*
	 * Current position within work bounds.
	 * Incremented when work is processed.
	 */
	private BlockPos current = BlockPos.ORIGIN;//position within bounds that is the 'active' position
	private BlockPos validate = BlockPos.ORIGIN;

	public WorkSiteQuarry() {
		super();
	}

	@Override
	public boolean userAdjustableBlocks() {
		return false;
	}

	@Override
	protected void onBoundsSet() {
		super.onBoundsSet();
		height = pos.getY();
		offsetBounds();
	}

	private void offsetBounds() {
		BlockPos boundsMax = getWorkBoundsMax();
		setWorkBoundsMax(boundsMax.up(pos.getY() - 1 - boundsMax.getY()));
		boundsMax = getWorkBoundsMin();
		setWorkBoundsMin(boundsMax.up(pos.getY() - height - boundsMax.getY()));
		BlockTools.notifyBlockUpdate(this);
	}

	@Override
	public void onBoundsAdjusted() {
		offsetBounds();
		current = new BlockPos(getWorkBoundsMin().getX(), getWorkBoundsMax().getY(), getWorkBoundsMin().getZ());
		validate = current;
	}

	@Override
	public Set<WorksiteUpgrade> getValidUpgrades() {
		return EnumSet.of(WorksiteUpgrade.ENCHANTED_TOOLS_1, WorksiteUpgrade.ENCHANTED_TOOLS_2, WorksiteUpgrade.QUARRY_MEDIUM, WorksiteUpgrade.QUARRY_LARGE, WorksiteUpgrade.TOOL_QUALITY_1, WorksiteUpgrade.TOOL_QUALITY_2, WorksiteUpgrade.TOOL_QUALITY_3, WorksiteUpgrade.QUARRY_CHUNK_LOADER);
	}

	@Override
	public int getBoundsMaxWidth() {
		if (getUpgrades().contains(WorksiteUpgrade.QUARRY_LARGE)) {
			return 64;
		}
		return getUpgrades().contains(WorksiteUpgrade.QUARRY_MEDIUM) ? 32 : 16;
	}

	@Override
	protected void updateWorksite() {
		if (!hasDoneInit) {
			initWorkSite();
			hasDoneInit = true;
		}
	}

	@Override
	public void addUpgrade(WorksiteUpgrade upgrade) {
		super.addUpgrade(upgrade);
		current = new BlockPos(getWorkBoundsMin().getX(), getWorkBoundsMax().getY(), getWorkBoundsMin().getZ());
		validate = current;
		this.finished = false;
	}

	private static final IWorksiteAction DIG_ACTION = WorksiteImplementation::getEnergyPerActivation;

	@Override
	protected Optional<IWorksiteAction> getNextAction() {
		return !finished ? Optional.of(DIG_ACTION) : Optional.empty();
	}

	@Override
	protected boolean processAction(IWorksiteAction action) {
		if (!hasDoneInit) {
			initWorkSite();
			hasDoneInit = true;
		}
		/*
		 * while the current position is invalid, increment to a valid one. generally the incremental scan
		 * should have take care of this prior to processWork being called, but just in case...
		 */
		while (!canHarvest(current)) {
			if (!incrementPosition()) {
				/*
				 * if no valid position was found, set finished, exit
				 */
				finished = true;
				return false;
			}
		}
		/*
		 * if made it this far, a valid position was found, break it and add blocks to inventory
		 */
		return harvestBlock(current);
	}

	private boolean harvestBlock(BlockPos current) {
		IBlockState state = world.getBlockState(current);
		Block block = state.getBlock();
		NonNullList<ItemStack> stacks = NonNullList.create();

		block.getDrops(stacks, world, current, state, getFortune());

		if (!InventoryTools.insertItems(mainInventory, stacks, true).isEmpty()) {
			return false;
		}

		if (!BlockTools.breakBlockNoDrops(world, current, state)) {
			return false;
		}

		InventoryTools.insertOrDropItems(mainInventory, stacks, world, current);

		return true;
	}

	private boolean incrementPosition() {
		if (finished) {
			return false;
		}
		if (isMaxInChunk(current.getX()) || current.getX() >= getWorkBoundsMax().getX()) {
			int startX = Math.max(getWorkBoundsMin().getX(), getMinChunkX());
			if (isMaxInChunk(current.getZ()) || current.getZ() >= getWorkBoundsMax().getZ()) {
				if (current.getY() <= (pos.getY() - (height + 1))) {
					return moveToStartOfNextChunk();
				} else {
					int startZ = Math.max(getWorkBoundsMin().getZ(), getMinChunkZ());
					current = new BlockPos(startX, current.getY() - 1, startZ);
				}
			} else {
				current = new BlockPos(startX, current.getY(), current.getZ() + 1);
			}
		} else {
			current = current.east();
		}
		return true;
	}

	private int getMinChunkZ() {
		return (current.getZ() >> 4) * 16;
	}

	private boolean moveToStartOfNextChunk() {
		unforceNonMachineChunk();

		int x = getMinChunkX() + 16;
		int z = Math.max(getWorkBoundsMin().getZ(), getMinChunkZ());
		if (x > getWorkBoundsMax().getX()) {
			x = getWorkBoundsMin().getX();
			z = getMinChunkZ() + 16;
			if (z > getWorkBoundsMax().getZ()) {
				return false;
			}
		}
		current = new BlockPos(x, getWorkBoundsMax().getY(), z);

		chunkLoadWorkBounds();

		return true;
	}

	private void unforceNonMachineChunk() {
		if(!hasChunkLoaderUpgrade()) {
			return;
		}

		ChunkPos currentChunk = new ChunkPos(current);
		//unload chunk only if not same as machine chunk
		if (!currentChunk.equals(new ChunkPos(pos))) {
			ForgeChunkManager.unforceChunk(chunkTicket, new ChunkPos(current));
		}
	}

	@Override
	protected void chunkLoadWorkBounds() {
		if(!hasChunkLoaderUpgrade()) {
			return;
		}

		ForgeChunkManager.forceChunk(chunkTicket, new ChunkPos(current));
	}

	private int getMinChunkX() {
		return (current.getX() >> 4) * 16;
	}

	private boolean isMaxInChunk(int coord) {
		return (coord & 15) == 15;
	}

	private boolean canHarvest(BlockPos harvestPos) {
		IBlockState state = world.getBlockState(harvestPos);
		Block block = state.getBlock();
		if (world.isAirBlock(harvestPos) || state.getMaterial().isLiquid()) {
			return false;
		}
		int harvestLevel = block.getHarvestLevel(state);
		if (harvestLevel >= 2) {
			int toolLevel = 1;
			if (getUpgrades().contains(WorksiteUpgrade.TOOL_QUALITY_3)) {
				toolLevel = Integer.MAX_VALUE;
			} else if (getUpgrades().contains(WorksiteUpgrade.TOOL_QUALITY_2)) {
				toolLevel = 3;
			} else if (getUpgrades().contains(WorksiteUpgrade.TOOL_QUALITY_1)) {
				toolLevel = 2;
			}
			if (toolLevel < harvestLevel) {
				return false;
			}//else is harvestable, check the rest of the checks
		}
		return state.getBlockHardness(world, harvestPos) >= 0;
	}

	private void initWorkSite() {
		BlockPos boundsMin = getWorkBoundsMin();
		setWorkBoundsMin(boundsMin.up(pos.getY() - height - boundsMin.getY()));
		current = new BlockPos(getWorkBoundsMin().getX(), getWorkBoundsMax().getY(), getWorkBoundsMin().getZ());
		validate = current;
		BlockTools.notifyBlockUpdate(this);//resend work-bounds change
	}

	@Override
	public WorkType getWorkType() {
		return WorkType.MINING;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		current = BlockPos.fromLong(tag.getLong("current"));
		validate = BlockPos.fromLong(tag.getLong("validate"));
		finished = tag.getBoolean("finished");
		hasDoneInit = tag.getBoolean("init");
		height = tag.getInteger("height");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setLong("current", current.toLong());
		tag.setLong("validate", validate.toLong());
		tag.setBoolean("finished", finished);
		tag.setBoolean("init", hasDoneInit);
		tag.setInteger("height", height);
		return tag;
	}

	@Override
	public boolean onBlockClicked(EntityPlayer player, @Nullable EnumHand hand) {
		if (!player.world.isRemote) {
			NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_WORKSITE_QUARRY, pos);
		}
		return true;
	}
}
