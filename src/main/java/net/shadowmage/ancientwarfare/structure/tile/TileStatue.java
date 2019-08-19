package net.shadowmage.ancientwarfare.structure.tile;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler;
import net.shadowmage.ancientwarfare.core.tile.TileUpdatable;
import net.shadowmage.ancientwarfare.core.util.Trig;

public class TileStatue extends TileUpdatable implements BlockRotationHandler.IRotatableTile {
	private EntityStatueInfo entityStatueInfo = new EntityStatueInfo();
	private EnumFacing facing;

	public TileStatue() {
		entityStatueInfo.setRenderType(EntityStatueInfo.RenderType.MODEL);
	}

	public EntityStatueInfo getEntityStatueInfo() {
		return entityStatueInfo;
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return new AxisAlignedBB(pos.add(-2, -2, -2), pos.add(3, 3,3));
	}

	@Override
	public EnumFacing getPrimaryFacing() {
		return facing;
	}

	@Override
	public void setPrimaryFacing(EnumFacing facing) {
		this.facing = facing;
	}

	@Override
	protected void writeUpdateNBT(NBTTagCompound tag) {
		super.writeUpdateNBT(tag);
		writeNBT(tag);
	}

	@Override
	protected void handleUpdateNBT(NBTTagCompound tag) {
		super.handleUpdateNBT(tag);
		readNBT(tag);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		readNBT(compound);
	}

	private void readNBT(NBTTagCompound compound) {
		facing = EnumFacing.getHorizontal(compound.getByte("facing"));
		entityStatueInfo.deserializeNBT(compound.getCompoundTag("entityStatueInfo"));
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		return writeNBT(super.writeToNBT(compound));
	}

	private NBTTagCompound writeNBT(NBTTagCompound ret) {
		ret.setByte("facing", (byte) facing.getHorizontalIndex());
		ret.setTag("entityStatueInfo", entityStatueInfo.serializeNBT(new NBTTagCompound()));
		return ret;
	}
}
