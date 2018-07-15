package net.shadowmage.ancientwarfare.automation.tile.torque.multiblock;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.shadowmage.ancientwarfare.automation.config.AWAutomationStatics;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.network.PacketBlockEvent;
import net.shadowmage.ancientwarfare.core.tile.TileUpdatable;
import net.shadowmage.ancientwarfare.core.util.BlockFinder;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.core.util.Trig;
import net.shadowmage.ancientwarfare.core.util.WorldTools;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class TileFlywheelStorage extends TileUpdatable implements ITickable {

	private static final String CONTROLLER_POS_TAG = "controllerPos";
	private static final String IS_CONTROL_TAG = "isControl";
	private static final String SET_WIDTH_TAG = "setWidth";
	private static final String SET_HEIGHT_TAG = "setHeight";
	public BlockPos controllerPos;
	public boolean isControl = false;//set to true if this is the control block for a setup
	public int setWidth;
	public int setHeight;
	private int setCube;//validation params, only 'valid' in the control block.  used by rendering
	public double storedEnergy;
	public double maxEnergyStored;
	private double maxRpm = 100;
	public double torqueLoss;
	public double rotation;//used in rendering
	public double lastRotationDiff;
	private int clientEnergy;
	private int clientDestEnergy;
	private int networkUpdateTicks = 0;

	@Override
	public void update() {
		if (isControl) {
			if (world.isRemote) {
				clientNetworkUpdate();
			} else {
				serverNetworkUpdate();
				applyPowerLoss();
			}
		}
	}

	protected void clientNetworkUpdate() {
		updateRotation();
		if (networkUpdateTicks > 0) {
			int diff = clientDestEnergy - clientEnergy;
			clientEnergy += diff / networkUpdateTicks;
			networkUpdateTicks--;
		}
	}

	protected void applyPowerLoss() {
		double eff = 1.d - getEfficiency();
		eff *= 0.1d;
		torqueLoss = storedEnergy * eff;
		storedEnergy -= torqueLoss;
	}

	protected double getEfficiency() {
		int meta = getBlockMetadata();
		switch (meta) {
			case 0:
				return AWAutomationStatics.low_efficiency_factor;
			case 1:
				return AWAutomationStatics.med_efficiency_factor;
			case 2:
				return AWAutomationStatics.high_efficiency_factor;
			default:
				return AWAutomationStatics.low_efficiency_factor;
		}
	}

	private void serverNetworkUpdate() {
		if (!AWAutomationStatics.enable_energy_network_updates) {
			return;
		}
		networkUpdateTicks--;
		if (networkUpdateTicks <= 0) {
			double percentStored = storedEnergy / maxEnergyStored;
			int total = (int) (percentStored * 100.d);
			if (total != clientEnergy) {
				clientEnergy = total;
				sendDataToClient(1, clientEnergy);
			}
			networkUpdateTicks = AWAutomationStatics.energyMinNetworkUpdateFrequency;
		}
	}

	protected final void sendDataToClient(int type, int data) {
		PacketBlockEvent pkt = new PacketBlockEvent(pos, getBlockType(), (byte) type, (short) data);
		NetworkHandler.sendToAllTrackingChunk(world, pos.getX() >> 4, pos.getZ() >> 4, pkt);
	}

	@Override
	public boolean shouldRenderInPass(int pass) {
		return pass == 0 || pass == 1;
	}

	@Override
	public boolean receiveClientEvent(int a, int b) {
		if (world.isRemote && a == 1) {
			clientDestEnergy = b;
			networkUpdateTicks = AWAutomationStatics.energyMinNetworkUpdateFrequency;
		}
		return true;
	}

	private void updateRotation() {
		double rpm = (double) clientEnergy * 0.01d * this.maxRpm;
		lastRotationDiff = -(rpm * AWAutomationStatics.rpmToRpt) * Trig.TORADIANS;
		rotation += lastRotationDiff;
		rotation %= Trig.PI * 2;
	}

	public void blockBroken() {
		informNeighborsToValidate();
	}

	public final void blockPlaced() {
		validateSetup();
	}

	private void setController(@Nullable BlockPos controllerPos) {
		this.controllerPos = controllerPos;
		isControl = (controllerPos == null || controllerPos.equals(pos));
		if (controllerPos == null) {
			setWidth = 1;
			setHeight = 1;
			setCube = 1;
		}

		markDirty();
		BlockTools.notifyBlockUpdate(this);
	}

	private boolean validateSetup() {
		BlockFinder finder = new BlockFinder(world, getBlockType(), getBlockMetadata(), 30);
		Pair<BlockPos, BlockPos> corners = finder.cross(pos);
		int minX = corners.getLeft().getX();
		int minY = corners.getLeft().getY();
		int minZ = corners.getLeft().getZ();
		int w = corners.getRight().getX() - minX + 1;
		int h = corners.getRight().getY() - minY + 1;
		int l = corners.getRight().getZ() - minZ + 1;
		boolean valid = w == l && (w == 1 || w == 3) && finder.box(corners);
		if (valid) {
			int cx = w == 1 ? minX : minX + 1;
			int cz = l == 1 ? minZ : minZ + 1;
			setValidSetup(finder.getPositions(), cx, minY, cz, w, h, getBlockMetadata());
		} else {
			setInvalidSetup(finder.getAllConnectedBlocks(pos));
		}
		return valid;
	}

	private void setValidSetup(List<BlockPos> set, int cx, int cy, int cz, int size, int height, int type) {
		controllerPos = new BlockPos(cx, cy, cz);
		Optional<TileFlywheelStorage> te = WorldTools.getTile(world, controllerPos, TileFlywheelStorage.class);
		if (te.isPresent()) {
			te.get().setAsController(size, height, type);
			for (BlockPos pos : set) {
				WorldTools.getTile(world, pos, TileFlywheelStorage.class).ifPresent(t -> t.setController(controllerPos));
			}
		} else {
			controllerPos = null;
		}
	}

	private void setAsController(int size, int height, int type) {
		this.isControl = true;
		this.setWidth = size;
		this.setHeight = height;
		this.setCube = size * size * height;
		double energyPerBlockForType = 1600;
		switch (type) {
			case 0:
				energyPerBlockForType = AWAutomationStatics.low_storage_energy_max;
				break;
			case 1:
				energyPerBlockForType = AWAutomationStatics.med_storage_energy_max;
				break;
			case 2:
				energyPerBlockForType = AWAutomationStatics.high_storage_energy_max;
				break;
		}
		this.maxEnergyStored = (double) setCube * energyPerBlockForType;
		markDirty();
		BlockTools.notifyBlockUpdate(this);
	}

	private void setInvalidSetup(List<BlockPos> set) {
		setController(null);
		for (BlockPos pos : set) {
			WorldTools.getTile(world, pos, TileFlywheelStorage.class).ifPresent(t -> t.setController(null));
		}
	}

	private void informNeighborsToValidate() {
		for (EnumFacing d : EnumFacing.VALUES) {
			WorldTools.getTile(world, pos.offset(d), TileFlywheelStorage.class).ifPresent(TileFlywheelStorage::validateSetup);
		}
	}

	@Override
	protected void writeUpdateNBT(NBTTagCompound tag) {
		super.writeUpdateNBT(tag);
		if (controllerPos != null) {
			tag.setLong(CONTROLLER_POS_TAG, controllerPos.toLong());
		}
		if (isControl) {
			tag.setBoolean(IS_CONTROL_TAG, true);
			tag.setInteger(SET_WIDTH_TAG, setWidth);
			tag.setInteger(SET_HEIGHT_TAG, setHeight);
			tag.setInteger("clientEnergy", clientEnergy);
		}
	}

	@Override
	protected void handleUpdateNBT(NBTTagCompound tag) {
		super.handleUpdateNBT(tag);
		controllerPos = tag.hasKey(CONTROLLER_POS_TAG) ? BlockPos.fromLong(tag.getLong(CONTROLLER_POS_TAG)) : null;
		isControl = tag.getBoolean(IS_CONTROL_TAG);
		if (isControl) {
			setHeight = tag.getInteger(SET_HEIGHT_TAG);
			setWidth = tag.getInteger(SET_WIDTH_TAG);
			setCube = setWidth * setWidth * setHeight;
			clientEnergy = tag.getInteger("clientEnergy");
		}
		BlockTools.notifyBlockUpdate(this);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		controllerPos = tag.hasKey(CONTROLLER_POS_TAG) ? BlockPos.fromLong(tag.getLong(CONTROLLER_POS_TAG)) : null;
		isControl = tag.getBoolean(IS_CONTROL_TAG);
		if (isControl) {
			maxEnergyStored = tag.getDouble("maxEnergy");
			setHeight = tag.getInteger(SET_HEIGHT_TAG);
			setWidth = tag.getInteger(SET_WIDTH_TAG);
			setCube = setWidth * setWidth * setHeight;
		}
		storedEnergy = tag.getDouble("storedEnergy");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		if (controllerPos != null) {
			tag.setLong(CONTROLLER_POS_TAG, controllerPos.toLong());
		}
		if (isControl) {
			tag.setBoolean(IS_CONTROL_TAG, true);
			tag.setDouble("maxEnergy", maxEnergyStored);
			tag.setInteger(SET_WIDTH_TAG, setWidth);
			tag.setInteger(SET_HEIGHT_TAG, setHeight);
		}
		tag.setDouble("storedEnergy", storedEnergy);
		return tag;
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return new AxisAlignedBB(pos.getX() - 1, pos.getY() - 1, pos.getZ() - 1, pos.getX() + 2, pos.getY() + setHeight, pos.getZ() + 2);
	}

}
