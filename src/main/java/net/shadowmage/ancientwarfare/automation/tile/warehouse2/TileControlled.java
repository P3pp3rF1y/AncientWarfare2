package net.shadowmage.ancientwarfare.automation.tile.warehouse2;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.core.util.WorldTools;

public abstract class TileControlled extends TileEntity implements IControlledTile {

    private boolean init;
    private IControllerTile controller;
    private BlockPosition controllerPosition;

    @Override
    public final boolean canUpdate() {
        return true;
    }

    @Override
    public final void updateEntity() {
        if (!init) {
            init = true;
            if (!loadController()) {
                searchForController();
            }
        }
        updateTile();
    }

    private boolean loadController() {
        BlockPosition pos = controllerPosition;
        controllerPosition = null;
        if (pos != null && controller != null) {
            TileEntity te = worldObj.getTileEntity(pos.x, pos.y, pos.z);
            if (te instanceof IControllerTile && isValidController((IControllerTile) te)) {
                ((IControllerTile) te).addControlledTile(this);
            }
        }
        return controller != null;
    }

    protected abstract void updateTile();

    protected void searchForController() {
        BlockPosition pos = getPosition();
        BlockPosition min = pos.copy().offset(-16, -4, -16);
        BlockPosition max = pos.copy().offset(16, 4, 16);
        for (TileEntity te : WorldTools.getTileEntitiesInArea(worldObj, min.x, min.y, min.z, max.x, max.y, max.z)) {
            if (te instanceof IControllerTile) {
                if (isValidController((IControllerTile)te)) {
                    ((IControllerTile)te).addControlledTile(this);
                    break;
                }
            }
        }
    }

    protected boolean isValidController(IControllerTile tile) {
        return tile instanceof TileWarehouseBase && BlockTools.isPositionWithinBounds(getPosition(), ((TileWarehouseBase) tile).getWorkBoundsMin(), ((TileWarehouseBase) tile).getWorkBoundsMax());
    }

    @Override
    public final void invalidate() {
        if (controller != null) {
            controller.removeControlledTile(this);
        }
        controller = null;
        init = false;
        super.invalidate();
    }

    @Override
    public final void validate() {
        if (controller != null) {
            controller.removeControlledTile(this);
        }
        controller = null;
        init = false;
        super.validate();
    }

    @Override
    public final void setController(IControllerTile tile) {
        this.controller = tile;
        this.controllerPosition = tile == null ? null : tile.getPosition();
    }

    @Override
    public final IControllerTile getController() {
        return controller;
    }

    @Override
    public final BlockPosition getPosition() {
        return new BlockPosition(xCoord, yCoord, zCoord);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        if (tag.hasKey("controllerPosition")) {
            controllerPosition = new BlockPosition(tag.getCompoundTag("controllerPosition"));
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        if (controllerPosition != null) {
            tag.setTag("controllerPosition", controllerPosition.writeToNBT(new NBTTagCompound()));
        }
    }

}
