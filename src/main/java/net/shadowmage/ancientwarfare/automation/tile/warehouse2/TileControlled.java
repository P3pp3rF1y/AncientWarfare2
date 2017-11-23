package net.shadowmage.ancientwarfare.automation.tile.warehouse2;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.shadowmage.ancientwarfare.core.tile.TileUpdatable;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.core.util.WorldTools;

import javax.annotation.Nullable;

public abstract class TileControlled extends TileUpdatable implements IControlledTile, ITickable {

    private boolean init;
    private IControllerTile controller;
    private BlockPos controllerPosition;

    @Override
    public final void update() {
        if (!init) {
            init = true;
            if (!loadController()) {
                searchForController();
            }
        }
        updateTile();
    }

    private boolean loadController() {
        BlockPos pos = controllerPosition;
        controllerPosition = null;
        if (pos != null && controller == null) {
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof IControllerTile && isValidController((IControllerTile) te)) {
                ((IControllerTile) te).addControlledTile(this);
            }
        }
        return controller != null;
    }

    protected abstract void updateTile();

    protected void searchForController() {
        BlockPos min = pos.add(-16, -4, -16);
        BlockPos max = pos.add(16, 4, 16);
        for (TileEntity te : WorldTools.getTileEntitiesInArea(world, min.getX(), min.getY(), min.getZ(), max.getX(), max.getY(), max.getZ())) {
            if (te instanceof IControllerTile) {
                if (isValidController((IControllerTile)te)) {
                    ((IControllerTile)te).addControlledTile(this);
                    break;
                }
            }
        }
    }

    protected boolean isValidController(IControllerTile tile) {
        return tile instanceof TileWarehouseBase && BlockTools.isPositionWithinBounds(getPos(), ((TileWarehouseBase) tile).getWorkBoundsMin(), ((TileWarehouseBase) tile).getWorkBoundsMax());
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
        super.validate();
        if (controller != null) {
            controller.addControlledTile(this);
        }
    }

    @Override
    public final void setController(IControllerTile tile) {
        this.controller = tile;
        this.controllerPosition = tile == null ? null : tile.getPosisition();
    }

    @Nullable
    @Override
    public final IControllerTile getController() {
        return controller;
    }

    @Override
    public BlockPos getPosition() {
        return getPos();
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        if (tag.hasKey("controllerPosition")) {
            controllerPosition = BlockPos.fromLong(tag.getLong("controllerPosition"));
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        if (controllerPosition != null) {
            tag.setLong("controllerPosition", controllerPosition.toLong());
        }
        return tag;
    }

    @Override
    public final boolean equals(Object obj) {
        if (this == obj)
            return true;
        return obj instanceof TileControlled && this.world == ((TileControlled) obj).getWorld() && this.getPos().equals(((TileControlled) obj).getPos());
    }

    @Override
    public final int hashCode() {
        return this.getPos().hashCode();
    }
}
