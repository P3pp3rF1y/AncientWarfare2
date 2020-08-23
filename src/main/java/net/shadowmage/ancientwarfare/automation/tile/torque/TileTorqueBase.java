package net.shadowmage.ancientwarfare.automation.tile.torque;

import cofh.redstoneflux.api.IEnergyReceiver;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional;
import net.shadowmage.ancientwarfare.automation.config.AWAutomationStatics;
import net.shadowmage.ancientwarfare.automation.proxy.RFProxy;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.IRotatableTile;
import net.shadowmage.ancientwarfare.core.interfaces.IInteractableTile;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque.ITorqueTile;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque.TorqueCell;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.network.PacketBlockEvent;
import net.shadowmage.ancientwarfare.core.tile.TileUpdatable;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.core.util.WorldTools;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Optional.Interface(iface = "cofh.redstoneflux.api.IEnergyReceiver", modid = "redstoneflux", striprefs = true)
public abstract class TileTorqueBase extends TileUpdatable implements ITorqueTile, IInteractableTile, IRotatableTile, IEnergyReceiver, ITickable {

	public static final int DIRECTION_LENGTH = EnumFacing.VALUES.length;
	private static final String ORIENTATION_TAG = "orientation";
	/*
	 * The primary facing direction for this tile.  Default to north for uninitialized tiles (null is not a valid value)
	 */
	protected EnumFacing orientation = EnumFacing.NORTH;

	/*
	 * used by server to limit packet sending<br>
	 * used by client for lerp-ticks for lerping between power states
	 */
	protected int networkUpdateTicks;

	private TileEntity[] rfCache;//cannot reference interface directly, but can cast directly...//only used when cofh installed
	private ITorqueTile[] torqueCache;

	/*
	 * helper vars to be used by tiles during updating, to cache in/out/loss values<br>
	 * IMPORTANT: should NOT be relied upon for calculations, only for use for display purposes<br>
	 * E.G. A tile may choose to -not- update these vars.<br>
	 * However, best effort should be made to update these vars accurately.<br><br>
	 * Generated energy should be counted as 'in'<br>
	 * Any directly output energy should be counted as 'out'<br>
	 * Only direct power loss from transmission efficiency or per-tick loss should be counted for 'loss'
	 */
	protected double torqueIn;
	protected double torqueOut;
	protected double torqueLoss;
	protected double prevEnergy;

	//************************************** COFH RF METHODS ***************************************//
	@Optional.Method(modid = "redstoneflux")
	@Override
	public final int getEnergyStored(EnumFacing from) {
		return (int) (getTorqueStored(from) * AWAutomationStatics.torqueToRf);
	}

	@Optional.Method(modid = "redstoneflux")
	@Override
	public final int getMaxEnergyStored(EnumFacing from) {
		return (int) (getMaxTorque(from) * AWAutomationStatics.torqueToRf);
	}

	@Optional.Method(modid = "redstoneflux")
	@Override
	public final boolean canConnectEnergy(EnumFacing from) {
		return canOutputTorque(from) || canInputTorque(from);
	}

	@Optional.Method(modid = "redstoneflux")
	@Override
	public final int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
		if (!canInputTorque(from)) {
			return 0;
		}
		if (simulate) {
			return Math.min(maxReceive, (int) (AWAutomationStatics.torqueToRf * getMaxTorqueInput(from)));
		}
		return (int) (AWAutomationStatics.torqueToRf * addTorque(from, (double) maxReceive * AWAutomationStatics.rfToTorque));
	}

	//************************************** NEIGHBOR CACHE UPDATING ***************************************//

	public final ITorqueTile[] getTorqueCache() {
		if (torqueCache == null) {
			buildTorqueCache();
		}
		return torqueCache;
	}

	public final TileEntity[] getRFCache() {
		if (rfCache == null) {
			buildRFCache();
		}
		return rfCache;
	}

	private void buildTorqueCache() {
		if (!hasWorld()) {
			throw new RuntimeException("Attempt to build neighbor cache on null world!!");
		}
		ITorqueTile[] torqueCache = new ITorqueTile[DIRECTION_LENGTH];
		EnumFacing dir;
		for (int i = 0; i < torqueCache.length; i++) {
			dir = EnumFacing.values()[i];
			if (!canOutputTorque(dir) && !canInputTorque(dir)) {
				continue;
			}
			BlockPos offsetPos = pos.offset(dir);
			if (!world.isBlockLoaded(offsetPos)) {
				continue;
			}
			final int index = i;
			WorldTools.getTile(world, offsetPos, ITorqueTile.class).ifPresent(t -> torqueCache[index] = t);
		}
		this.torqueCache = torqueCache;
	}

	private void buildRFCache() {
		TileEntity[] rfCache = new TileEntity[DIRECTION_LENGTH];
		EnumFacing dir;
		for (int i = 0; i < rfCache.length; i++) {
			dir = EnumFacing.values()[i];
			if (!canOutputTorque(dir) && !canInputTorque(dir)) {
				continue;
			}
			BlockPos offsetPos = pos.offset(dir);
			if (!world.isBlockLoaded(offsetPos)) {
				continue;
			}
			final EnumFacing direction = dir;
			WorldTools.getTile(world, offsetPos).filter(t -> RFProxy.instance.isRFTile(t)).ifPresent(t -> rfCache[direction.ordinal()] = t);
		}
		this.rfCache = rfCache;
	}

	@Override
	public void validate() {
		super.validate();
		invalidateTorqueCache();
	}

	@Override
	public void invalidate() {
		super.invalidate();
		invalidateTorqueCache();
	}

	public void onNeighborTileChanged() {
		invalidateTorqueCache();
	}

	private void invalidateTorqueCache() {
		torqueCache = null;
		rfCache = null;
		onNeighborCacheInvalidated();
	}

	protected void onNeighborCacheInvalidated() {

	}

	//************************************** generic stuff ***************************************//

	@Override
	public boolean hasFastRenderer() {
		return true;
	}

	@Override
	public final void setPrimaryFacing(EnumFacing d) {
		this.orientation = d;
		this.world.updateComparatorOutputLevel(pos, getBlockType());
		this.invalidateTorqueCache();
		BlockTools.notifyBlockUpdate(this);
	}

	@Override
	public final EnumFacing getPrimaryFacing() {
		return orientation;
	}

	@Override
	public boolean onBlockClicked(EntityPlayer player, @Nullable EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);
		if (stack.isEmpty()) {
			if (!world.isRemote) {
				player.sendMessage(new TextComponentTranslation("guistrings.automation.torque.values", String.format("%.2f", getTotalTorque()), String.format("%.2f", getTorqueIn()), String.format("%.2f", getTorqueOut()), String.format("%.2f", getTorqueLoss())));
			}
			return true;
		}
		return false;
	}

	//************************************** Utility Methods ***************************************//

	protected void updateRotation() {
		throw new UnsupportedOperationException();
	}

	protected void clientNetworkUpdate() {
		throw new UnsupportedOperationException();
	}

	protected void serverNetworkSynch() {
		throw new UnsupportedOperationException();
	}

	protected abstract void handleClientRotationData(EnumFacing side, int value);

	/*
	 * @return the TOTAL amount stored in the entire tile (not just one side), used by on-right-click functionality
	 */
	protected abstract double getTotalTorque();

	/*
	 * @return the total output of torque for the tick
	 */
	private double getTorqueOut() {
		return torqueOut;
	}

	/*
	 * @return the total input of torque for the tick
	 */
	private double getTorqueIn() {
		return torqueIn;
	}

	/*
	 * @return the total torque lost (destroyed, gone completely) for the tick
	 */
	private double getTorqueLoss() {
		return torqueLoss;
	}

	protected final void serverNetworkUpdate() {
		networkUpdateTicks--;
		if (networkUpdateTicks <= 0) {
			networkUpdateTicks = AWAutomationStatics.energyMinNetworkUpdateFrequency;
			serverNetworkSynch();
		}
	}

	protected void sendSideRotation(EnumFacing side, int value) {
		int valueBits = (value & 0xff);
		sendDataToClient(side.ordinal(), valueBits);
	}

	protected final double transferPowerTo(EnumFacing from) {
		double transferred = 0;
		ITorqueTile[] tc = getTorqueCache();
		if (tc[from.ordinal()] != null) {
			if (tc[from.ordinal()].canInputTorque(from.getOpposite())) {
				return drainTorque(from, tc[from.ordinal()].addTorque(from.getOpposite(), getMaxTorqueOutput(from)));
			}
		} else {
			if (Loader.isModLoaded("redstoneflux")) {
				transferred = RFProxy.instance.transferPower(this, from, getRFCache()[from.ordinal()]);
				if (transferred > 0) {
					return transferred;
				}
			}
		}
		return transferred;
	}

	protected final double applyPowerDrain(TorqueCell cell) {
		double e = cell.getEnergy();
		cell.setEnergy(e * cell.getEfficiency());
		return cell.getEnergy() - e;
	}

	protected final void sendDataToClient(int type, int data) {
		PacketBlockEvent pkt = new PacketBlockEvent(pos, getBlockType(), (short) type, (short) data);
		NetworkHandler.sendToAllTrackingChunk(world, pos.getX() >> 4, pos.getZ() >> 4, pkt);
	}

	protected final float getRenderRotation(double rotation, double lastRotationDiff, float partialTicks) {
		return (float) (rotation - (lastRotationDiff * (1 - partialTicks)));
	}

	@Override
	public boolean receiveClientEvent(int a, int b) {
		if (world.isRemote && a < DIRECTION_LENGTH) {
			networkUpdateTicks = AWAutomationStatics.energyMinNetworkUpdateFrequency;
			handleClientRotationData(EnumFacing.values()[a], b);
		}
		return true;
	}

	//************************************** NBT / DATA PACKET ***************************************//

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		orientation = EnumFacing.VALUES[tag.getInteger(ORIENTATION_TAG)];
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setInteger(ORIENTATION_TAG, orientation.ordinal());

		return tag;
	}

	@Override
	protected void writeUpdateNBT(NBTTagCompound tag) {
		super.writeUpdateNBT(tag);
		tag.setInteger(ORIENTATION_TAG, orientation.ordinal());
	}

	@Override
	protected void handleUpdateNBT(NBTTagCompound tag) {
		super.handleUpdateNBT(tag);
		orientation = EnumFacing.VALUES[tag.getInteger(ORIENTATION_TAG)];
		this.invalidateTorqueCache(); //TODO is this needed on client??
		BlockTools.notifyBlockUpdate(this);
	}
}
