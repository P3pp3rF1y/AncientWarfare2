package net.shadowmage.ancientwarfare.automation.tile.worksite;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.automation.AncientWarfareAutomation;
import net.shadowmage.ancientwarfare.automation.chunkloader.AWChunkLoader;
import net.shadowmage.ancientwarfare.core.interfaces.IBoundedSite;
import net.shadowmage.ancientwarfare.core.interfaces.IChunkLoaderTile;
import net.shadowmage.ancientwarfare.core.upgrade.WorksiteUpgrade;
import net.shadowmage.ancientwarfare.core.util.BlockTools;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Set;

public abstract class TileWorksiteBounded extends TileWorksiteBase implements IBoundedSite, IChunkLoaderTile {

	private static final String BB_MIN_TAG = "bbMin";
	private static final String BB_MAX_TAG = "bbMax";
	/*
			 * minimum position of the work area bounding box, or a single block position if bbMax is not set
			 * must not be null if this block has a work-area
			 */
	private BlockPos bbMin = BlockPos.ORIGIN;

	/*
	 * maximum position of the work bounding box.  May be null
	 */
	private BlockPos bbMax = BlockPos.ORIGIN;

	private ForgeChunkManager.Ticket chunkTicket = null;

	@Override
	public Set<WorksiteUpgrade> getValidUpgrades() {
		return EnumSet.of(WorksiteUpgrade.ENCHANTED_TOOLS_1, WorksiteUpgrade.ENCHANTED_TOOLS_2, WorksiteUpgrade.SIZE_MEDIUM, WorksiteUpgrade.SIZE_LARGE,
				WorksiteUpgrade.TOOL_QUALITY_1, WorksiteUpgrade.TOOL_QUALITY_2, WorksiteUpgrade.TOOL_QUALITY_3, WorksiteUpgrade.BASIC_CHUNK_LOADER);
	}

	@Override
	public void onBlockBroken(IBlockState state) {
		super.onBlockBroken(state);
		if (this.chunkTicket != null) {
			ForgeChunkManager.releaseTicket(chunkTicket);
			this.chunkTicket = null;
		}
	}

	@Override
	public void addUpgrade(WorksiteUpgrade upgrade) {
		super.addUpgrade(upgrade);
		if (upgrade == WorksiteUpgrade.BASIC_CHUNK_LOADER || upgrade == WorksiteUpgrade.QUARRY_CHUNK_LOADER) {
			setupInitialTicket();//setup chunkloading for the worksite
		}
	}

	@Override
	public final void removeUpgrade(WorksiteUpgrade upgrade) {
		super.removeUpgrade(upgrade);
		if (upgrade == WorksiteUpgrade.BASIC_CHUNK_LOADER || upgrade == WorksiteUpgrade.QUARRY_CHUNK_LOADER) {
			setTicket(null);//release any existing ticket
		}
	}

	@Override
	public final boolean hasWorkBounds() {
		return !bbMin.equals(BlockPos.ORIGIN) && !bbMax.equals(BlockPos.ORIGIN);
	}

	@Override
	public final BlockPos getWorkBoundsMin() {
		return bbMin;
	}

	@Override
	public final BlockPos getWorkBoundsMax() {
		return bbMax;
	}

	@Override
	public final void setBounds(BlockPos min, BlockPos max) {
		setWorkBoundsMin(BlockTools.getMin(min, max));
		setWorkBoundsMax(BlockTools.getMax(min, max));
		onBoundsSet();
	}

	@Override
	public int getBoundsMaxWidth() {
		return getUpgrades().contains(WorksiteUpgrade.SIZE_LARGE) ? 16 : getUpgrades().contains(WorksiteUpgrade.SIZE_MEDIUM) ? 9 : 5;
	}

	@Override
	public int getBoundsMaxHeight() {
		return 1;
	}

	@Override
	public final void setTicket(@Nullable ForgeChunkManager.Ticket tk) {
		if (chunkTicket != null) {
			ForgeChunkManager.releaseTicket(chunkTicket);
			chunkTicket = null;
		}
		this.chunkTicket = tk;
		if (this.chunkTicket == null) {
			return;
		}
		AWChunkLoader.INSTANCE.writeDataToTicket(chunkTicket, pos);
		ChunkPos ccip = new ChunkPos(pos);
		ForgeChunkManager.forceChunk(chunkTicket, ccip);
		if (hasWorkBounds()) {
			int minX = getWorkBoundsMin().getX() >> 4;
			int minZ = getWorkBoundsMin().getZ() >> 4;
			int maxX = getWorkBoundsMax().getX() >> 4;
			int maxZ = getWorkBoundsMax().getZ() >> 4;
			for (int x = minX; x <= maxX; x++) {
				for (int z = minZ; z <= maxZ; z++) {
					ccip = new ChunkPos(x, z);
					ForgeChunkManager.forceChunk(chunkTicket, ccip);
				}
			}
		}
	}

	private void setupInitialTicket() {
		if (chunkTicket != null) {
			ForgeChunkManager.releaseTicket(chunkTicket);
		}
		if (getUpgrades().contains(WorksiteUpgrade.BASIC_CHUNK_LOADER) || getUpgrades().contains(WorksiteUpgrade.QUARRY_CHUNK_LOADER)) {
			setTicket(ForgeChunkManager.requestTicket(AncientWarfareAutomation.instance, world, ForgeChunkManager.Type.NORMAL));
		}
	}

	/*
	 * Used by user-set-blocks tile to set all default harvest-checks to true when bounds are FIRST set
	 */
	protected void onBoundsSet() {

	}

	@Override
	public void onBoundsAdjusted() {
		//TODO implement to check target blocks, clear invalid ones
	}

	@Override
	public void onPostBoundsAdjusted() {
		setupInitialTicket();
	}

	boolean isInBounds(BlockPos pos) {
		return pos.getX() >= bbMin.getX() && pos.getX() <= bbMax.getX() && pos.getZ() >= bbMin.getZ() && pos.getZ() <= bbMax.getZ();
	}

	protected void validateCollection(Collection<BlockPos> blocks) {
		if (!hasWorkBounds()) {
			blocks.clear();
			return;
		}
		Iterator<BlockPos> it = blocks.iterator();
		BlockPos pos;
		while (it.hasNext() && (pos = it.next()) != null) {
			if (!isInBounds(pos)) {
				it.remove();
			}
		}
	}

	@Override
	public final void setWorkBoundsMin(BlockPos min) {
		if (min != bbMin) {
			bbMin = min;
			markDirty();
		}
	}

	@Override
	public final void setWorkBoundsMax(BlockPos max) {
		if (max != bbMax) {
			bbMax = max;
			markDirty();
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox() {
		if (hasWorkBounds()) {
			BlockPos min = getWorkBoundsMin();
			int minX = Math.min(min.getX(), pos.getX());
			int minY = Math.min(min.getY(), pos.getY());
			int minZ = Math.min(min.getZ(), pos.getZ());
			BlockPos max = getWorkBoundsMax();
			int maxX = Math.max(max.getX() + 1, pos.getX() + 1);
			int maxY = Math.max(max.getY() + 1, pos.getY() + 1);
			int maxZ = Math.max(max.getZ() + 1, pos.getZ() + 1);

			return new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
		}
		return super.getRenderBoundingBox();
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		if (tag.hasKey(BB_MIN_TAG)) {
			bbMin = BlockPos.fromLong(tag.getLong(BB_MIN_TAG));
		}
		if (tag.hasKey(BB_MAX_TAG)) {
			bbMax = BlockPos.fromLong(tag.getLong(BB_MAX_TAG));
		}
		if (bbMax == BlockPos.ORIGIN) {
			setWorkBoundsMax(pos.add(0, 0, 1));
		}
		if (bbMin == BlockPos.ORIGIN) {
			setWorkBoundsMin(pos.add(0, 0, 1));
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		if (bbMin != BlockPos.ORIGIN) {
			tag.setLong(BB_MIN_TAG, bbMin.toLong());
		}
		if (bbMax != BlockPos.ORIGIN) {
			tag.setLong(BB_MAX_TAG, bbMax.toLong());
		}

		return tag;
	}

	@Override
	protected void writeUpdateNBT(NBTTagCompound tag) {
		super.writeUpdateNBT(tag);
		if (bbMin != BlockPos.ORIGIN) {
			tag.setLong(BB_MIN_TAG, bbMin.toLong());
		}
		if (bbMax != BlockPos.ORIGIN) {
			tag.setLong(BB_MAX_TAG, bbMax.toLong());
		}
	}

	@Override
	protected void handleUpdateNBT(NBTTagCompound tag) {
		super.handleUpdateNBT(tag);
		if (tag.hasKey(BB_MIN_TAG)) {
			bbMin = BlockPos.fromLong(tag.getLong(BB_MIN_TAG));
		}
		if (tag.hasKey(BB_MAX_TAG)) {
			bbMax = BlockPos.fromLong(tag.getLong(BB_MAX_TAG));
		}
	}
}
