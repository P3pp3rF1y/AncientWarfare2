package net.shadowmage.ancientwarfare.automation.tile.torque;

import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.shadowmage.ancientwarfare.automation.config.AWAutomationStatics;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque.TorqueCell;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.core.util.Trig;

public class TileWaterwheelGenerator extends TileTorqueSingleCell {

	public float wheelRotation;
	public float lastWheelRotationDiff;

	private float rotTick;
	private byte rotationDirection = 1; //TODO get rid of magic numbers used for this

	private int updateTick;

	public boolean validSetup = false;

	public TileWaterwheelGenerator() {
		torqueCell = new TorqueCell(0, 4, AWAutomationStatics.low_transfer_max, AWAutomationStatics.low_efficiency_factor);
		float maxWheelRpm = 20;
		rotTick = maxWheelRpm * AWAutomationStatics.rpmToRpt;
	}

	@Override
	public void update() {
		super.update();
		if (!world.isRemote) {
			updateTick--;
			if (updateTick <= 0) {
				updateTick = 20;
				boolean valid = validateBlocks();
				if (valid != validSetup) {
					validSetup = valid;
					BlockTools.notifyBlockUpdate(this);
				}
			}
			if (validSetup)//server, update power gen
			{
				torqueCell.setEnergy(torqueCell.getEnergy() + AWAutomationStatics.waterwheel_generator_output);
			}
		}
	}

	@Override
	protected void updateRotation() {
		super.updateRotation();
		if (validSetup) {
			lastWheelRotationDiff = (rotTick * (float) rotationDirection) * Trig.TORADIANS;
			wheelRotation += lastWheelRotationDiff;
			wheelRotation %= Trig.PI * 2;
		}
	}

	@Override
	protected void handleUpdateNBT(NBTTagCompound tag) {
		super.handleUpdateNBT(tag);
		validSetup = tag.getBoolean("validSetup");
		rotationDirection = tag.getByte("rotationDirection");
	}

	@Override
	protected void writeUpdateNBT(NBTTagCompound tag) {
		super.writeUpdateNBT(tag);
		tag.setBoolean("validSetup", validSetup);
		tag.setByte("rotationDirection", rotationDirection);
	}

	private boolean validateBlocks() {
		EnumFacing d = orientation.getOpposite();
		BlockPos innerPos = pos.offset(d);
		//quick check for invalid setup
		//must have air inside the inner two blocks
		if (getValidationType(innerPos.up()) != 1 || getValidationType(innerPos) != 1)
			return false;
		BlockPos right = innerPos.offset(d.rotateY());
		BlockPos left = innerPos.offset(d.rotateYCCW());

		byte[] validationGrid = new byte[6];
		validationGrid[0] = getValidationType(left.up());
		validationGrid[1] = getValidationType(right.up());
		validationGrid[2] = getValidationType(left);
		validationGrid[3] = getValidationType(right);
		validationGrid[4] = getValidationType(left.down());
		validationGrid[5] = getValidationType(right.down());
		for (byte aValue : validationGrid) {
			if (aValue == 0) {
				return false;
			}
		}
		if (validationGrid[2] == 2 && validationGrid[4] == 2)//left side water flowing down
		{
			//check opposite side for air (underneath has already been checked by quick block check above)
			if (validationGrid[1] == 1 && validationGrid[3] == 1) {
				rotationDirection = -1;//TODO check if these are correct rotation directions
				return true;
			}
			return false;
		} else if (validationGrid[3] == 2 && validationGrid[5] == 2)//right side water flowing down
		{
			//check opposite side for air (underneath has already been checked by quick block check above)
			if (validationGrid[0] == 1 && validationGrid[2] == 1) {
				rotationDirection = 1;
				return true;
			}
			return false;
		} else//not a direct flow downwards, check underneath for flow
		{
			if (validationGrid[4] != 2 || validationGrid[5] != 2 || getValidationType(innerPos.down()) != 2) {
				return false;
			}
			IBlockState stateLeft = world.getBlockState(left.down());
			IBlockState stateRight = world.getBlockState(right.down());
			int levelLeft = stateLeft.getMaterial() != Material.WATER ? 0 : stateLeft.getValue(BlockLiquid.LEVEL);
			int levelRight = stateRight.getMaterial() != Material.WATER ? 0 : stateRight.getValue(BlockLiquid.LEVEL);
			rotationDirection = (byte) (levelLeft < levelRight ? -1 : levelRight < levelLeft ? 1 : 0);
			return true;
		}
	}

	private byte getValidationType(BlockPos pos) {
		if (world.isAirBlock(pos)) {
			return 1;
		}
		IBlockState state = world.getBlockState(pos);
		if (state.getMaterial() == Material.WATER) {
			return 2;
		}
		return 0;//TODO get rid of these magic numbers
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return new AxisAlignedBB(pos.getX() - 3, pos.getY() - 3, pos.getZ() - 3, pos.getX() + 4, pos.getY() + 4, pos.getZ() + 4);
	}

	@Override
	public float getClientOutputRotation(EnumFacing from, float delta) {
		if (from == orientation.getOpposite()) {
			return getRenderRotation(wheelRotation, lastWheelRotationDiff, delta);
		}
		return super.getClientOutputRotation(from, delta);
	}
}
