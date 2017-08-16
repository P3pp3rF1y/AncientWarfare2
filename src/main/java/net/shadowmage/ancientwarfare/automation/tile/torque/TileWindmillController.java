package net.shadowmage.ancientwarfare.automation.tile.torque;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.shadowmage.ancientwarfare.automation.config.AWAutomationStatics;
import net.shadowmage.ancientwarfare.automation.tile.torque.multiblock.TileWindmillBlade;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque.TorqueCell;

public class TileWindmillController extends TileTorqueSingleCell {

    public TileWindmillController() {
        double max = AWAutomationStatics.low_transfer_max;
        torqueCell = new TorqueCell(max, max, max, AWAutomationStatics.low_efficiency_factor);
    }

    @Override
    public void updateEntity() {
        super.updateEntity();
        if (!worldObj.isRemote) {
            TileWindmillBlade blade = getControlledBlade();
            if (blade != null && blade.energy > 0) {
                double d = blade.energy;
                blade.energy = 0;
                torqueCell.setEnergy(torqueCell.getEnergy() + d);
            }
        }
    }

    private TileWindmillBlade getControlledBlade() {
        TileEntity te;
        EnumFacing d = getPrimaryFacing().getOpposite();
        int x = xCoord + d.offsetX;
        int y = yCoord + d.offsetY;
        int z = zCoord + d.offsetZ;
        if (worldObj.blockExists(x, y, z) && (te = worldObj.getTileEntity(x, y, z)) instanceof TileWindmillBlade) {
            TileWindmillBlade blade = (TileWindmillBlade) te;
            if (blade.isControl) {
                return blade;
            }
        }
        return null;
    }

    @Override
    public boolean canInputTorque(EnumFacing from) {
        return false;
    }

}
